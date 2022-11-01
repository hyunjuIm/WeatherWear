package com.hyunju.weatherwear.data.repository.weather

import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.data.response.weather.Items

interface WeatherRepository {

    suspend fun getWeather(
        dataType: String,
        numOfRows: Int,
        pageNo: Int,
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ): Items?

    suspend fun getWeatherItemsFromDevice(): List<WeatherEntity>

    suspend fun saveWeatherItemsToDevice(weatherItems: List<WeatherEntity>)

}