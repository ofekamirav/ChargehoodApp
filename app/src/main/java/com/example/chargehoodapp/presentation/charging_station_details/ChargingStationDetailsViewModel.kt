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
        (MyApplication.Globals.context?.applicationContext as MyApplication).repository

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

    // Load station details from the DB with side threads
    fun loadChargingStationDetails(stationId: String) {
        viewModelScope.launch {
            try {
                val station = withContext(Dispatchers.IO) {
                    repository.getChargingStationById(stationId) // פעולה שקוראת ממסד הנתונים
                }
                Log.d("TAG", "ViewModel: Station loaded - $station")
                _chargingStation.postValue(station)

                val ownerId = station?.ownerId
                if (ownerId != null) {
                    val ownerName = withContext(Dispatchers.IO) {
                        repository.getOwnerName(ownerId)
                    }
                    val ownerPhone = withContext(Dispatchers.IO) {
                        repository.getOwnerPhoneNumber(ownerId)
                    }
                    Log.d("TAG", "ViewModel: Owner loaded - Name: $ownerName, Phone: $ownerPhone")
                    _ownerName.postValue(ownerName)
                    _ownerPhoneNumber.postValue(ownerPhone)
                }
            } catch (e: Exception) {
                Log.e("TAG", "ChargingStationDetailsViewModel-Error loading station details: ${e.message}")
                _errorMessage.postValue("Error loading station details: ${e.message}")
            }
        }
    }


    fun checkCurrentUserPaymentBoolean() {
        viewModelScope.launch {
            try {
                val user = currentUserId?.let { userRepository.getUserByUid(it) }

                if (user != null) {
                    _currentUserPaymentBoolean.postValue(user.hasPaymentInfo)
                } else {
                    _currentUserPaymentBoolean.postValue(false)
                    Log.d("TAG", "ChargingStationDetailsViewModel-User not found")
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error checking user payment boolean: ${e.message}")
            }

        }

    }
}



