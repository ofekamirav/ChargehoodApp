package com.example.chargehoodapp.presentation.your_station

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

class YourStationListViewModel : ViewModel() {

    private val repository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository
    private val userUid = FirebaseModel.getCurrentUser()?.uid?: ""

    private val _stations = MutableLiveData<List<ChargingStation>?>()
    val stations: LiveData<List<ChargingStation>> = repository.userStations

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> get() = _isEmpty


    init {
        if (userUid.isEmpty()) {
            Log.e("TAG", "YourStationListViewModel - No user UID found!")
        } else {
            FirebaseModel.addAuthStateListener {
                refreshChargingStations()
            }
        }
    }


    fun deleteStation(station: ChargingStation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteChargingStation(station)

            val updatedList = _stations.value?.toMutableList()?.apply { remove(station) }

            //UI update always in main thread
            withContext(Dispatchers.Main) {
                _stations.value = updatedList
                _isEmpty.value = updatedList.isNullOrEmpty()
                refreshChargingStations()
            }
        }
    }

    fun refreshChargingStations() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAG", "YourStationListViewModel-Refreshing charging stations for user: $userUid")
            repository.chargingStationDao.clearUserStations(userUid)
            repository.syncUserStations()
        }
    }



}
