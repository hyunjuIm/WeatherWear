package com.hyunju.weatherwear.screen.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.LocationEntity
import com.hyunju.weatherwear.data.entity.LocationLatLngEntity
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.data.repository.wear.WeatherWearRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import com.hyunju.weatherwear.data.response.weather.Items
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.util.conventer.TO_GRID
import com.hyunju.weatherwear.util.conventer.convertGridGPS
import com.hyunju.weatherwear.util.date.getTodayDate
import com.hyunju.weatherwear.util.date.getYesterdayDate
import com.hyunju.weatherwear.util.event.UpdateEventBus
import com.hyunju.weatherwear.util.event.UpdateEvent
import com.hyunju.weatherwear.util.weather.getCommentWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val weatherRepository: WeatherRepository,
    private val weatherWearRepository: WeatherWearRepository
) : BaseViewModel() {

    val homeStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch(exceptionHandler) {
        homeStateLiveData.value = HomeState.Loading

        homeStateLiveData.value = HomeState.Pick(
            weatherWearEntity = weatherWearRepository.getWeatherWearLatestItem(getTodayDate())
        )

        val location = mapRepository.getLocationDataFromDevice()
        homeStateLiveData.value = HomeState.Find(
            location = if (location.isEmpty()) null else location.first()
        )
    }

    fun getLocationData(location: LocationLatLngEntity) = viewModelScope.launch(exceptionHandler) {
        homeStateLiveData.value = HomeState.Loading

        val grid = convertGridGPS(TO_GRID, location)

        val locationEntity: LocationEntity?

        val responseData = mapRepository.getReverseGeoInformation(
            latitude = grid.lat,
            longitude = grid.lng
        ) ?: run {
            homeStateLiveData.value = HomeState.Error(R.string.can_not_load_address_info)
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

        homeStateLiveData.value = HomeState.Find(
            location = locationEntity
        )
    }

    // ????????? ?????? ?????? ????????????
    fun updateLocationWeather(locationEntity: LocationEntity) =
        viewModelScope.launch(exceptionHandler) {
            homeStateLiveData.value = HomeState.Loading

            val weatherItemList = getTodayWeatherData(locationEntity) ?: run {
                homeStateLiveData.value = HomeState.Error(R.string.can_not_load_weather_info)
                return@launch
            }

            Items(item = weatherItemList.map { it.toItem() })
                .getDateWeatherModel(getTodayDate())?.let {
                    val commentList = getCommentWeather(it)
                    homeStateLiveData.value = HomeState.Success(
                        location = locationEntity,
                        weatherInfo = it,
                        weatherType = it.toWeatherType(),
                        sensibleTemperature = it.toSensibleTemperature(),
                        comment = commentList[0] + "\n" + commentList[1]
                    )
                } ?: run {
                homeStateLiveData.value = HomeState.Error(R.string.can_not_load_weather_info)
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
        homeStateLiveData.value = HomeState.Loading
        homeStateLiveData.value = HomeState.Error(message)
    }

    private val _updateUIState = MutableLiveData<Boolean>()
    val updateUIState = _updateUIState

    init {
        initEventBusSubscribe()
    }

    private fun initEventBusSubscribe() {
        viewModelScope.launch {
            UpdateEventBus.subscribeEvent {
                when (it) {
                    UpdateEvent.Updated -> _updateUIState.value = true
                    UpdateEvent.UnUpdated -> _updateUIState.value = false
                }
            }
        }
    }
}