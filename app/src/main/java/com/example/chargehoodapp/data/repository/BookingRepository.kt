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


    suspend fun getAllRelevantBookings(): LiveData<List<Booking>> {
        Log.d("TAG", "BookingRepository - Fetching all relevant bookings")
        val resultLiveData = MutableLiveData<List<Booking>>()

        withContext(Dispatchers.IO) {
            try {
                val currentUserId = getCurrentUserId()
                val ownerStations = chargingStationRepository.getAllOwnerStations()
                Log.d("TAG", "BookingRepository - Owner stations: $ownerStations")
                val ownerStationIds = ownerStations.map { it.id }
                Log.d("TAG", "BookingRepository - Owner station IDs: $ownerStationIds")

                val allBookings = mutableListOf<Booking>()

                //get the user's bookings
                val userBookingsSnapshot = bookingsCollection
                    .whereEqualTo("status", "Completed")
                    .whereEqualTo("userId", currentUserId)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()

                allBookings.addAll(userBookingsSnapshot.toObjects(Booking::class.java))

                // get all bookings for the user's stations
                ownerStationIds.chunked(10).forEach { chunk: List<String> ->
                    Log.d("TAG", "BookingRepository - Fetching bookings for station IDs: $chunk")
                    val snapshot = bookingsCollection
                        .whereEqualTo("status", "Completed")
                        .whereIn("stationId", chunk)
                        .orderBy("date", Query.Direction.DESCENDING)
                        .get()
                        .await()
                    allBookings.addAll(snapshot.toObjects(Booking::class.java))
                    Log.d("TAG", "BookingRepository - Fetched ${snapshot.size()} bookings for stations: $chunk")
                }

                val uniqueBookings = allBookings.distinctBy { it.bookingId }
                    .sortedByDescending { it.date }

                resultLiveData.postValue(uniqueBookings)
            } catch (e: Exception) {
                Log.e("TAG", "Error fetching all relevant bookings: ${e.message}")
                resultLiveData.postValue(emptyList())
            }
        }

        return resultLiveData
    }


    suspend fun syncBookings() {
        withContext(Dispatchers.IO) {
            val currentUserId = getCurrentUserId() ?: ""
            Log.d("TAG", "BookingRepository - Syncing bookings for userId: $currentUserId")

            try {
                val snapshot = bookingsCollection
                    .whereEqualTo("status", "Completed")
                    .whereEqualTo("userId", currentUserId)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val bookings = snapshot.toObjects(Booking::class.java)
                Log.d("TAG", "BookingRepository-Fetched ${bookings.size} completed bookings from Firestore")

                val existingIds = bookingDao.getCompletedBookings(currentUserId).map { it.bookingId }
                val newBookings = bookings.filter { it.bookingId !in existingIds }

                if (newBookings.isNotEmpty()) {
                    bookingDao.insertAll(newBookings)
                    Log.d("TAG", "BookingRepository - Inserted ${newBookings.size} new bookings to Room")
                } else {
                    Log.d("TAG", "BookingRepository - No new bookings to insert")
                }

            } catch (e: Exception) {
                Log.e("TAG", "BookingRepository - Error syncing bookings: ${e.message}")
            }
        }
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

    fun updateBooking(bookingId: String, updatedBooking: Booking) {
        bookingsCollection.document(bookingId)
            .set(updatedBooking, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("TAG", "Booking updated successfully: $updatedBooking")
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error updating booking: ${e.message}")
            }
    }

    private fun getCurrentUserId(): String? {
        return FirebaseModel.getCurrentUser()?.uid
    }




}
