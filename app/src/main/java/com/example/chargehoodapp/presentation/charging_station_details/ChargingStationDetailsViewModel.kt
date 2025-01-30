package com.example.chargehoodapp.presentation.charging_station_details


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


    private var userRepository = UserRepository()

    private val userUid = FirebaseModel.getCurrentUser()?.uid ?:""

    private val _currentUserPaymentBoolean = MutableLiveData<Boolean?>()
    val currentUserPaymentBoolean: LiveData<Boolean?> = _currentUserPaymentBoolean

    private val _chargingStation = MutableLiveData<ChargingStation?>()
    val chargingStation: LiveData<ChargingStation?> = _chargingStation

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _ownerName = MutableLiveData<String?>()
    val ownerName: LiveData<String?> = _ownerName

    private val _ownerPhoneNumber = MutableLiveData<String?>()
    val ownerPhoneNumber: LiveData<String?> = _ownerPhoneNumber

    fun setStation(station: ChargingStation) {
        _chargingStation.postValue(station)
    }

    fun setUserPaymentBoolean() {
        viewModelScope.launch(Dispatchers.IO) {
            val boolean = userRepository.getUserByUid(userUid)?.hasPaymentInfo
            _currentUserPaymentBoolean.postValue(boolean)
        }
    }

    fun loadOwnerDetails(station: ChargingStation) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            try {
                val ownerId = station.ownerId
                if (ownerId.isBlank()) {
                    _errorMessage.postValue("Owner ID is null")
                    Log.e("TAG", "ChargingStationDetailsViewModel - ERROR: Owner ID is null!")
                    return@launch
                }
                Log.d("TAG", "ChargingStationDetailsViewModel - Fetching user for Owner ID: $ownerId")

                val user = userRepository.getUserByUid(ownerId)
                withContext(Dispatchers.Main) {
                    _ownerName.value = user?.name ?: "Unknown Owner"
                    _ownerPhoneNumber.value = user?.phoneNumber ?: "No Phone"
                }
                Log.d("TAG", "ChargingStationDetailsViewModel - Owner name: ${user?.name}")

            } catch (e: Exception) {
                Log.e("TAG", "ChargingStationDetailsViewModel - Error loading owner details: ${e.message}")
            }
            _isLoading.postValue(false)
        }
    }



}



