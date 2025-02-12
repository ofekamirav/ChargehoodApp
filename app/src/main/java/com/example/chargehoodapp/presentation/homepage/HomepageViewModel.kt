package com.example.chargehoodapp.presentation.homepage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.local.dao.ChargingStationDao
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomepageViewModel : ViewModel() {

    private val repository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository

    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation: LiveData<LatLng> = _currentLocation

    val chargingStations: LiveData<List<ChargingStation>> = repository.allStations

    private val _locationPermissionGranted = MutableLiveData<Boolean>()
    val locationPermissionGranted: LiveData<Boolean> = _locationPermissionGranted

    private var fusedLocationClient: FusedLocationProviderClient? = null


    fun initLocationProvider(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        checkLocationPermission(context)
    }

    fun checkLocationPermission(context: Context) {
        val permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        _locationPermissionGranted.value = permissionGranted
    }

    //Get the current location and update the LiveData
    fun updateLocation(context: Context) {
        try {
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                location?.let {
                    _currentLocation.value = LatLng(it.latitude, it.longitude)
                    Log.d("TAG", "HomepageViewModel-Current location: ${_currentLocation.value}")
                }
            }?.addOnFailureListener { e ->
                Log.e("TAG", "HomepageViewModel-Location update failed: ${e.message}")
            }
        } catch (e: SecurityException) {
            Log.e("TAG", "HomepageViewModel-Permission denied: ${e.message}")
        }
    }

    fun syncStations() {
        viewModelScope.launch {
            repository.getAllStations()
            repository.syncAllStations()
        }
    }
}
