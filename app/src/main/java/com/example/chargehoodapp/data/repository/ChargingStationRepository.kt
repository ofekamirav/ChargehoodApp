package com.example.chargehoodapp.data.repository

import FirebaseModel
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.chargehoodapp.base.Constants.Collections.CHARGING_STATIONS
import com.example.chargehoodapp.base.Constants.Collections.USERS
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.local.dao.ChargingStationDao
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.remote.CloudinaryModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
//Connect the ViewModel with the database- local and remote
class ChargingStationRepository(
    private val chargingStationDao: ChargingStationDao,
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val stationsCollection = firestore.collection(CHARGING_STATIONS)
    private val usersCollection = firestore.collection(USERS)
    private val cloudinaryModel = CloudinaryModel()
    private val userUid = FirebaseModel.getCurrentUser()?.uid

    companion object {
        private const val LAST_UPDATE_KEY = "last_update_time_charging_stations"
    }


    //Update charging stations in local database from Firestore
    suspend fun syncChargingStations() {
        val lastUpdateTime = MyApplication.getLastUpdateTime(LAST_UPDATE_KEY)

        try {
            //Get all charging stations from the last update time
            val snapshot = stationsCollection
                .whereGreaterThan("lastUpdated", lastUpdateTime)
                .get().await()

            val updatedStations = snapshot.toObjects(ChargingStation::class.java)

            // Update the local database with the new charging stations
            if (updatedStations.isNotEmpty()) {
                chargingStationDao.updateChargingStations(updatedStations)

                // Update the last update time
                val latestUpdateTime = updatedStations.maxOfOrNull { it.lastUpdated ?: 0L } ?: lastUpdateTime
                MyApplication.saveLastUpdateTime(LAST_UPDATE_KEY, latestUpdateTime)
            }

            // Delete charging stations that are no longer in the updated list
            val ids = updatedStations.map { it.id }
            chargingStationDao.deleteStationsNotIn(ids)

        } catch (e: Exception) {
            Log.e("SyncError", "Error syncing data: ${e.message}")
        }
    }

    //Get all charging stations
    fun getAllChargingStations(): LiveData<List<ChargingStation>> = chargingStationDao.getAllChargingStations()

    //Create station in side thread
    suspend fun createChargingStation(chargingStation: ChargingStation, image: Bitmap?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val wazeUrl = "https://waze.com/ul?ll=${chargingStation.latitude},${chargingStation.longitude}&navigate=yes"
                var stationWithImage: ChargingStation? = null

                if (image != null) {
                    cloudinaryModel.uploadImage(
                        bitmap = image,
                        name = "station_${System.currentTimeMillis()}",
                        folder = "stations",
                        onSuccess = { imageUrl ->
                            stationWithImage = chargingStation.copy(imageUrl = imageUrl.toString(), wazeUrl = wazeUrl)
                        },
                        onError = {
                            Log.e("TAG", "Error uploading image")
                        }
                    )
                } else {
                    stationWithImage = chargingStation.copy(wazeUrl = wazeUrl)
                }

                stationWithImage?.let { station ->
                    val documentRef = stationsCollection.add(station).await()
                    val generatedId = documentRef.id

                    val finalStation = station.copy(id = generatedId)
                    chargingStationDao.createChargingStation(finalStation)
                    documentRef.set(finalStation).await()
                    Log.d("TAG", "Charging station created with ID: $generatedId")
                }

                userUid?.let {
                    usersCollection.document(it).update("isStationOwner", true).await()
                }

                true
            } catch (e: Exception) {
                Log.e("TAG", "Error creating charging station: ${e.message}")
                false
            }
        }
    }

    fun deleteChargingStation(chargingStation: ChargingStation) {
       chargingStationDao.deleteChargingStation(chargingStation)
       stationsCollection.document(chargingStation.id.toString()).delete()
       Log.d("TAG", "ChargingStationRepository-Charging station deleted: ${chargingStation.id}")
   }

    fun getChargingStationById(id: String): ChargingStation? = chargingStationDao.getChargingStation(id)

    fun getAllChargingStationsByOwnerId(ownerId: String): LiveData<List<ChargingStation>> = chargingStationDao.getAllChargingStationsByOwnerId(ownerId)


    suspend fun getOwnerName(ownerId: String): String? {
        return try {
            val document = firestore.collection("users").document(ownerId).get().await()
            document.getString("name")
        } catch (e: Exception) {
            null
        }
    }

    fun updateStationStatus(stationId: String?, status: String) {
        if(stationId != null){
            stationsCollection.document(stationId).update("availability", status)
            chargingStationDao.updateStationStatus(stationId, status)
            Log.d("TAG", "ChargingStationRepository-Charging station status updated: $status")
        }
    }

    suspend fun getOwnerPhoneNumber(ownerId: String): String? {
        return try {
            val document = firestore.collection("users").document(ownerId).get().await()
            document.getString("phoneNumber")
        } catch (e: Exception) {
            null
        }
    }
}