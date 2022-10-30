package com.hyunju.weatherwear.data.repository.wear

import com.hyunju.weatherwear.data.entity.WeatherWearEntity

interface WeatherWearRepository {

    suspend fun getWeatherWear(id: Long): WeatherWearEntity

    suspend fun getAllWeatherWears(): List<WeatherWearEntity>

    suspend fun insertWeatherWear(weatherWearEntity: WeatherWearEntity): Long

    suspend fun removeWeatherWear(id: Long)

}