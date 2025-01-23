package com.example.chargehoodapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.chargehoodapp.data.model.ChargingStation


@Dao
interface ChargingStationDao {

    @Query("SELECT * FROM charging_stations")
    fun getAllChargingStations(): LiveData<List<ChargingStation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createChargingStation(vararg chargingStation: ChargingStation)

    @Delete
    fun deleteChargingStation(station: ChargingStation)

    @Query("SELECT * FROM charging_stations WHERE id = :id")
    fun getChargingStation(id: String): ChargingStation?

    @Update
    fun updateChargingStation(chargingStation: ChargingStation)
}
