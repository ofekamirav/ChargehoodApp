package com.example.chargehoodapp.presentation.charging_station_details

import FirebaseModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import com.example.chargehoodapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChargingStationDetailsViewModel: ViewModel() {

    private val repository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository

    private val userRepository = UserRepository()

    private val currentUserId = FirebaseModel.getCurrentUser()?.uid

    private val _currentUserPaymentBoolean = MutableLiveData<Boolean?>()
    val currentUserPaymentBoolean: LiveData<Boolean?> = _currentUserPaymentBoolean

    private val _chargingStation = MutableLiveData<ChargingStation?>()
    val chargingStation: LiveData<ChargingStation?> = _chargingStation

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _ownerName = MutableLiveData<String?>()
    val ownerName: LiveData<String?> = _ownerName

    private val _ownerPhoneNumber = MutableLiveData<String?>()
    val ownerPhoneNumber: LiveData<String?> = _ownerPhoneNumber

    fun setStation(station: ChargingStation) {
        _chargingStation.value = station
    }

    fun loadOwnerDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val ownerId = _chargingStation.value?.ownerId ?: return@launch
                val ownerName = repository.getOwnerName(ownerId)
                val ownerPhone = repository.getOwnerPhoneNumber(ownerId)
                _ownerName.postValue(ownerName)
                _ownerPhoneNumber.postValue(ownerPhone)
            } catch (e: Exception) {
                Log.e("TAG", "Error loading owner details: ${e.message}")
            }
        }
    }


}



