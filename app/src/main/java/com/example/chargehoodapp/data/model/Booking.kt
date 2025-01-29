package com.example.chargehoodapp.data.model

data class Booking(
    val bookingId: String,
    val userId: String,
    val stationId: String,
    val time: Long,
    val status: String,
    val energyCharged: Double,
    val chargingCost: Double,
    val date: Long
)
