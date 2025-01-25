package com.example.chargehoodapp.data.repository

import FirebaseModel
import androidx.lifecycle.LiveData
import com.example.chargehoodapp.base.Constants.Collections.CHARGING_STATIONS
import com.example.chargehoodapp.base.Constants.Collections.USERS
import com.example.chargehoodapp.data.local.dao.ChargingStationDao
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.remote.CloudinaryModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
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


    //Get all charging stations
    fun getAllChargingStations(): LiveData<List<ChargingStation>> = chargingStationDao.getAllChargingStations()

    //Create a new charging station
    suspend fun createChargingStation(chargingStation: ChargingStation) {
        if(userUid != null){
            chargingStationDao.createChargingStation(chargingStation)//Create in local database
            stationsCollection.document(chargingStation.id).set(chargingStation).await() //Create in remote database
            usersCollection.document(userUid).update("isStationOwner", true).await()
        }

        //Upload station image to Cloudinary

    }


}