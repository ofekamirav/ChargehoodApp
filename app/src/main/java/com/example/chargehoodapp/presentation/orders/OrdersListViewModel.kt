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
import kotlinx.coroutines.launch

class OrdersListViewModel: ViewModel() {

    private val BookingRepository: BookingRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).bookingRepository

    private val stationRepository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository

    private val _completedBookings = MutableLiveData<List<Booking>?>()
    val completedBookings: LiveData<List<Booking>?> get() = _completedBookings

    private val _station = MutableLiveData<ChargingStation>()
    val station: LiveData<ChargingStation> get() = _station


    fun loadCompletedBookings() {
        BookingRepository.getCompletedBookingsLive().observeForever { bookings ->
            if (bookings != null) {
                _completedBookings.postValue(bookings)
            }
            Log.d("TAG", "OrdersListViewModel-Completed bookings loaded: $bookings")
        }
    }

    fun getStationById(stationId: String): LiveData<ChargingStation?> {
        return stationRepository.getChargingStationById(stationId)
    }
}
