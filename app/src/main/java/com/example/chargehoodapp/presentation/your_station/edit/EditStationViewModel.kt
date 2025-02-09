package com.example.chargehoodapp.presentation.your_station.edit

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditStationViewModel : ViewModel() {

    private val repository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository

    private val _stationImage = MutableLiveData<Bitmap?>()
    val stationImage: LiveData<Bitmap?> = _stationImage

    private val ownerId = FirebaseModel.getCurrentUser()?.uid ?: ""

    private val _chargingSpeed = MutableLiveData<String?>()
    val chargingSpeed: LiveData<String?> = _chargingSpeed

    private val _connectionType = MutableLiveData<String?>()
    val connectionType: LiveData<String?> = _connectionType

    private val _pricePerKW = MutableLiveData<String?>()
    val pricePerKW: LiveData<String?> = _pricePerKW

    private val _stationUpdated = MutableLiveData<Boolean>()
    val stationUpdated: LiveData<Boolean> = _stationUpdated

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _stationDetails = MutableLiveData<ChargingStation>()
    val stationDetails: LiveData<ChargingStation> = _stationDetails

    fun setStationImage(bitmap: Bitmap) {
        _stationImage.postValue(bitmap)
    }

    fun setChargingSpeed(speed: String) {
        _chargingSpeed.postValue(speed)
    }

    fun setConnectionType(type: String) {
        _connectionType.postValue(type)
    }

    fun setPrice(price: String) {
        _pricePerKW.postValue(price)
    }

    fun updateChargingStation(latitude: Double, longitude: Double, addressName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val stationId = _stationDetails.value?.id ?: return@launch

            val updatedStation = ChargingStation(
                id = stationId,
                ownerId = ownerId,
                latitude = latitude,
                longitude = longitude,
                addressName = addressName,
                connectionType = _connectionType.value ?: "",
                chargingSpeed = _chargingSpeed.value ?: "",
                availability = true,
                imageUrl = "",
                pricePerkW = _pricePerKW.value?.toDoubleOrNull() ?: 0.0,
                wazeUrl = "",
                lastUpdated = System.currentTimeMillis()
            )

            }
        }
    }

