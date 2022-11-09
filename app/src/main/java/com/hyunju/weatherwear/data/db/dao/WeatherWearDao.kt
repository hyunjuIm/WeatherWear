package com.hyunju.weatherwear.data.db.dao

import androidx.room.*
import com.hyunju.weatherwear.data.entity.WeatherWearEntity
import com.hyunju.weatherwear.util.weather.Temperatures
import java.util.*

@Dao
interface WeatherWearDao {

    @Query("SELECT * FROM WeatherWearEntity WHERE id=:id")
    suspend fun get(id: Long): WeatherWearEntity

    @Query("SELECT * FROM (SELECT * FROM WeatherWearEntity ORDER BY id DESC) WHERE dateText=:date ORDER BY date DESC LIMIT 1")
    suspend fun getTodayLatestItem(date: String): WeatherWearEntity

    @Query("SELECT * FROM (SELECT * FROM WeatherWearEntity ORDER BY id DESC) WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    suspend fun getSearchDate(start: Date, end: Date): List<WeatherWearEntity>

    @Query("SELECT * FROM (SELECT * FROM WeatherWearEntity ORDER BY id DESC) WHERE maxTemperature BETWEEN :start AND :end ORDER BY date DESC")
    suspend fun getSearchMaxTemperatureRange(start: Int, end: Int): List<WeatherWearEntity>

    @Query("SELECT * FROM (SELECT * FROM WeatherWearEntity ORDER BY id DESC) WHERE minTemperature BETWEEN :start AND :end ORDER BY date DESC")
    suspend fun getSearchMinTemperatureRange(start: Int, end: Int): List<WeatherWearEntity>

    @Query("SELECT * FROM (SELECT * FROM WeatherWearEntity ORDER BY id DESC) ORDER BY date DESC")
    suspend fun getAll(): List<WeatherWearEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherWearEntity: WeatherWearEntity): Long

    @Update
    suspend fun update(weatherWearEntity: WeatherWearEntity)

    @Query("DELETE FROM WeatherWearEntity WHERE id=:id")
    suspend fun delete(id: Long)

}