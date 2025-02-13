package com.example.chargehoodapp.presentation.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.model.Booking
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.BookingRepository
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersListViewModel: ViewModel() {

    private val BookingRepository: BookingRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).bookingRepository

    private val stationRepository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository

    private val _allRelevantBookings = MutableLiveData<List<Booking>?>()
    val allRelevantBookings: LiveData<List<Booking>?> get() = _allRelevantBookings

    private val _station = MutableLiveData<ChargingStation>()
    val station: LiveData<ChargingStation> get() = _station

    init {
        FirebaseModel.addAuthStateListener {
            loadAllRelevantBookings()
        }
    }


    fun loadOrders() {
        viewModelScope.launch(Dispatchers.IO) {
             val bookings = BookingRepository.getAllBookings()
            _allRelevantBookings.postValue(bookings)
            Log.d("TAG", "OrdersListViewModel-Loaded ${bookings.size} bookings")
            }
        }


    fun loadAllRelevantBookings() {
        viewModelScope.launch(Dispatchers.IO) {
            val bookings = BookingRepository.getAllRelevantBookings()
            withContext(Dispatchers.Main) {
                _allRelevantBookings.value = bookings
            }
        }
    }

    fun getCurrentUserId(): String {
        return FirebaseModel.getCurrentUser()?.uid ?: ""
    }

    fun getStationById(stationId: String): LiveData<ChargingStation?> {
        return stationRepository.getChargingStationById(stationId)
    }
}
