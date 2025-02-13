package com.example.chargehoodapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chargehoodapp.data.model.Booking

@Dao
interface BookingDao {

    @Query("SELECT * FROM booking WHERE userId = :userId AND status = 'Completed' ORDER BY date DESC")
    fun getCompletedBookings(userId: String): List<Booking>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(bookings: List<Booking>)

    @Query("DELETE FROM booking WHERE status = 'Completed'")
    fun clearCompletedBookings()

    @Query("SELECT * FROM booking ORDER BY date DESC")
    fun getAllBookings(): List<Booking>
}
