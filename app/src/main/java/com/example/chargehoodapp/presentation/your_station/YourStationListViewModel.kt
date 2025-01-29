package com.example.chargehoodapp.presentation.your_station

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.ChargingStationRepository

class YourStationListViewModel : ViewModel() {

    private val repository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository
    private val userUid = FirebaseModel.getCurrentUser()?.uid

    private val _stations = MutableLiveData<List<ChargingStation>>()
    val stations: LiveData<List<ChargingStation>> get() = _stations

    fun setStations(newStations: List<ChargingStation>) {
        _stations.value = newStations
    }
    fun lowDataStations() {
        repository.getChargingStationById(userUid.toString())
    }
}
