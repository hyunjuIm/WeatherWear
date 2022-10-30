package com.hyunju.weatherwear.screen.write

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.LocationLatLngEntity
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.util.location.TO_GRID
import com.hyunju.weatherwear.util.location.convertGridGPS
import com.hyunju.weatherwear.util.weather.getWeatherType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : BaseViewModel() {

    val writeStateLiveData = MutableLiveData<WriteState>(WriteState.Uninitialized)

    // 위치로 날씨 정보 받아오기
    fun getWeatherInformation(locationLatLngEntity: LocationLatLngEntity, date: String) =
        viewModelScope.launch(exceptionHandler) {
            writeStateLiveData.value = WriteState.Loading

            val grid = convertGridGPS(
                TO_GRID,
                locationLatLngEntity.latitude,
                locationLatLngEntity.longitude
            )
            val weatherItems = weatherRepository.getWeather(
                dataType = "JSON",
                numOfRows = 500,
                pageNo = 1,
                baseDate = date,
                baseTime = "0200",
                nx = grid.x.toInt(),
                ny = grid.y.toInt()
            ) ?: run {
                writeStateLiveData.value = WriteState.Error(R.string.can_not_load_weather_info)
                return@launch
            }

            weatherItems.toEntity()?.let {
                writeStateLiveData.value = WriteState.Success(
                    location = locationLatLngEntity,
                    weatherInfo = it,
                    weatherType = getWeatherType(it)
                )
            } ?: run {
                writeStateLiveData.value = WriteState.Error(R.string.can_not_load_weather_info)
                return@launch
            }
        }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        writeStateLiveData.value = WriteState.Loading
        writeStateLiveData.value = WriteState.Error(message)
    }

}