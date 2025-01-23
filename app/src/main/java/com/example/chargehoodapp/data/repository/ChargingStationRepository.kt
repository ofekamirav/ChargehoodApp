package com.example.chargehoodapp.data.repository

import androidx.lifecycle.LiveData
import com.example.chargehoodapp.base.Constants.Collections.CHARGING_STATIONS
import com.example.chargehoodapp.data.local.dao.ChargingStationDao
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.remote.CloudinaryModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
//handle the charging station data between the local database and the remote Firestore database
class ChargingStationRepository(
    private val chargingStationDao: ChargingStationDao,
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val stationsCollection = firestore.collection(CHARGING_STATIONS)
    private val cloudinaryModel = CloudinaryModel()


    //Get all charging stations
    fun getAllChargingStations(): LiveData<List<ChargingStation>> = chargingStationDao.getAllChargingStations()

    //Create a new charging station
    suspend fun createChargingStation(chargingStation: ChargingStation) {
        chargingStationDao.createChargingStation(chargingStation)
        stationsCollection.document(chargingStation.id).set(chargingStation).await()

        //need yo add logic for image upload to cloudinary

    }
}