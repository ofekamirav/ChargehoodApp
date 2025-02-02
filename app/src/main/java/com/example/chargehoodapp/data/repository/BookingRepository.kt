package com.example.chargehoodapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chargehoodapp.base.Constants.Collections.BOOKING
import com.example.chargehoodapp.data.local.dao.ChargingStationDao
import com.example.chargehoodapp.data.model.Booking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BookingRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val bookingsCollection = firestore.collection(BOOKING)
    private val currentUserId = FirebaseModel.getCurrentUser()?.uid ?: ""


    fun getCompletedBookingsLive(): LiveData<List<Booking>?> {
        val bookingsLiveData = MutableLiveData<List<Booking>?>()
        firestore.collection("bookings")
            .whereEqualTo("status", "completed")
            .whereEqualTo("userId", currentUserId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                val bookings = snapshot?.toObjects(Booking::class.java)
                bookingsLiveData.postValue(bookings)

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
