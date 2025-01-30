package com.example.chargehoodapp.presentation.owner_station_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chargehoodapp.data.model.ChargingStation

class OwnerStationDetailsViewModel: ViewModel() {


    private val _chargingStation = MutableLiveData<ChargingStation?>()
    val chargingStation: LiveData<ChargingStation?> = _chargingStation

    fun setStation(station: ChargingStation) {
        _chargingStation.postValue(station)
    }


}