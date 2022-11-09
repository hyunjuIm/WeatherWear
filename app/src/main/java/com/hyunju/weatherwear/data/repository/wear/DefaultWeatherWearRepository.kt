package com.hyunju.weatherwear.data.repository.wear

import com.hyunju.weatherwear.data.db.dao.WeatherWearDao
import com.hyunju.weatherwear.data.entity.WeatherWearEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class DefaultWeatherWearRepository @Inject constructor(
    private val weatherWearDao: WeatherWearDao
) : WeatherWearRepository {

    override suspend fun getWeatherWear(id: Long): WeatherWearEntity = withContext(Dispatchers.IO) {
        weatherWearDao.get(id)
    }

    override suspend fun getWeatherWearLatestItem(date: String): WeatherWearEntity =
        withContext(Dispatchers.IO) {
            weatherWearDao.getTodayLatestItem(date)
        }

    override suspend fun getSearchDateWeatherWears(date: String): List<WeatherWearEntity> =
        withContext(Dispatchers.IO) {
            weatherWearDao.getSearchDate(date)
        }

    override suspend fun getSearchMaxTemperatureRangeWeatherWears(
        start: Int,
        end: Int
    ): List<WeatherWearEntity> = withContext(Dispatchers.IO) {
        weatherWearDao.getSearchMaxTemperatureRange(start, end)
    }

    override suspend fun getSearchMinTemperatureRangeWeatherWears(
        start: Int,
        end: Int
    ): List<WeatherWearEntity> = withContext(Dispatchers.IO) {
        weatherWearDao.getSearchMinTemperatureRange(start, end)
    }

    override suspend fun getAllWeatherWears(): List<WeatherWearEntity> =
        withContext(Dispatchers.IO) {
            weatherWearDao.getAll()
        }

    override suspend fun insertWeatherWear(weatherWearEntity: WeatherWearEntity): Long =
        withContext(Dispatchers.IO) {
            weatherWearDao.insert(weatherWearEntity)
        }

    override suspend fun modifyWeatherWear(weatherWearEntity: WeatherWearEntity) =
        withContext(Dispatchers.IO) {
            weatherWearDao.update(weatherWearEntity)
        }

    override suspend fun removeWeatherWear(id: Long) = withContext(Dispatchers.IO) {
        weatherWearDao.delete(id)
    }
}