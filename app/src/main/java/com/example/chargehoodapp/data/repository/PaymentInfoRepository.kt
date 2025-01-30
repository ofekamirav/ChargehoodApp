package com.example.chargehoodapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.chargehoodapp.base.Constants.Collections.PAYMENT_INFO
import com.example.chargehoodapp.base.Constants.Collections.USERS
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.local.dao.PaymentInfoDao
import com.example.chargehoodapp.data.model.PaymentInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class PaymentInfoRepository(
    private val paymentInfoDao: PaymentInfoDao
) {

    private val firestore = FirebaseFirestore.getInstance()
    private val paymentInfoCollection = firestore.collection(PAYMENT_INFO)
    private val userUid = FirebaseModel.getCurrentUser()?.uid
    private val usersCollection = firestore.collection(USERS)

    companion object {
        private const val PAYMENT_LAST_UPDATE_KEY = "last_update_time_payment"
    }

    fun getPaymentMethods(): LiveData<List<PaymentInfo>> {
        Log.d("TAG", "PaymentInfoRepository-Fetching from Room for user: $userUid")
        return paymentInfoDao.getPaymentInfo(userUid ?: "")
    }


    suspend fun syncPaymentInfo() {
        withContext(Dispatchers.IO) {
            try {
                val lastUpdateTime = MyApplication.getLastUpdateTime(PAYMENT_LAST_UPDATE_KEY)

                val snapshot = paymentInfoCollection
                    .whereEqualTo("userId", userUid)
                    .whereGreaterThan("lastUpdated", lastUpdateTime)
                    .get()
                    .await()
                Log.d("TAG", "PaymentInfoRepository-Fetched from Firestore: ${snapshot.documents.map { it.data }}")

                val paymentInfoList = snapshot.documents.mapNotNull { doc ->
                    val payment = doc.toObject(PaymentInfo::class.java)
                    payment?.copy(id = doc.id)
                }

                if (paymentInfoList.isNotEmpty()) {
                    Log.d("TAG", "PaymentInfoRepository-Syncing ${paymentInfoList.size} PaymentInfo records from Firestore.")

                    paymentInfoDao.addPaymentInfoList(paymentInfoList)

                    val latestUpdateTime = paymentInfoList.maxOfOrNull { it.lastUpdated } ?: lastUpdateTime
                    MyApplication.saveLastUpdateTime(PAYMENT_LAST_UPDATE_KEY, latestUpdateTime)

                    //Get cards from ROOM after syncing
                    val updatedList = paymentInfoDao.getPaymentInfoSync(userUid ?: "")
                    Log.d("TAG", "PaymentInfoRepository-Updated Room records: $updatedList")
                }

                val ids = paymentInfoList.map { it.id }

                if (ids.isNotEmpty()) {
                    paymentInfoDao.deletePaymentInfoNotIn(ids)
                } else {
                    Log.d("TAG", "PaymentInfoRepository-Skipping deletion: No new payment info fetched from Firestore.")
                }


                Log.d("TAG", "PaymentInfoRepository-PaymentInfo synced successfully.")
            } catch (e: Exception) {
                Log.e("TAG", "PaymentInfoRepository-Error syncing PaymentInfo: ${e.message}")
            }
        }
    }


    suspend fun addPaymentInfo(paymentInfo: PaymentInfo) {
        withContext(Dispatchers.IO) {
            try {
                val documentRef = paymentInfoCollection.add(paymentInfo).await()
                val generatedId = documentRef.id
                Log.d("TAG", "PaymentInfoRepository-PaymentInfo added successfully with ID: $generatedId")

                val updatedPaymentInfo = paymentInfo.copy(id = generatedId)
                paymentInfoDao.addPaymentInfo(updatedPaymentInfo)

                paymentInfoCollection.document(generatedId).set(updatedPaymentInfo).await()

                usersCollection.document(userUid ?: "").update("hasPaymentInfo", true).await()

                syncPaymentInfo()

                Log.d("TAG", "PaymentInfoRepository-PaymentInfo synced successfully after addition.")
            } catch (e: Exception) {
                Log.e("TAG", "PaymentInfoRepository-Error adding PaymentInfo: ${e.message}")
            }
        }
    }


    fun getPaymentMethodsSync(): LiveData<List<PaymentInfo>> {
        return paymentInfoDao.getPaymentInfoSync(userUid ?: "")
    }


    suspend fun deletePaymentInfo(paymentInfo: PaymentInfo) {
        try {
            paymentInfoCollection.document(paymentInfo.id).delete().await()

            paymentInfoDao.deletePaymentInfoById(paymentInfo.id)

            val remainingPayments = paymentInfoDao.getPaymentInfo(userUid ?: "").value
            if (remainingPayments.isNullOrEmpty()) {
                usersCollection.document(userUid ?: "").update("hasPaymentInfo", false)
            }

            Log.d("TAG", "Payment deleted successfully")
        } catch (e: Exception) {
            Log.e("TAG", "Error deleting payment: ${e.message}")
        }
    }




}