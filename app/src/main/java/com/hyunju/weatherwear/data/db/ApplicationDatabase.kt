package com.hyunju.weatherwear.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hyunju.weatherwear.data.db.dao.LocationDao
import com.hyunju.weatherwear.data.db.dao.WeatherDao
import com.hyunju.weatherwear.data.db.dao.WeatherWearDao
import com.hyunju.weatherwear.data.entity.LocationEntity
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.data.entity.WeatherWearEntity

@Database(
    entities = [WeatherWearEntity::class, WeatherEntity::class, LocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "ApplicationDataBase.db"
    }

    abstract fun weatherWearDao(): WeatherWearDao

    abstract fun weatherDao(): WeatherDao

    abstract fun locationDao(): LocationDao

}