package com.example.chargehoodapp.presentation.your_station

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chargehoodapp.data.model.ChargingStation

class YourStationListViewModel : ViewModel() {

    private val _stations = MutableLiveData<List<ChargingStation>>()
    val stations: LiveData<List<ChargingStation>> get() = _stations

    fun setStations(newStations: List<ChargingStation>) {
        _stations.value = newStations
    }
}
