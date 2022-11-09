package com.hyunju.weatherwear.data.repository.wear

import com.hyunju.weatherwear.data.entity.WeatherWearEntity
import java.util.*

interface WeatherWearRepository {

    suspend fun getWeatherWear(id: Long): WeatherWearEntity

    suspend fun getWeatherWearLatestItem(date: String): WeatherWearEntity?

    suspend fun getSearchDateWeatherWears(date: String): List<WeatherWearEntity>

    suspend fun getSearchMaxTemperatureRangeWeatherWears(start: Int, end: Int): List<WeatherWearEntity>

    suspend fun getSearchMinTemperatureRangeWeatherWears(start: Int, end: Int): List<WeatherWearEntity>

    suspend fun getAllWeatherWears(): List<WeatherWearEntity>

    suspend fun insertWeatherWear(weatherWearEntity: WeatherWearEntity): Long

    suspend fun modifyWeatherWear(weatherWearEntity: WeatherWearEntity)

    suspend fun removeWeatherWear(id: Long)

}