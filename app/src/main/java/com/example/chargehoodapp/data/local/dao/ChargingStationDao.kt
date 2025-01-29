package com.example.chargehoodapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateChargingStations(stations: List<ChargingStation>)

    @Query("UPDATE charging_stations SET availability = :status WHERE id = :id")
    fun updateStationStatus(id: String, status: String)

    @Query("DELETE FROM charging_stations WHERE id NOT IN (:ids)")
    fun deleteStationsNotIn(ids: List<String?>)
}
