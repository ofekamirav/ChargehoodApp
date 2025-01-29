package com.example.chargehoodapp.presentation.your_station

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import kotlinx.coroutines.launch

class AddStationViewModel(private val repository: ChargingStationRepository) : ViewModel() {

    private val _stationImage = MutableLiveData<Bitmap?>()
    val stationImage: LiveData<Bitmap?> = _stationImage

    private val _chargingSpeed = MutableLiveData<String?>()
    val chargingSpeed: LiveData<String?> = _chargingSpeed

    private val _connectionType = MutableLiveData<String?>()
    val connectionType: LiveData<String?> = _connectionType

    private val _pricePerKW = MutableLiveData<String?>()
    val pricePerKW: LiveData<String?> = _pricePerKW

    private val _stationAdded = MutableLiveData<Boolean>()
    val stationAdded: LiveData<Boolean> = _stationAdded

    fun setStationImage(bitmap: Bitmap) {
        _stationImage.value = bitmap
    }

    fun setChargingSpeed(speed: String) {
        _chargingSpeed.value = speed
    }

    fun setConnectionType(type: String) {
        _connectionType.value = type
    }

    fun setPrice(price: String) {
        _pricePerKW.value = price
    }

    fun createChargingStation(ownerId: String, latitude: Double, longitude: Double, addressName: String) {
        val speed = _chargingSpeed.value ?: return
        val type = _connectionType.value ?: return
        val price = _pricePerKW.value?.toDoubleOrNull() ?: return
        val image = _stationImage.value

        val newStation = ChargingStation(
            id = "",
            ownerId = ownerId,
            latitude = latitude,
            longitude = longitude,
            addressName = addressName,
            connectionType = type,
            chargingSpeed = speed,
            availability = true,
            imageUrl = "",
            pricePerkW = price,
            wazeUrl = "",
            lastUpdated = System.currentTimeMillis()
        )

        viewModelScope.launch {
            val success = repository.createChargingStation(newStation, image)
            _stationAdded.value = success
        }
    }
}
