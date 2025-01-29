package com.example.chargehoodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chargehoodapp.base.Constants.Collections.PAYMENT_INFO

@Entity(tableName = PAYMENT_INFO)
data class PaymentInfo(
    @PrimaryKey val id: String,
    val userId: String,
    val cardLastFour: String,
    val cardType: String,
    val cardExpiry: String,
    val lastUpdated: Long
){
    constructor() : this("", "", "", "", "", System.currentTimeMillis())

    constructor(userId: String, cardLastFour: String, cardType: String, cardExpiry: String) : this("", userId, cardLastFour, cardType, cardExpiry, System.currentTimeMillis())
}


