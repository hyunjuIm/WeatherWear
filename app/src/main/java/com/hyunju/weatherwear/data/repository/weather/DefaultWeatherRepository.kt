package com.hyunju.weatherwear.data.repository.weather

import android.util.Log
import com.hyunju.weatherwear.data.network.WeatherApiService
import com.hyunju.weatherwear.data.response.wether.Items
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) : WeatherRepository {

    override suspend fun getWeather(
        dataType: String,
        numOfRows: Int,
        pageNo: Int,
        baseDate: String,
        baseTime: String,
        nx: Int,
        ny: Int
    ): Items? = withContext(Dispatchers.IO) {
        val response = weatherApiService.getWeather(
            dataType = dataType,
            numOfRows = numOfRows,
            pageNo = pageNo,
            baseDate = baseDate,
            baseTime = baseTime,
            nx = nx,
            ny = ny
        )
        if (response.isSuccessful) {
            response.body()?.response?.body?.items
        } else {
            null
        }
    }

}