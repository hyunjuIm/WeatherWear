package com.hyunju.weatherwear.screen.write

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.LocationLatLngEntity
import com.hyunju.weatherwear.data.entity.WeatherWearEntity
import com.hyunju.weatherwear.data.repository.wear.WeatherWearRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import com.hyunju.weatherwear.model.WriteModel
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.util.conventer.TO_GRID
import com.hyunju.weatherwear.util.conventer.convertGridGPS
import com.hyunju.weatherwear.util.weather.getWeatherType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val weatherWearRepository: WeatherWearRepository
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

    fun uploadWeatherWear(writeModel: WriteModel) = viewModelScope.launch(exceptionHandler) {
        writeStateLiveData.value = WriteState.Loading

        val weatherWear = WeatherWearEntity(
            location = writeModel.location.name,
            date = writeModel.date.timeInMillis,
            maxTemperature = writeModel.weather.TMX,
            minTemperature = writeModel.weather.TMN,
            weatherType = getWeatherType(writeModel.weather).text,
            photo = writeModel.photo.toString(),
            diary = writeModel.diary
        )

        writeStateLiveData.value = WriteState.Register(
            weatherWearRepository.insertWeatherWear(weatherWear)
        )
    }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        writeStateLiveData.value = WriteState.Loading
        writeStateLiveData.value = WriteState.Error(message)
    }

}