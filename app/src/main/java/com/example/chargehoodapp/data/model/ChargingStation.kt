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
    val connectionType: String,
    val chargingSpeed: String,
    val availability: Boolean,
    val imageUrl: String,
    val pricePerkW: Double,
    val wazeUrl: String,
    val lastUpdated: Long? = null
    ) {
    fun toGeoPoint(): GeoPoint {
        return GeoPoint(latitude, longitude)
    }

    companion object {
        fun fromGeoPoint(id: String, ownerId: String, geoPoint: GeoPoint): ChargingStation {
            return ChargingStation(
                id = id,
                ownerId = ownerId,
                latitude = geoPoint.latitude,
                longitude = geoPoint.longitude,
                connectionType = "Type",
                chargingSpeed = "Fast",
                availability = true,
                imageUrl = "url",
                pricePerkW = 0.5,
                wazeUrl = "waze",
                lastUpdated = System.currentTimeMillis()
            )
        }
    }
}