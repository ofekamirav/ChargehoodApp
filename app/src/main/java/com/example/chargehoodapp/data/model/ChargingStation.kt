package com.example.chargehoodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chargehoodapp.base.Constants.Collections.CHARGING_STATIONS
import com.google.firebase.firestore.GeoPoint

@Entity(tableName = CHARGING_STATIONS)
data class ChargingStation(
    @PrimaryKey val id: String,
    val ownerId: String,
    val location: GeoPoint,
    val connectionType: String,
    val chargingSpeed: String,
    val availability: Boolean,
    val imageUrl: String,
    val pricePerMinute: Double,
    val whatsappNumber: String,
    val wazeUrl: String
    )