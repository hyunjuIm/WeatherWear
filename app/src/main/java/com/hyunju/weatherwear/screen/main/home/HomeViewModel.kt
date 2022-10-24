package com.hyunju.weatherwear.screen.main.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import com.hyunju.weatherwear.data.response.wether.CategoryType
import com.hyunju.weatherwear.data.response.wether.Items
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.util.date.getCurrentTime
import com.hyunju.weatherwear.util.date.getTodayDate
import com.hyunju.weatherwear.util.location.TO_GRID
import com.hyunju.weatherwear.util.location.convertGridGPS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val weatherRepository: WeatherRepository
) : BaseViewModel() {

    val homeStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)

    fun updateLocationWeather(latitude: Double, longitude: Double) = viewModelScope.launch {
        homeStateLiveData.value = HomeState.Loading

        val addressInfo = mapRepository.getReverseGeoInformation(latitude, longitude)
        val location = addressInfo?.toSearchInfoEntity(latitude, longitude) ?: run {
            homeStateLiveData.value = HomeState.Error(R.string.can_not_load_address_info)
            return@launch
        }

        val grid = convertGridGPS(TO_GRID, latitude, longitude)
        val weatherInfo = weatherRepository.getWeather(
            dataType = "JSON",
            numOfRows = 250,
            pageNo = 1,
            baseDate = getTodayDate(),
            baseTime = "0200",
            nx = grid.x.toInt(),
            ny = grid.y.toInt()
        ) ?: run {
            homeStateLiveData.value = HomeState.Error(R.string.can_not_load_weather_info)
            return@launch
        }

        val weatherEntity = parseWeatherData(weatherInfo)

        homeStateLiveData.value = HomeState.Success(
            location = location,
            weatherInfo = weatherEntity
        )
    }

    private fun parseWeatherData(weatherInfo: Items): WeatherEntity {
        val weatherEntity = WeatherEntity()

        weatherInfo.item?.forEach {
            weatherEntity.run {
                if (it?.category == CategoryType.TMN) TMN = fcstValueToInt(it.fcstValue)
                if (it?.category == CategoryType.TMX) TMX = fcstValueToInt(it.fcstValue)

                if (it?.fcstTime == getCurrentTime()) {
                    date = it.baseDate ?: ""
                    time = it.fcstTime
                    if (it.category == CategoryType.POP) POP = fcstValueToInt(it.fcstValue)
                    if (it.category == CategoryType.PTY) PTY = fcstValueToInt(it.fcstValue)
                    if (it.category == CategoryType.REH) REH = fcstValueToInt(it.fcstValue)
                    if (it.category == CategoryType.SKY) SKY = fcstValueToInt(it.fcstValue)
                    if (it.category == CategoryType.TMP) TMP = fcstValueToInt(it.fcstValue)
                    if (it.category == CategoryType.WSD) WSD = fcstValueToDouble(it.fcstValue)
                    this.x = it.nx ?: -1
                    this.y = it.ny ?: -1
                }
            }
        }
        return weatherEntity
    }

}