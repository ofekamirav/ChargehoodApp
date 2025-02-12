package com.example.chargehoodapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.local.dao.BookingDao
import com.example.chargehoodapp.data.local.dao.ChargingStationDao
import com.example.chargehoodapp.data.local.dao.PaymentInfoDao
import com.example.chargehoodapp.data.model.Booking
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.model.PaymentInfo

@Database(entities = [ChargingStation::class, PaymentInfo::class, Booking::class], version = 12)
abstract class AppLocalDBRepository : RoomDatabase() {
    abstract fun chargingStationDao(): ChargingStationDao
    abstract fun paymentInfoDao(): PaymentInfoDao
    abstract fun bookingDao(): BookingDao

}

object AppLocalDB {

    val database: AppLocalDBRepository by lazy {

        val context = MyApplication.Globals.context ?: throw IllegalStateException("Context not initialized")

        Room.databaseBuilder(
            context= context,
            klass = AppLocalDBRepository::class.java,
            name = "dbFileName.db"
        )
            .fallbackToDestructiveMigration()//hundler the versions
            .build()

    }
}