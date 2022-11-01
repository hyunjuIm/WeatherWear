package com.hyunju.weatherwear.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hyunju.weatherwear.data.entity.LocationEntity

@Dao
interface LocationDao {

    @Query("SELECT * FROM LocationEntity")
    suspend fun getAll(): List<LocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locationEntity: LocationEntity)

    @Query("DELETE FROM LocationEntity")
    suspend fun deleteAll()

}