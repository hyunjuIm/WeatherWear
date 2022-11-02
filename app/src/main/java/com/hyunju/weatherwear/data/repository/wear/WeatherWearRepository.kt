package com.hyunju.weatherwear.data.repository.wear

import com.hyunju.weatherwear.data.entity.WeatherWearEntity

interface WeatherWearRepository {

    suspend fun getWeatherWear(id: Long): WeatherWearEntity

    suspend fun getWeatherWearLatestItem(): WeatherWearEntity?

    suspend fun getSearchDateWeatherWears(date: Long): List<WeatherWearEntity>

    suspend fun getSearchTemperatureRangeWeatherWears(start: Int, end: Int): List<WeatherWearEntity>

    suspend fun getAllWeatherWears(): List<WeatherWearEntity>

    suspend fun insertWeatherWear(weatherWearEntity: WeatherWearEntity): Long

    suspend fun removeWeatherWear(id: Long)

}