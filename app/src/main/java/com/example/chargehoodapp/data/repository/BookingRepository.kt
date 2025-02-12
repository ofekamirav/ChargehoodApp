package com.example.chargehoodapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chargehoodapp.base.Constants.Collections.BOOKING
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.local.dao.BookingDao
import com.example.chargehoodapp.data.local.dao.ChargingStationDao
import com.example.chargehoodapp.data.model.Booking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BookingRepository(private val bookingDao: BookingDao) {

    private val firestore = FirebaseModel.database
    private val bookingsCollection = firestore.collection(BOOKING)

    private val chargingStationRepository: ChargingStationRepository =
        (MyApplication.Globals.context?.applicationContext as MyApplication).StationRepository

    suspend fun getAllRelevantBookings(): List<Booking> {
        Log.d("TAG", "BookingRepository - Fetching all relevant bookings")
        val resultList = mutableListOf<Booking>()

        withContext(Dispatchers.IO) {
            try {
                val currentUserId = getCurrentUserId()

                val ownerStations = chargingStationRepository.getAllOwnerStations()
                val ownerStationIds = ownerStations.map { it.id }

                val allBookings = mutableListOf<Booking>()

                val userBookingsSnapshot = bookingsCollection
                    .whereEqualTo("status", "Completed")
                    .whereEqualTo("userId", currentUserId)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()
                allBookings.addAll(userBookingsSnapshot.toObjects(Booking::class.java))

                ownerStationIds.chunked(10).forEach { chunk: List<String> ->
                    val snapshot = bookingsCollection
                        .whereEqualTo("status", "Completed")
                        .whereIn("stationId", chunk)
                        .orderBy("date", Query.Direction.DESCENDING)
                        .get()
                        .await()
                    allBookings.addAll(snapshot.toObjects(Booking::class.java))
                }

                val uniqueBookings = allBookings.distinctBy { it.bookingId }
                    .sortedByDescending { it.date }

                resultList.addAll(uniqueBookings)

                bookingDao.deleteAll()
                bookingDao.insertAll(uniqueBookings)

            } catch (e: Exception) {
                Log.e("TAG", "Error fetching all relevant bookings: ${e.message}")
            }
        }

        return resultList
    }

    fun getAllBookings(): List<Booking>{
        return bookingDao.getAllBookings()
    }


    fun createBooking(booking: Booking): String {
        //Generate a unique ID for the booking
        val bookingId = bookingsCollection.document().id

        //Set the booking ID in the booking object
        val updatedBooking = booking.copy(bookingId = bookingId)

        //Add the booking to Firestore
        bookingsCollection.document(bookingId).set(updatedBooking)
        Log.d("TAG", "BookingRepository-Booking created with ID: $bookingId")
        return bookingId
    }

    suspend fun updateBooking(bookingId: String, updatedBooking: Booking) {
        bookingsCollection.document(bookingId)
            .set(updatedBooking, SetOptions.merge()).await()
            withContext(Dispatchers.IO) {
                bookingDao.insertBooking(updatedBooking)
            }
        Log.d("TAG", "BookingRepository-Booking updated with ID: $bookingId")
    }

    private fun getCurrentUserId(): String? {
        return FirebaseModel.getCurrentUser()?.uid
    }


}
