package com.example.chargehoodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chargehoodapp.base.Constants.Collections.BOOKING

@Entity(tableName = BOOKING)
data class Booking(
    @PrimaryKey val bookingId: String = "",
    val userId: String? = null,
    val stationId: String? = null,
    val stationName: String? = null,
    val time: Long = 0L,
    val status: String? = null,
    val energyCharged: Double = 0.0,
    val chargingCost: Double = 0.0,
    val date: Long = System.currentTimeMillis()
)
