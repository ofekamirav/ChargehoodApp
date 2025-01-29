package com.example.chargehoodapp.data.repository

import android.util.Log
import com.example.chargehoodapp.base.Constants.Collections.BOOKING
import com.example.chargehoodapp.data.local.dao.ChargingStationDao
import com.example.chargehoodapp.data.model.Booking
import com.google.firebase.firestore.FirebaseFirestore

class BookingRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val bookingsCollection = firestore.collection(BOOKING)


    fun createBooking(booking: Booking) {
        //Generate a unique ID for the booking
        val bookingId = bookingsCollection.document().id

        //Set the booking ID in the booking object
        val updatedBooking = booking.copy(bookingId = bookingId)

        //Add the booking to Firestore
        bookingsCollection.document(bookingId).set(updatedBooking)
        Log.d("TAG", "BookingRepository-Booking created with ID: $bookingId")
    }

    fun updateBookingStatus(bookingId: String, status: String) {
        bookingsCollection.document(bookingId)
            .update("status", status)
    }


    fun updateBookingChargeCostAndTime(bookingId: String, chargeCost: Double, energyCharged: Double, time: Long)  {
        bookingsCollection.document(bookingId)
            .update("chargeCost", chargeCost, "energyCharged", energyCharged, "time", time)

    }

    fun getAllBookings(callback: (List<Booking>) -> Unit) {
        bookingsCollection.get()
            .addOnSuccessListener { result ->
                val bookings = result.documents.mapNotNull { it.toObject(Booking::class.java) }
                callback(bookings)
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "BookingRepository-Error fetching bookings: ${e.message}")
                callback(emptyList())
            }
    }

//    fun listenToBooking(bookingId: String): LiveData<Booking> {
//        val bookingLiveData = MutableLiveData<Booking>()
//        db.collection("bookings").document(bookingId)
//            .addSnapshotListener { snapshot, error ->
//                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
//                snapshot.toObject(Booking::class.java)?.let {
//                    bookingLiveData.postValue(it)
//                }
//            }
//        return bookingLiveData
//    }



}