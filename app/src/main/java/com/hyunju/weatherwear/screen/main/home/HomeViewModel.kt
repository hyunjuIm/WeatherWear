package com.hyunju.weatherwear.screen.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.util.date.getTodayDate
import com.hyunju.weatherwear.util.location.TO_GRID
import com.hyunju.weatherwear.util.location.convertGridGPS
import com.hyunju.weatherwear.util.weather.getCommentWeather
import com.hyunju.weatherwear.util.weather.getMatchingUiWeatherInfo
import com.hyunju.weatherwear.util.weather.getSensibleTemperature
import com.hyunju.weatherwear.util.weather.parseWeatherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val weatherRepository: WeatherRepository
) : BaseViewModel() {

    val homeStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)

    // 위치로 날씨 정보 받아오기
    fun updateLocationWeather(latitude: Double, longitude: Double) =
        viewModelScope.launch(exceptionHandler) {
            homeStateLiveData.value = HomeState.Loading

            val addressInfo = mapRepository.getReverseGeoInformation(latitude, longitude)
            val location = addressInfo?.toSearchInfoEntity(latitude, longitude) ?: run {
                homeStateLiveData.value = HomeState.Error(R.string.can_not_load_address_info)
                return@launch
            }

            val grid = convertGridGPS(TO_GRID, latitude, longitude)
            val weatherInfo = weatherRepository.getWeather(
                dataType = "JSON",
                numOfRows = 500,
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
                weatherInfo = weatherEntity,
                weatherType = getMatchingUiWeatherInfo(weatherEntity),
                sensibleTemperature = getSensibleTemperature(weatherEntity.TMP, weatherEntity.WSD),
                comment = getCommentWeather(weatherEntity)
            )
        }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        homeStateLiveData.value = HomeState.Loading
        homeStateLiveData.value = HomeState.Error(message)
    }
}