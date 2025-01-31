package com.example.chargehoodapp.base

import android.app.Application
import android.content.Context
import com.example.chargehoodapp.BuildConfig
import com.example.chargehoodapp.data.local.AppLocalDB
import com.example.chargehoodapp.data.model.ChargingStation
import com.example.chargehoodapp.data.repository.BookingRepository
import com.example.chargehoodapp.data.repository.ChargingStationRepository
import com.example.chargehoodapp.data.repository.PaymentInfoRepository
import com.google.android.libraries.places.api.Places

//Shared preferences to all project
class MyApplication : Application() {

    val database by lazy { AppLocalDB.database }
    val StationRepository by lazy { ChargingStationRepository(database.chargingStationDao()) }
    val paymentInfoRepository by lazy { PaymentInfoRepository(database.paymentInfoDao()) }
    //val bookingRepository by lazy { BookingRepository(database.bookingDao()) }


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

    companion object {
        private const val SYNC_PREFS_KEY = "sync_prefs"

        fun saveLastUpdateTime(key: String, timestamp: Long) {
            val sharedPreferences = Globals.context!!
                .getSharedPreferences(SYNC_PREFS_KEY, Context.MODE_PRIVATE)
            sharedPreferences.edit().putLong(key, timestamp).apply()
        }

        fun getLastUpdateTime(key: String): Long {
            val sharedPreferences = Globals.context!!
                .getSharedPreferences(SYNC_PREFS_KEY, Context.MODE_PRIVATE)
            return sharedPreferences.getLong(key, 0L)
        }
    }
}
