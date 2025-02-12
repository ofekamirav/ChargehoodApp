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

    @Query("DELETE FROM charging_stations WHERE ownerId = :ownerId")
    fun clearUserStations(ownerId: String)

    @Query("SELECT * FROM charging_stations")
    fun getAllChargingStations(): List<ChargingStation>

    @Query("SELECT * FROM charging_stations")
    fun getAllStationsList(): List<ChargingStation>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createChargingStation(vararg chargingStation: ChargingStation)

    @Delete
    fun deleteChargingStation(station: ChargingStation)

    @Query("SELECT * FROM charging_stations WHERE ownerId = :ownerId")
    fun getAllChargingStationsByOwnerId(ownerId: String): List<ChargingStation>

    @Query("DELETE FROM charging_stations")
    fun clearAllStations()

    @Query("SELECT * FROM charging_stations WHERE id = :id")
    fun getChargingStation(id: String): LiveData<ChargingStation?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateChargingStations(stations: List<ChargingStation>)

    @Query("UPDATE charging_stations SET availability = :status WHERE id = :id")
    fun updateStationStatus(id: String, status: Boolean)

    @Query("DELETE FROM charging_stations WHERE id NOT IN (:ids)")
    fun deleteStationsNotIn(ids: List<String?>)
}
