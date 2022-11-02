package com.hyunju.weatherwear.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hyunju.weatherwear.data.entity.WeatherWearEntity
import com.hyunju.weatherwear.util.weather.Temperatures
import java.util.*

@Dao
interface WeatherWearDao {

    @Query("SELECT * FROM WeatherWearEntity WHERE id=:id")
    suspend fun get(id: Long): WeatherWearEntity

    @Query("SELECT * FROM WeatherWearEntity ORDER BY date DESC LIMIT 1")
    suspend fun getLatestItem(): WeatherWearEntity

    @Query("SELECT * FROM WeatherWearEntity WHERE date BETWEEN :start AND :end")
    suspend fun getSearchDate(start: Date, end: Date): List<WeatherWearEntity>

    @Query("SELECT * FROM WeatherWearEntity WHERE maxTemperature BETWEEN :start AND :end")
    suspend fun getSearchMaxTemperatureRange(start: Int, end: Int): List<WeatherWearEntity>

    @Query("SELECT * FROM WeatherWearEntity WHERE minTemperature BETWEEN :start AND :end")
    suspend fun getSearchMinTemperatureRange(start: Int, end: Int): List<WeatherWearEntity>

    @Query("SELECT * FROM (SELECT * FROM WeatherWearEntity ORDER BY id DESC) ORDER BY date DESC")
    suspend fun getAll(): List<WeatherWearEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherWearEntity: WeatherWearEntity): Long

    @Query("DELETE FROM WeatherWearEntity WHERE id=:id")
    suspend fun delete(id: Long)

}