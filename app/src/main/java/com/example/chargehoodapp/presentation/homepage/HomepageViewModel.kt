package com.example.chargehoodapp.presentation.homepage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng

class HomepageViewModel : ViewModel() {

    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation: LiveData<LatLng> = _currentLocation

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
}
