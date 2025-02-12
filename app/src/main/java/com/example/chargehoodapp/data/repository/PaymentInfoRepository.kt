package com.example.chargehoodapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val firestore = FirebaseModel.database
    private val paymentInfoCollection = firestore.collection(PAYMENT_INFO)
    private val usersCollection = firestore.collection(USERS)



    private fun getCurrentUserId(): String? {
        return FirebaseModel.getCurrentUser()?.uid
    }


    suspend fun syncPaymentInfo(): List<PaymentInfo> {
        val userUid = getCurrentUserId() ?: return emptyList()
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = paymentInfoCollection
                    .whereEqualTo("userId", userUid)
                    .get()
                    .await()

                val paymentInfoList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(PaymentInfo::class.java)?.copy(id = doc.id)
                }

                paymentInfoDao.deletePaymentInfoByUserId(userUid)
                paymentInfoDao.addPaymentInfoList(paymentInfoList)

                paymentInfoList
            } catch (e: Exception) {
                Log.e("TAG", "PaymentInfoRepository-Error syncing PaymentInfo: ${e.message}")
                emptyList()
            }
        }
    }



    suspend fun addPaymentInfo(paymentInfo: PaymentInfo) {
        val userUid = getCurrentUserId()

        withContext(Dispatchers.IO) {
            try {
                val documentRef= paymentInfoCollection.document()
                val generatedId = documentRef.id
                Log.d("TAG", "PaymentInfoRepository-PaymentInfo added successfully with ID: $generatedId")

                val updatedPaymentInfo = paymentInfo.copy(id = generatedId)
                documentRef.set(updatedPaymentInfo).await()
                paymentInfoDao.addPaymentInfo(updatedPaymentInfo)

                syncPaymentInfo()

                usersCollection.document(userUid ?: "").update("hasPaymentInfo", true).await()

                Log.d("TAG", "PaymentInfoRepository-PaymentInfo synced successfully after addition.")
            } catch (e: Exception) {
                Log.e("TAG", "PaymentInfoRepository-Error adding PaymentInfo: ${e.message}")
            }
        }
    }




    suspend fun deletePaymentInfo(paymentInfo: PaymentInfo) {
        val userUid = getCurrentUserId()
        try {
            paymentInfoCollection.document(paymentInfo.id).delete().await()

            paymentInfoDao.deletePaymentInfoById(paymentInfo.id)

            val remainingPayments = paymentInfoDao.getPaymentInfo(userUid ?: "").value
            Log.d("TAG", "PaymentInfoRepository-Remaining payments after delete: $remainingPayments")
            if (remainingPayments.isNullOrEmpty()) {
                usersCollection.document(userUid ?: "").update("hasPaymentInfo", false).await()
            }

            Log.d("TAG", "Payment deleted successfully")
        } catch (e: Exception) {
            Log.e("TAG", "Error deleting payment: ${e.message}")
        }
    }




}