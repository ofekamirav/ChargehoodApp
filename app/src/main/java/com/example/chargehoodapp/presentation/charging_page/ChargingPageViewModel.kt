package com.example.chargehoodapp.presentation.charging_page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chargehoodapp.data.model.ChargingStation

class ChargingPageViewModel: ViewModel() {

    private val _chargingStation = MutableLiveData<ChargingStation>()
    val chargingStation: LiveData<ChargingStation> = _chargingStation



}