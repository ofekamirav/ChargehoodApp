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
    private val chargingStationDao: ChargingStationDao,
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val stationsCollection = firestore.collection(CHARGING_STATIONS)
    private val usersCollection = firestore.collection(USERS)
    private val cloudinaryModel = CloudinaryModel()
    private val userUid = FirebaseModel.getCurrentUser()?.uid

    private val _chargingStations = MutableLiveData<List<ChargingStation>>()
    val chargingStations: LiveData<List<ChargingStation>> get() = _chargingStations

    companion object {
        private const val LAST_UPDATE_KEY = "last_update_time_charging_stations"
    }

    init {
        listenForFirestoreUpdates()
    }

    //Update charging stations in local database from Firestore
    suspend fun syncChargingStations() {
        try {
            val snapshot = stationsCollection.get().await()
            val updatedStations = snapshot.toObjects(ChargingStation::class.java)

            withContext(Dispatchers.IO) {
                chargingStationDao.clearAllStations()
                chargingStationDao.updateChargingStations(updatedStations)
            }

            _chargingStations.postValue(updatedStations)

            Log.d("TAG", "ChargingStationRepository - Station sync: ${updatedStations.size}")
        } catch (e: Exception) {
            Log.e("TAG", "ChargingStationRepository - SyncError: ${e.message}")
        }
    }


    fun getAllChargingStations(): LiveData<List<ChargingStation>> {
        return chargingStationDao.getAllChargingStations()
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
                        _chargingStations.postValue(updatedStations)
                    }
                }
            }
        }
    }



    //Create station in side thread
    suspend fun createChargingStation(chargingStation: ChargingStation, image: Bitmap?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("TAG", "ChargingStationRepository-Starting station creation...")

                val wazeUrl = "https://waze.com/ul?ll=${chargingStation.latitude},${chargingStation.longitude}&navigate=yes"
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
                        Log.e("TAG", "ChargingStationRepository-Image upload failed, station not created.")
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

                Log.d("TAG", "ChargingStationRepository-Station created successfully with ID: $generatedId")
                true
            } catch (e: Exception) {
                Log.e("TAG", "ChargingStationRepository-Error creating charging station: ${e.message}")
                false
            }
        }
    }


    fun deleteChargingStation(chargingStation: ChargingStation) {
        try {
                CoroutineScope(Dispatchers.IO).launch {
                    chargingStationDao.deleteChargingStation(chargingStation)
                    stationsCollection.document(chargingStation.id).delete()
                }


            val remainingStations =
                chargingStationDao.getAllChargingStationsByOwnerId(userUid ?: "").value
            if (remainingStations.isNullOrEmpty()) {
                userUid?.let {
                    usersCollection.document(it).update("isStationOwner", false)
                        .addOnSuccessListener {
                            Log.d("TAG", "ChargingStationRepository-User isStationOwner updated to false")
                        }

                    Log.d(
                        "TAG",
                        "ChargingStationRepository-Charging station deleted: ${chargingStation.id}"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "ChargingStationRepository-Error deleting charging station: ${e.message}")
        }
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