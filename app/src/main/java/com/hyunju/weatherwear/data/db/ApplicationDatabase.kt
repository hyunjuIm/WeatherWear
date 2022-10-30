package com.hyunju.weatherwear.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hyunju.weatherwear.data.db.dao.WeatherWearDao
import com.hyunju.weatherwear.data.entity.WeatherWearEntity

@Database(
    entities = [WeatherWearEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "ApplicationDataBase.db"
    }

    abstract fun weatherWearDao(): WeatherWearDao

}