package com.example.chargehoodapp.presentation.owner_station_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.ChargingStationRepository

class OwnerStationDetailsViewModel: ViewModel() {

    private val repository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository


    private val _chargingStation = MutableLiveData<ChargingStation?>()
    val chargingStation: LiveData<ChargingStation?> = _chargingStation

    fun setStation(station: ChargingStation) {
        _chargingStation.value = station
    }


}