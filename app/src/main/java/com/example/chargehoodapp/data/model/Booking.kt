package com.example.chargehoodapp.data.model

data class Booking(
    val bookingId: String,
    val userId: String,
    val stationId: String,
    val startTime: Long,
    val endTime: Long,
    val status: String
)
