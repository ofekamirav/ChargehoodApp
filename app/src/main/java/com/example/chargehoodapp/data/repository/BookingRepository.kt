package com.example.chargehoodapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chargehoodapp.base.Constants.Collections.BOOKING
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

    private val firestore = FirebaseFirestore.getInstance()
    private val bookingsCollection = firestore.collection(BOOKING)
    private val currentUserId = FirebaseModel.getCurrentUser()?.uid ?: ""

    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    fun getCompletedBookingsLive(): LiveData<List<Booking>> {
        Log.d("TAG", "BookingRepository - currentUserId: $currentUserId")
        val bookingsLiveData = MutableLiveData<List<Booking>>()
        Log.d("TAG", "BookingRepository - Fetching completed bookings for userId: $currentUserId")

        coroutineScope.launch {
            val localBookings = bookingDao.getCompletedBookings(currentUserId)
            bookingsLiveData.postValue(localBookings)

            firestore.collection("bookings")
                .whereEqualTo("status", "Completed")
                .whereEqualTo("userId", currentUserId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val bookings = snapshot.toObjects(Booking::class.java)
                    Log.d("TAG", "Fetched ${bookings.size} bookings from Firestore")

                    coroutineScope.launch {
                        val existingIds = bookingDao.getCompletedBookings(currentUserId).map { it.bookingId }
                        val newBookings = bookings.filter { it.bookingId !in existingIds }

                        if (newBookings.isNotEmpty()) {
                            bookingDao.insertAll(newBookings)
                            Log.d("TAG", "Inserted ${newBookings.size} new bookings to Room")
                        }

                        bookingsLiveData.postValue(bookingDao.getCompletedBookings(currentUserId))
                    }
                }
        }

        return bookingsLiveData
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



}
