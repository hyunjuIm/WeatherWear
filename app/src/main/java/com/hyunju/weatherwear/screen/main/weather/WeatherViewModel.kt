package com.hyunju.weatherwear.screen.main.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.LocationEntity
import com.hyunju.weatherwear.data.entity.LocationLatLngEntity
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import com.hyunju.weatherwear.data.response.weather.Items
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.util.conventer.TO_GRID
import com.hyunju.weatherwear.util.conventer.convertGridGPS
import com.hyunju.weatherwear.util.date.getTodayDate
import com.hyunju.weatherwear.util.date.getYesterdayDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val weatherRepository: WeatherRepository
) : BaseViewModel() {

    val weatherStateLiveData = MutableLiveData<WeatherState>(WeatherState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch(exceptionHandler) {
        weatherStateLiveData.value = WeatherState.Loading

        val location = mapRepository.getLocationDataFromDevice()
        weatherStateLiveData.value = WeatherState.Find(
            location = if (location.isEmpty()) null else location.first()
        )
    }

    fun getLocationData(location: LocationLatLngEntity) = viewModelScope.launch(exceptionHandler) {
        weatherStateLiveData.value = WeatherState.Loading

        val grid = convertGridGPS(TO_GRID, location)

        val locationEntity: LocationEntity?

        val responseData = mapRepository.getReverseGeoInformation(
            latitude = grid.lat,
            longitude = grid.lng
        ) ?: run {
            weatherStateLiveData.value = WeatherState.Error(R.string.can_not_load_address_info)
            return@launch
        }

        locationEntity = LocationEntity(
            name = responseData.toLocationNameString(),
            latitude = grid.lat,
            longitude = grid.lng,
            x = grid.x.toInt(),
            y = grid.y.toInt()
        )

        mapRepository.saveLocationDataToDevice(locationEntity)

        weatherStateLiveData.value = WeatherState.Find(
            location = locationEntity
        )
    }

    // 위치로 날씨 정보 받아오기
    fun updateLocationWeather(
        locationEntity: LocationEntity
    ) = viewModelScope.launch(exceptionHandler) {
        weatherStateLiveData.value = WeatherState.Loading
        try {
            val weatherEntityList = getTodayWeatherData(locationEntity) ?: throw Exception()

            val weatherItemList = Items(item = weatherEntityList.map { it.toItem() })

            // 오늘 날씨 정보 반환
            weatherItemList.getDateWeatherModel(getTodayDate())?.let {
                weatherStateLiveData.value = WeatherState.Success.Today(
                    location = locationEntity,
                    weatherInfo = it,
                    weatherType = it.toWeatherType(),
                    sensibleTemperature = it.toSensibleTemperature()
                )
            } ?: throw Exception()

            // 오늘 시간대별 날씨 정보 반환
            val timeWeatherList = weatherItemList.getTimeWeatherModelList()
            timeWeatherList[0].time = "지금"
            weatherStateLiveData.value = WeatherState.Success.Time(
                timeWeatherInfo = timeWeatherList
            )

            // 요일별 일기 예보 정보 반환
            val weekWeatherList = weatherItemList.getWeekWeatherModelList()
            weekWeatherList[0].dayOfWeek = "오늘"
            weatherStateLiveData.value = WeatherState.Success.Week(
                weekWeatherList = weekWeatherList
            )

        } catch (e: Exception) {
            weatherStateLiveData.value = WeatherState.Error(R.string.can_not_load_weather_info)
            return@launch
        }
    }

    private suspend fun getTodayWeatherData(location: LocationEntity): List<WeatherEntity>? {
        val hasWeatherData = weatherRepository.getWeatherItemsFromDevice().filter {
            getTodayDate() == it.baseDate && location.x == it.nx && location.y == it.ny
        }

        val weatherEntityList: List<WeatherEntity>?

        if (hasWeatherData.isNotEmpty()) {
            weatherEntityList = hasWeatherData
        } else {
            val responseData = weatherRepository.getWeather(
                dataType = "JSON",
                numOfRows = 2000,
                pageNo = 1,
                baseDate = getYesterdayDate(),
                baseTime = "2300",
                nx = location.x,
                ny = location.y
            ) ?: run { return null }

            weatherEntityList = responseData.item?.map { item ->
                item.let { it?.toEntity() } ?: kotlin.run { return null }
            } ?: kotlin.run { return null }

            weatherRepository.saveWeatherItemsToDevice(weatherEntityList)
        }

        return weatherEntityList
    }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        weatherStateLiveData.value = WeatherState.Loading
        weatherStateLiveData.value = WeatherState.Error(message)
    }

}