package com.example.chargehoodapp.presentation.charging_page

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

    private val MAX_CHARGING_TIME_MINUTES = 300 // 5 hours

    private val BookingRepository = BookingRepository()

    private val currentUserId = FirebaseModel.getCurrentUser()?.uid

    private val StationRepository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository

    private val _station = MutableLiveData<ChargingStation>()
    val station: LiveData<ChargingStation> get() = _station

    private val _booking = MutableLiveData<Booking?>()
    val booking: LiveData<Booking?> get() = _booking

    private val _progress = MutableLiveData<Int>(0)
    val progress: LiveData<Int> get() = _progress

    private val _energyCharged = MutableLiveData<Double>(0.0)
    val energyCharged: LiveData<Double> get() = _energyCharged

    private val _chargingCost = MutableLiveData<Double>(0.0)
    val chargingCost: LiveData<Double> get() = _chargingCost

    private val _elapsedMinutes = MutableLiveData<Int>(0)
    val elapsedMinutes: LiveData<Int> get() = _elapsedMinutes

    private var timerJob: Job? = null

    fun setStation(station: ChargingStation) {
        _station.value = station
    }

    fun startCharging() {
        _energyCharged.value = 0.0
        _chargingCost.value = 0.0
        _elapsedMinutes.value = 0
        _progress.value = 0

        val currentDate = System.currentTimeMillis()

        val booking = Booking(
            bookingId = "",
            userId = currentUserId.toString(),
            stationId = station.value?.id.toString(),
            time = 0,
            status = "In use",
            energyCharged = 0.0,
            chargingCost = 0.0,
            date = currentDate
        )

        BookingRepository.createBooking(booking)

        //Timer runs in side thread
        timerJob = viewModelScope.launch(Dispatchers.IO){
            while (isActive && (_elapsedMinutes.value ?: 0) < MAX_CHARGING_TIME_MINUTES){
                delay(60_000)

                //In main thread
                withContext(Dispatchers.Main){
                    val chargingSpeed = station.value?.chargingSpeed?.split(" ")?.get(0)?.toDoubleOrNull() ?: 0.0
                    val pricePerkWh = station.value?.pricePerkW ?: 0.0

                    _elapsedMinutes.value = (_elapsedMinutes.value ?: 0) + 1
                    val newEnergy = (_energyCharged.value ?: 0.0) + (chargingSpeed / 60.0)
                    val newCost = newEnergy * pricePerkWh

                    _energyCharged.value = round(newEnergy * 100) / 100
                    _chargingCost.value = round(newCost * 100) / 100

                    //Update the progress bar
                    val progressValue = (_elapsedMinutes.value ?: 0) * 100 / MAX_CHARGING_TIME_MINUTES
                    _progress.value = progressValue.coerceIn(0, 100)

                    if ((_elapsedMinutes.value ?: 0) >= MAX_CHARGING_TIME_MINUTES) {
                        stopCharging()
                    }

                }
            }
        }
    }


    fun stopCharging() {
        timerJob?.cancel()

        val finalEnergyCharged = _energyCharged.value ?: 0.0
        val finalCost = _chargingCost.value ?: 0.0
        val totalTimeMinutes = _elapsedMinutes.value ?: 0

        _booking.value?.let{ booking ->
            BookingRepository.updateBookingChargeCostAndTime(booking.bookingId, finalCost, finalEnergyCharged, totalTimeMinutes.toLong())
            BookingRepository.updateBookingStatus(booking.bookingId, "completed")
        }

        viewModelScope.launch(Dispatchers.IO) {
            StationRepository.updateStationStatus(station.value?.id, "available")
        }

        //for the next charge
        _energyCharged.value = 0.0
        _chargingCost.value = 0.0
        _elapsedMinutes.value = 0
        _progress.value = 0

    }



}