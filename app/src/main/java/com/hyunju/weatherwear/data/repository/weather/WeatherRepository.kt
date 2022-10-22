package com.hyunju.weatherwear.data.repository.weather

import com.hyunju.weatherwear.data.response.wether.Items

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

}