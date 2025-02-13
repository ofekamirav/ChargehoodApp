package com.example.chargehoodapp.presentation.edit_station_details

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.remote.CloudinaryModel
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import com.example.chargehoodapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditStationViewModel : ViewModel() {

    private val cloudinaryModel = CloudinaryModel()

    private val repository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _updateStatus = MutableLiveData<Boolean?>()
    val updateStatus: LiveData<Boolean?> = _updateStatus

    private val _stationDetails = MutableLiveData<ChargingStation?>()
    val stationDetails: LiveData<ChargingStation?> = _stationDetails

    private var originalStation: ChargingStation? = null

    fun setOriginalStation(station: ChargingStation) {
        originalStation = station
        _stationDetails.postValue(station)
    }

    fun clearLiveData() {
        _errorMessage.postValue(null)
        _updateStatus.postValue(null)
        _stationDetails.postValue(null)
    }

    fun getChargingStationById(stationId: String): LiveData<ChargingStation?> {
        Log.d("TAG", "EditStationViewModel-getChargingStationById called with ID: $stationId")
        return repository.getChargingStationById(stationId)
    }


    fun updateChargingStation(
        lat: Double?,
        long: Double?,
        address: String?,
        connectionType: String?,
        chargingSpeed: String?,
        price: Double?,
        imageUrl: Bitmap?
    ) {
        viewModelScope.launch {
            val currentStation = originalStation ?: return@launch
            val updates = mutableMapOf<String, Any>()

            if (lat != null && lat != currentStation.latitude) updates["latitude"] = lat
            if (long != null && long != currentStation.longitude) updates["longitude"] = long
            if (!address.isNullOrEmpty() && address != currentStation.addressName) updates["address"] = address
            if (!connectionType.isNullOrEmpty() && connectionType != currentStation.connectionType) updates["connectionType"] = connectionType
            if (!chargingSpeed.isNullOrEmpty() && chargingSpeed != currentStation.chargingSpeed) updates["chargingSpeed"] = chargingSpeed
            if (price != null && price != currentStation.pricePerkW) updates["pricePerkW"] = price

            if (imageUrl != null) {
                cloudinaryModel.uploadImage(
                    bitmap = imageUrl,
                    name = "station_${System.currentTimeMillis()}",
                    folder = "stations",
                    onSuccess = { uploadedImageUrl ->
                        updates["stationImageUrl"] = uploadedImageUrl ?: ""
                        finalizeUpdate(updates)
                    },
                    onError = {
                        _errorMessage.postValue("Failed to upload image")
                        Log.e("TAG", "EditStationViewModel-Error uploading image: $it")
                        _updateStatus.postValue(false)
                    }
                )
            } else if (updates.isNotEmpty()) {
                finalizeUpdate(updates)
            } else {
                _updateStatus.postValue(true)
                Log.d("TAG", "EditStationViewModel-No updates to apply")
            }
        }
    }

    private fun finalizeUpdate(updates: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            if (updates.isNotEmpty() && originalStation?.id != null) {
                val success = repository.updateChargingStation(originalStation!!.id, updates)

                withContext(Dispatchers.Main) {
                    _updateStatus.postValue(success)
                    Log.d("TAG", "EditStationViewModel - Update status: $success")
                }
            } else {
                withContext(Dispatchers.Main) {
                    _updateStatus.postValue(true)
                    Log.d("TAG", "EditStationViewModel - No updates to apply or original station ID is null")
                }
            }
        }
    }



}




