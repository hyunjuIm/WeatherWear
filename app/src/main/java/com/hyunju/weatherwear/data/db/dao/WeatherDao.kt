package com.hyunju.weatherwear.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hyunju.weatherwear.data.entity.WeatherEntity

@Dao
interface WeatherDao {

    @Query("SELECT * FROM WeatherEntity")
    suspend fun getAll(): List<WeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherItems: List<WeatherEntity>)

    @Query("DELETE FROM WeatherEntity")
    suspend fun deleteAll()

}