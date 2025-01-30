package com.example.chargehoodapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chargehoodapp.data.model.PaymentInfo

@Dao
interface PaymentInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPaymentInfo(paymentInfo: PaymentInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPaymentInfoList(paymentInfoList: List<PaymentInfo>)

    @Query("SELECT * FROM payment_info WHERE userId = :userId")
    fun getPaymentInfo(userId: String): LiveData<List<PaymentInfo>>

    //Delete payment card by id
    @Query("DELETE FROM payment_info WHERE id = :id")
    fun deletePaymentInfoById(id: String)

    //Delete all payment info for a specific user
    @Query("DELETE FROM payment_info WHERE userId = :userId")
    fun deletePaymentInfoByUserId(userId: String)

    //Delete all payment info that are not in the given list
    @Query("DELETE FROM payment_info WHERE id NOT IN (:ids)")
    fun deletePaymentInfoNotIn(ids: List<String?>)

}