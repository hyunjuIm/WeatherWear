package com.hyunju.weatherwear.data.repository.weather

import com.hyunju.weatherwear.data.db.dao.WeatherDao
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.data.network.WeatherApiService
import com.hyunju.weatherwear.data.response.weather.Items
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val weatherDao: WeatherDao
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

    override suspend fun getWeatherItemsFromDevice(): List<WeatherEntity> =
        withContext(Dispatchers.IO) {
            weatherDao.getAll()
        }

    override suspend fun saveWeatherItemsToDevice(weatherItems: List<WeatherEntity>) =
        withContext(Dispatchers.IO) {
            weatherDao.deleteAll()
            weatherDao.insert(weatherItems)
        }

}