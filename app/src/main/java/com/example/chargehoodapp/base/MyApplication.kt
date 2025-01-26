package com.example.chargehoodapp.base

import android.app.Application
import android.content.Context
import com.example.chargehoodapp.data.local.AppLocalDB
import com.example.chargehoodapp.data.repository.ChargingStationRepository


//Shared preferences to all project
class MyApplication: Application() {

    val database by lazy { AppLocalDB.database }
    val repository by lazy { ChargingStationRepository(database.ChargingStationDao()) }

    object Globals {
        var context: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.context = applicationContext
    }

    companion object {
        fun saveLastUpdateTime(timestamp: Long) {
            val sharedPreferences = Globals.context!!
                .getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().putLong("last_update_time", timestamp).apply()
        }

        fun getLastUpdateTime(): Long {
            val sharedPreferences = Globals.context!!
                .getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
            return sharedPreferences.getLong("last_update_time", 0L)
        }
    }
}