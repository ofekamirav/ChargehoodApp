package com.example.chargehoodapp.presentation.your_station

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import kotlinx.coroutines.launch

class AddStationViewModel(private val repository: ChargingStationRepository) : ViewModel() {

    private val _stations = MutableLiveData<List<ChargingStation>>()
    val stations: LiveData<List<ChargingStation>> get() = _stations

    fun fetchStations() {
        viewModelScope.launch {
            repository.getAllChargingStations().observeForever {
                _stations.value = it
            }
        }
    }

    fun setStations(stationList: List<ChargingStation>) {
        _stations.value = stationList
    }
}