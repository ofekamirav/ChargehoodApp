package com.example.chargehoodapp.presentation.your_station

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import com.example.chargehoodapp.data.repository.PaymentInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddStationViewModel() : ViewModel() {

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

    private val _stationAdded = MutableLiveData<Boolean?>()
    val stationAdded: LiveData<Boolean?> = _stationAdded

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

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

    fun createChargingStation(latitude: Double, longitude: Double, addressName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.createChargingStation(
                ChargingStation(
                    id = "",
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
                ),
                _stationImage.value
            )

            withContext(Dispatchers.Main) {
                _stationAdded.value = success
            }
        }
    }

    fun clearLiveData() {
        _stationImage.value = null
        _chargingSpeed.value = null
        _connectionType.value = null
        _pricePerKW.value = null
        _stationAdded.value = null
        _errorMessage.value = null
    }

}
