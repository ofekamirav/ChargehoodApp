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
                    .get()
                    .await()

                Log.d("TAG", "PaymentInfoRepository-Firestore - Fetched ${snapshot.documents.size} documents")

                val paymentInfoList = snapshot.documents.mapNotNull { doc ->
                    val payment = doc.toObject(PaymentInfo::class.java)
                    payment?.copy(id = doc.id)
                }

                Log.d("TAG", "PaymentInfoRepository-Firestore - Converted to objects: $paymentInfoList")

                if (paymentInfoList.isNotEmpty()) {
                    paymentInfoDao.addPaymentInfoList(paymentInfoList)
                    val updatedList = paymentInfoDao.getPaymentInfoSync(userUid ?: "")
                    Log.d("TAG", "PaymentInfoRepository-Room - Saved to database: $updatedList")
                } else{
                    Log.d("TAG", "PaymentInfoRepository-Firestore - No documents found")
                }

            } catch (e: Exception) {
                Log.e("TAG", "PaymentInfoRepository-Error syncing PaymentInfo: ${e.message}")
            }
        }
    }



    suspend fun addPaymentInfo(paymentInfo: PaymentInfo) {
        withContext(Dispatchers.IO) {
            try {
                val documentRef= paymentInfoCollection.document()
                val generatedId = documentRef.id
                Log.d("TAG", "PaymentInfoRepository-PaymentInfo added successfully with ID: $generatedId")

                val updatedPaymentInfo = paymentInfo.copy(id = generatedId)
                documentRef.set(updatedPaymentInfo).await()
                paymentInfoDao.addPaymentInfo(updatedPaymentInfo)

                usersCollection.document(userUid ?: "").update("hasPaymentInfo", true).await()

                Log.d("TAG", "PaymentInfoRepository-PaymentInfo synced successfully after addition.")
            } catch (e: Exception) {
                Log.e("TAG", "PaymentInfoRepository-Error adding PaymentInfo: ${e.message}")
            }
        }
    }


    fun getPaymentMethodsSync(): LiveData<List<PaymentInfo>> {
        val data = paymentInfoDao.getPaymentInfo(userUid ?: "")
        data.observeForever {
            Log.d("TAG", "PaymentInfoRepository-Room - LiveData updated: $it")
        }
        return data
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