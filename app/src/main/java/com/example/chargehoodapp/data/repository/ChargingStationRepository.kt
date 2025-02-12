package com.example.chargehoodapp.data.repository

import FirebaseModel
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chargehoodapp.base.Constants.Collections.CHARGING_STATIONS
import com.example.chargehoodapp.base.Constants.Collections.USERS
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.local.dao.ChargingStationDao
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.remote.CloudinaryModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
//Connect the ViewModel with the database- local and remote
class ChargingStationRepository(
    val chargingStationDao: ChargingStationDao,
) {
    private val firestore = FirebaseModel.database
    private val stationsCollection = firestore.collection(CHARGING_STATIONS)
    private val usersCollection = firestore.collection(USERS)
    private val cloudinaryModel = CloudinaryModel()

    private val _allStations = MutableLiveData<List<ChargingStation>>()
    val allStations: LiveData<List<ChargingStation>> get() = _allStations

    private val _userStations = MutableLiveData<List<ChargingStation>>()
    val userStations: LiveData<List<ChargingStation>> get() = _userStations



    init {
        listenForFirestoreUpdates()
    }

    //Update charging stations in local database from Firestore
    suspend fun syncAllStations() {
        try {
            val snapshot = stationsCollection.get().await()
            val allStations = snapshot.toObjects(ChargingStation::class.java)

            withContext(Dispatchers.IO) {
                chargingStationDao.clearAllStations()
                chargingStationDao.updateChargingStations(allStations)
            }

            _allStations.postValue(allStations)
            Log.d("TAG", "ChargingStationRepository - Synced all stations: ${allStations.size}")
        } catch (e: Exception) {
            Log.e("TAG", "ChargingStationRepository - Error syncing all stations: ${e.message}")
        }
    }

    suspend fun syncUserStations() {
        try {
            val userUid = getCurrentUserId() ?: return
            val snapshot = stationsCollection.whereEqualTo("ownerId", userUid).get().await()
            val userStations = snapshot.toObjects(ChargingStation::class.java)

            withContext(Dispatchers.IO) {
                chargingStationDao.clearUserStations(userUid)
                chargingStationDao.updateChargingStations(userStations)
            }

            _userStations.postValue(userStations)
            Log.d("TAG", "ChargingStationRepository - Synced user stations: ${userStations.size}")
        } catch (e: Exception) {
            Log.e("TAG", "ChargingStationRepository - Error syncing user stations: ${e.message}")
        }
    }


    fun listenForFirestoreUpdates() {
        stationsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("TAG", "ChargingStationRepository-Firestore listener error: ${error.message}")
                return@addSnapshotListener
            }

            snapshot?.let {
                val updatedStations = it.toObjects(ChargingStation::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    chargingStationDao.clearAllStations()
                    chargingStationDao.updateChargingStations(updatedStations)

                    withContext(Dispatchers.Main) {
                        _allStations.postValue(updatedStations)
                    }
                }
            }
        }
    }


    //Create station in side thread
    suspend fun createChargingStation(chargingStation: ChargingStation, image: Bitmap?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val userUid = getCurrentUserId()
                Log.d("TAG", "ChargingStationRepository-Starting station creation...")

                val wazeUrl =
                    "https://waze.com/ul?ll=${chargingStation.latitude},${chargingStation.longitude}&navigate=yes"
                var imageUrl: String? = null

                if (image != null) {
                    val uploadSuccess = cloudinaryModel.uploadImage(
                        image,
                        "station_${System.currentTimeMillis()}",
                        "stations",
                        onSuccess = { url -> imageUrl = url },
                        onError = { Log.e("TAG", "ChargingStationRepository-Upload error: $it") }
                    )

                    if (!uploadSuccess) {
                        Log.e(
                            "TAG",
                            "ChargingStationRepository-Image upload failed, station not created."
                        )
                        return@withContext false
                    }
                }

                val stationWithImage = chargingStation.copy(
                    imageUrl = imageUrl ?: "",
                    wazeUrl = wazeUrl
                )

                val documentRef = stationsCollection.add(stationWithImage).await()
                val generatedId = documentRef.id

                val finalStation = stationWithImage.copy(id = generatedId)
                documentRef.set(finalStation).await()

                chargingStationDao.createChargingStation(finalStation)


                userUid?.let {
                    usersCollection.document(it).update("isStationOwner", true).await()
                }

                Log.d(
                    "TAG",
                    "ChargingStationRepository-Station created successfully with ID: $generatedId"
                )
                true
            } catch (e: Exception) {
                Log.e(
                    "TAG",
                    "ChargingStationRepository-Error creating charging station: ${e.message}"
                )
                false
            }
        }
    }

    fun clearLiveData() {
        _allStations.postValue(emptyList())
        _userStations.postValue(emptyList())
    }


    fun getAllOwnerStations(): List<ChargingStation> {
        return chargingStationDao.getAllChargingStationsByOwnerId(getCurrentUserId() ?: "")
    }


    fun deleteChargingStation(chargingStation: ChargingStation) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                chargingStationDao.deleteChargingStation(chargingStation)
                stationsCollection.document(chargingStation.id).delete().await()

                val remainingStations = chargingStationDao.getAllChargingStationsByOwnerId(
                    getCurrentUserId() ?: ""
                )
                if (remainingStations.isEmpty()) {
                    usersCollection.document(getCurrentUserId() ?: "")
                        .update("isStationOwner", false).await()
                }

                Log.d(
                    "TAG",
                    "ChargingStationRepository - Station deleted successfully: ${chargingStation.id}"
                )
            } catch (e: Exception) {
                Log.e("TAG", "ChargingStationRepository - Error deleting station: ${e.message}")
            }
        }
    }


    fun getChargingStationById(id: String): LiveData<ChargingStation?> =
        chargingStationDao.getChargingStation(id)


    fun updateStationStatus(stationId: String?, status: Boolean) {
        if (stationId.isNullOrEmpty()) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                stationsCollection.document(stationId).update("availability", status).await()
                chargingStationDao.updateStationStatus(stationId, status)

                Log.d("TAG", "ChargingStationRepository - Station status updated to: $status")
            } catch (e: Exception) {
                Log.e(
                    "TAG",
                    "ChargingStationRepository - Failed to update station status: ${e.message}"
                )
            }
        }
    }

    suspend fun updateChargingStation(stationId: String, updates: Map<String, Any>): Boolean {
        if (stationId.isEmpty()) {
            Log.e("TAG", "ChargingStationRepository - Error: Station ID is empty!")
            return false
        }

        return try {
            stationsCollection.document(stationId).update(updates).await()

            val updatedStationSnapshot = stationsCollection.document(stationId).get().await()
            val updatedStation = updatedStationSnapshot.toObject(ChargingStation::class.java)

            if (updatedStation != null) {
                withContext(Dispatchers.IO) {
                    chargingStationDao.updateChargingStations(listOf(updatedStation))
                }
                Log.d("TAG", "ChargingStationRepository - Charging station updated: $stationId")
            }

            true
        } catch (e: Exception) {
            Log.e("TAG", "ChargingStationRepository - Error updating charging station: ${e.message}")
            false
        }
    }



    private fun getCurrentUserId(): String? {
        return FirebaseModel.getCurrentUser()?.uid
    }


}