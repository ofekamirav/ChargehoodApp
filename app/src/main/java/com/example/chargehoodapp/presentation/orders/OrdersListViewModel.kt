package com.example.chargehoodapp.presentation.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chargehoodapp.data.model.Booking
import com.example.chargehoodapp.data.repository.BookingRepository

class OrdersListViewModel: ViewModel() {

    private val repository = BookingRepository()

    private val _orders = MutableLiveData<List<Booking>>()
    val orders: LiveData<List<Booking>> get() = _orders

    fun fetchOrders() {
        repository.getAllBookings { bookings ->
            _orders.postValue(bookings)
        }
    }
}