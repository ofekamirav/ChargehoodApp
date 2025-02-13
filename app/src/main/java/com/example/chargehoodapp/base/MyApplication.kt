package com.example.chargehoodapp.base

import android.app.Application
import android.content.Context
import com.example.chargehoodapp.BuildConfig
import com.example.chargehoodapp.data.local.AppLocalDB
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.BookingRepository
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import com.example.chargehoodapp.data.repository.PaymentInfoRepository
import com.example.chargehoodapp.data.repository.UserRepository
import com.google.android.libraries.places.api.Places

//Shared preferences to all project
class MyApplication : Application() {

    val database by lazy { AppLocalDB.database }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val StationRepository by lazy { ChargingStationRepository(database.chargingStationDao()) }
    val paymentInfoRepository by lazy { PaymentInfoRepository(database.paymentInfoDao()) }
    val bookingRepository by lazy { BookingRepository(database.bookingDao()) }


    object Globals {
        var context: Context? = null
        var selectedStation: ChargingStation? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.context = applicationContext
        if (!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.GOOGLE_MAP_API_KEY)
        }
    }

}
