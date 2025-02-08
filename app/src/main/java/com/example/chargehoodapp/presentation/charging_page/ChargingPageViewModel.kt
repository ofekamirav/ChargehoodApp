package com.example.chargehoodapp.presentation.charging_page

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.local.dao.ChargingStationDao
import com.example.chargehoodapp.data.model.Booking
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.BookingRepository
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.round


class ChargingPageViewModel: ViewModel() {

    private val MAX_CHARGING_TIME_SECONDS = 0.8 * 60 * 60 // 50 mins

    private val BookingRepository: BookingRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).bookingRepository

    private val Stationrepository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository

    private val _station = MutableLiveData<ChargingStation>()
    val station: LiveData<ChargingStation> get() = _station

    private val _booking = MutableLiveData<Booking?>()
    val booking: LiveData<Booking?> get() = _booking

    private val _progress = MutableLiveData<Int>(0)
    val progress: LiveData<Int> get() = _progress

    val formattedTimeLiveData = MutableLiveData<String>()
    val formattedTime: LiveData<String> get() = formattedTimeLiveData

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _elapsedSeconds = MutableLiveData<Int>(0)
    val elapsedSeconds: LiveData<Int> get() = _elapsedSeconds

    private var timerJob: Job? = null

    private fun getCurrentUserId(): String {
        return FirebaseModel.getCurrentUser()?.uid ?: ""
    }


    fun setStation(station: ChargingStation) {
        _station.value = station
    }

    fun startCharging() {
        _isLoading.value = true
        _elapsedSeconds.value = 0
        _progress.value = 0

        val currentDate = System.currentTimeMillis()

        val booking = Booking(
            bookingId = "",
            userId = getCurrentUserId(),
            stationId = station.value?.id.toString(),
            stationName = station.value?.addressName.toString(),
            time = 0,
            status = "Charging",
            energyCharged = 0.0,
            chargingCost = 0.0,
            date = currentDate
        )

        viewModelScope.launch(Dispatchers.IO) {
            val createdBookingId = BookingRepository.createBooking(booking)

            withContext(Dispatchers.Main) {
                _booking.value = booking.copy(bookingId = createdBookingId)
                Log.d("TAG", "ChargingPageViewModel - Booking created with ID: $createdBookingId")
                _isLoading.value = false
            }
        }

        //Change the station availability to false
        viewModelScope.launch(Dispatchers.IO) {
            Stationrepository.updateStationStatus(station.value?.id, false)
        }

        // Timer runs in side thread
        timerJob = viewModelScope.launch(Dispatchers.IO) {
            var totalSeconds = 0
            while (isActive && totalSeconds < MAX_CHARGING_TIME_SECONDS) {
                delay(1_000)
                totalSeconds++

                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60
                val progressbarValue = ((totalSeconds.toFloat() / MAX_CHARGING_TIME_SECONDS) * 100).toInt()

                withContext(Dispatchers.Main) {
                    _elapsedSeconds.value = totalSeconds
                    _progress.value = progressbarValue.coerceIn(0, 100)

                    formattedTimeLiveData.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    Log.d("TAG", "Progress: ${_progress.value}")


                    if (totalSeconds >= MAX_CHARGING_TIME_SECONDS) {
                        stopCharging()
                    }
                }

            }
        }

    }


    fun stopCharging() {
        timerJob?.cancel()

        val totalTimeSeconds = _elapsedSeconds.value ?: 0
        val finalEnergyCharged = calculateEnergyCharged(totalTimeSeconds)
        val finalCost = calculateChargingCost(finalEnergyCharged)

        val updatedBooking = _booking.value?.copy(
            energyCharged = finalEnergyCharged,
            chargingCost = finalCost,
            time = totalTimeSeconds.toLong(),
            status = "Completed"
        )

        if (updatedBooking != null) {
            viewModelScope.launch(Dispatchers.IO) {
                BookingRepository.updateBooking(updatedBooking.bookingId.toString(), updatedBooking)
                Log.d("TAG", "ChargingPageViewModel - Booking updated: $updatedBooking")

                Stationrepository.updateStationStatus(station.value?.id, true)
                Log.d("TAG", "ChargingPageViewModel - Station status updated: TRUE")
            }
        } else {
            Log.e("TAG", "ChargingPageViewModel - ERROR: Booking is null, update failed!")
        }

        // Reset values for the next charge
        _elapsedSeconds.value = 0
        _progress.value = 0
    }


    private fun calculateEnergyCharged(totalTimeSeconds: Int): Double {
        val chargingSpeed = _station.value?.chargingSpeed?.split(" ")?.get(0)?.toDoubleOrNull() ?: 0.0
        return round((chargingSpeed * totalTimeSeconds / 3600.0) * 100) / 100
    }

    private fun calculateChargingCost(energyCharged: Double): Double {
        val pricePerKWh = _station.value?.pricePerkW ?: 0.0
        return round((energyCharged * pricePerKWh) * 100) / 100
    }


}