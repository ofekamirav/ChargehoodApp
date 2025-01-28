package com.example.chargehoodapp.presentation.your_station

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddStationViewModel : ViewModel() {
    val connectionType = MutableLiveData<String?>()
    val chargingSpeed = MutableLiveData<String?>()
    val pricePerMinute = MutableLiveData<String?>()

    private val _validationError = MutableLiveData<String?>()
    val validationError: LiveData<String?> get() = _validationError

    fun validateInputs(): Boolean {
        return when {
            connectionType.value.isNullOrEmpty() -> {
                _validationError.value = "Please select a connection type."
                false
            }
            chargingSpeed.value.isNullOrEmpty() -> {
                _validationError.value = "Please select a charging speed."
                false
            }
            pricePerMinute.value.isNullOrEmpty() -> {
                _validationError.value = "Please enter a price per minute."
                false
            }
            else -> {
                _validationError.value = null
                true
            }
        }
    }

    fun resetInputs() {
        connectionType.value = null
        chargingSpeed.value = null
        pricePerMinute.value = null
    }

    fun isConnectionTypeSelected(type: String): Boolean {
        return connectionType.value == type
    }

    fun isChargingSpeedSelected(speed: String): Boolean {
        return chargingSpeed.value == speed
    }
}
