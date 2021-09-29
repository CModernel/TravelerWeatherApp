package com.example.travelerweatherapp.Data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeatherDao {

    @Query("SELECT * FROM Weather")
    fun getAll(): List<Weather>

    @Query("SELECT * FROM Weather WHERE id = :id")
    fun getById(id: Int): Weather

    @Query("SELECT * FROM weather where isLocal = 1 order by dt desc limit 1")
    fun getLocal(): Weather

    @Query("SELECT * FROM Weather")
    fun getAllLiveData(): LiveData<List<Weather>>

    @Query("SELECT * FROM Weather WHERE isLocal = 0")
    fun getAllCapitals(): List<Weather>

    @Update
    suspend fun update(weather: Weather)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: List<Weather>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: Weather)

    @Delete
    suspend fun delete(weather: Weather)

}