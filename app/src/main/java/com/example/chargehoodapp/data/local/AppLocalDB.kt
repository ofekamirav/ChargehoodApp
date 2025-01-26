package com.example.chargehoodapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chargehoodapp.base.MyApplication
import com.example.chargehoodapp.data.local.dao.ChargingStationDao
import com.example.chargehoodapp.data.model.ChargingStation

@Database(entities = [ChargingStation::class], version = 1)
abstract class AppLocalDBRepository : RoomDatabase() {
    abstract fun ChargingStationDao(): ChargingStationDao
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