package com.example.chargehoodapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.chargehoodapp.base.Constants.Collections.PAYMENT_INFO
import com.example.chargehoodapp.base.Constants.Collections.USERS
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.local.dao.PaymentInfoDao
import com.example.chargehoodapp.data.model.PaymentInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
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
        return paymentInfoDao.getPaymentInfo(userUid ?: "")
    }


    suspend fun syncPaymentInfo() {
        try {
            val lastUpdateTime = MyApplication.getLastUpdateTime(PAYMENT_LAST_UPDATE_KEY)

            // Filter by userId and lastUpdated
            val snapshot = paymentInfoCollection
                .whereEqualTo("userId", userUid)
                .whereGreaterThan("lastUpdated", lastUpdateTime)
                .get()
                .await()

            val paymentInfoList = snapshot.toObjects(PaymentInfo::class.java)

            if (paymentInfoList.isNotEmpty()) {
                // Update local database
                paymentInfoDao.addPaymentInfoList(paymentInfoList)

                // Update last update time
                val latestUpdateTime = paymentInfoList.maxOfOrNull { it.lastUpdated } ?: lastUpdateTime
                MyApplication.saveLastUpdateTime(PAYMENT_LAST_UPDATE_KEY, latestUpdateTime)
            }

            // Delete local cards not in the remote list
            val ids = paymentInfoList.map { it.id }
            paymentInfoDao.deletePaymentInfoNotIn(ids)

            Log.d("TAG", "PaymentInfoRepository-PaymentInfo synced successfully.")
        } catch (e: Exception) {
            Log.e("TAG", "PaymentInfoRepository-Error syncing PaymentInfo: ${e.message}")
        }
    }

    suspend fun addPaymentInfo(paymentInfo: PaymentInfo) {
        try {
            // Create a new document with an auto-generated ID
                val documentRef = paymentInfoCollection.add(paymentInfo).await()
                val generatedId = documentRef.id

                // Update the payment info locally with the new ID
                val updatedPaymentInfo = paymentInfo.copy(id = generatedId)
                paymentInfoDao.addPaymentInfo(updatedPaymentInfo)

                // Update the user document to indicate payment info exists
                usersCollection.document(userUid ?: "").update("hasPaymentInfo", true)

                Log.d("TAG", "PaymentInfoRepository-PaymentInfo added successfully with ID: $generatedId")
        } catch (e: Exception) {
            Log.e("TAG", "PaymentInfoRepository-Error adding PaymentInfo: ${e.message}")
        }

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