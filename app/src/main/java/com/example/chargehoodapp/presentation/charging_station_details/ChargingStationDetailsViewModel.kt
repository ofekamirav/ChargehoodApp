package com.example.chargehoodapp.presentation.charging_station_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import kotlinx.coroutines.launch

class ChargingStationDetailsViewModel: ViewModel() {

    private val repository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).repository

    private val _chargingStation = MutableLiveData<ChargingStation?>()
    val chargingStation: LiveData<ChargingStation?> = _chargingStation

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _ownerName = MutableLiveData<String?>()
    val ownerName: LiveData<String?> = _ownerName

    private val _ownerPhoneNumber = MutableLiveData<String?>()
    val ownerPhoneNumber: LiveData<String?> = _ownerPhoneNumber

    fun loadChargingStationDetails(stationId: String) {
        viewModelScope.launch {
            try {
                val station = repository.getChargingStationById(stationId)
                _chargingStation.postValue(station)

                val ownerId = station?.ownerId
                if (ownerId != null) {
                    _ownerName.postValue(repository.getOwnerName(ownerId))
                    _ownerPhoneNumber.postValue(repository.getOwnerPhoneNumber(ownerId))
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error loading station details: ${e.message}")
            }
        }
    }
}
