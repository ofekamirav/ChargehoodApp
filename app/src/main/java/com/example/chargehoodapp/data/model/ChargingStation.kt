package com.example.chargehoodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.GeoPoint

@Entity(tableName = "charging_station")
data class ChargingStation(
    @PrimaryKey val uid: String,
    val ownerId: String,
    val location: GeoPoint,
    val connectionType: String,
    val chargingSpeed: String,
    val availability: Boolean,
    val imageUrl: String,
    val pricePerMinute: Double
    ) {

    companion object {

    }
}