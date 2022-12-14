package com.hyunju.weatherwear.di

import android.content.Context
import androidx.room.Room
import com.hyunju.weatherwear.data.db.ApplicationDatabase
import com.hyunju.weatherwear.util.conventer.RoomTypeConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    @Singleton
    fun provideDB(@ApplicationContext context: Context): ApplicationDatabase =
        Room.databaseBuilder(context, ApplicationDatabase::class.java, ApplicationDatabase.DB_NAME)
            .build()

    @Provides
    @Singleton
    fun provideWeatherWearDao(database: ApplicationDatabase) =
        database.weatherWearDao()

    @Provides
    @Singleton
    fun provideWeatherDao(database: ApplicationDatabase) = database.weatherDao()

    @Provides
    @Singleton
    fun provideLocationDao(database: ApplicationDatabase) = database.locationDao()

}