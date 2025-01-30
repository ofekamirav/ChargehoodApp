package com.example.chargehoodapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chargehoodapp.base.Constants.Collections.CHARGING_STATIONS
import com.google.firebase.firestore.GeoPoint

@Entity(tableName = CHARGING_STATIONS)
data class ChargingStation(
    @PrimaryKey val id: String,
    val ownerId: String,
    val latitude: Double,
    val longitude: Double,
    val addressName: String = "",
    val connectionType: String,
    val chargingSpeed: String,
    val availability: Boolean,
    val imageUrl: String,
    val pricePerkW: Double,
    val wazeUrl: String,
    val lastUpdated: Long = System.currentTimeMillis()
    ){
    constructor() : this("", "", 0.0, 0.0, "", "", "", true, "", 0.0, "", 0L)
}