package com.hyunju.weatherwear.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hyunju.weatherwear.data.entity.WeatherWearEntity

@Dao
interface WeatherWearDao {

    @Query("SELECT * FROM WeatherWearEntity WHERE id=:id")
    suspend fun get(id: Long): WeatherWearEntity

    @Query("SELECT * FROM WeatherWearEntity ORDER BY date DESC")
    suspend fun getAll(): List<WeatherWearEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherWearEntity: WeatherWearEntity):Long

    @Query("DELETE FROM WeatherWearEntity WHERE id=:id")
    suspend fun delete(id: Long)

}