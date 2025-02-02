package com.example.chargehoodapp.data.model


data class Booking(
    val bookingId: String? = null,
    val userId: String? = null,
    val stationId: String? = null,
    val stationName: String? = null,
    val time: Long,
    val status: String? = null,
    val energyCharged: Double,
    val chargingCost: Double,
    val date: Long = System.currentTimeMillis()
)
