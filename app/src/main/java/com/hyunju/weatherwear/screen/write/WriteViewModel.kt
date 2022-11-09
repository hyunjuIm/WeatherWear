package com.hyunju.weatherwear.screen.write

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.*
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.data.repository.wear.WeatherWearRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import com.hyunju.weatherwear.data.response.weather.Items
import com.hyunju.weatherwear.model.WriteModel
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.util.conventer.LatXLngY
import com.hyunju.weatherwear.util.conventer.TO_GRID
import com.hyunju.weatherwear.util.conventer.convertGridGPS
import com.hyunju.weatherwear.util.date.calculateSubtractionDate
import com.hyunju.weatherwear.util.date.getTodayDate
import com.hyunju.weatherwear.util.date.getYesterdayDate
import com.hyunju.weatherwear.util.date.setMillisDateFormatForApi
import com.hyunju.weatherwear.util.event.UpdateEventBus
import com.hyunju.weatherwear.util.event.UpdateEvent
import com.hyunju.weatherwear.util.file.BitmapUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val weatherRepository: WeatherRepository,
    private val weatherWearRepository: WeatherWearRepository
) : BaseViewModel() {

    val writeStateLiveData = MutableLiveData<WriteState>(WriteState.Uninitialized)

    // DB 에서 위치 정보 꺼내와서 조회 - 오늘 날짜로 넘기기
    override fun fetchData(): Job = viewModelScope.launch(exceptionHandler) {
        val location = mapRepository.getLocationDataFromDevice()

        if (location.isNotEmpty()) {
            getWeatherInformation(
                searchResultEntity = SearchResultEntity(
                    name = location.first().name,
                    locationLatLng = LocationLatLngEntity(
                        location.first().latitude,
                        location.first().longitude
                    )
                ), date = getTodayDate()
            )
        }
    }

    // 날씨 정보 받아오기
    fun getWeatherInformation(searchResultEntity: SearchResultEntity, date: String) =
        viewModelScope.launch(exceptionHandler) {
            writeStateLiveData.value = WriteState.Loading

            // 최근 3일이 아니면 조회 없이 반환
            val calculateDate = calculateSubtractionDate(start = getTodayDate(), end = date)
            if (calculateDate > 2) {
                writeStateLiveData.value = WriteState.Fail
                return@launch
            }

            val grid = convertGridGPS(TO_GRID, searchResultEntity.locationLatLng)

            val hasWeatherData = weatherRepository.getWeatherItemsFromDevice().filter {
                date == it.fcstDate && grid.x.toInt() == it.nx && grid.y.toInt() == it.ny
            }

            val weatherEntityList =
                hasWeatherData.ifEmpty { getWeatherDataFromAPI(grid, date) } ?: run {
                    writeStateLiveData.value = WriteState.Fail
                    return@launch
                }

            Items(item = weatherEntityList.map { it.toItem() }).getDateWeatherModel(date)?.let {
                writeStateLiveData.value = WriteState.Success(
                    location = searchResultEntity,
                    weatherInfo = it,
                    weatherType = it.toWeatherType().text
                )
            } ?: run {
                writeStateLiveData.value = WriteState.Error(R.string.can_not_load_weather_info)
            }
        }

    private suspend fun getWeatherDataFromAPI(grid: LatXLngY, date: String): List<WeatherEntity>? {
        val weatherEntityList: List<WeatherEntity>?

        val responseData = weatherRepository.getWeather(
            dataType = "JSON",
            numOfRows = 600,
            pageNo = 1,
            baseDate = getYesterdayDate(date),
            baseTime = "2300",
            nx = grid.x.toInt(),
            ny = grid.y.toInt()
        ) ?: run { return null }

        weatherEntityList = responseData.item?.map { item ->
            item.let { it?.toEntity() } ?: kotlin.run { return null }
        } ?: kotlin.run { return null }

        return weatherEntityList
    }

    fun uploadWeatherWear(writeModel: WriteModel) = viewModelScope.launch(exceptionHandler) {
        writeStateLiveData.value = WriteState.Loading

        val photo = withContext(Dispatchers.IO) {
            BitmapUtil.setImageBitmap(writeModel.photo)
        } ?: kotlin.run {
            writeStateLiveData.value = WriteState.Error(R.string.request_error)
            return@launch
        }

        val dateText = setMillisDateFormatForApi(writeModel.date.timeInMillis)

        val weatherWear = if (writeModel.weather == null) {
            WeatherWearEntity(
                location = writeModel.location,
                createDate = writeModel.date.timeInMillis,
                date = writeModel.date.time,
                dateText = dateText,
                maxTemperature = null,
                minTemperature = null,
                weatherType = null,
                photo = photo,
                diary = writeModel.diary
            )
        } else {
            WeatherWearEntity(
                location = writeModel.location,
                createDate = writeModel.date.timeInMillis,
                date = writeModel.date.time,
                dateText = dateText,
                maxTemperature = writeModel.weather.TMX,
                minTemperature = writeModel.weather.TMN,
                weatherType = writeModel.weather.toWeatherType().text,
                photo = photo,
                diary = writeModel.diary
            )
        }

        writeStateLiveData.value = WriteState.Register(
            weatherWearRepository.insertWeatherWear(weatherWear)
        )

        UpdateEventBus.invokeEvent(UpdateEvent.Updated)
    }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        writeStateLiveData.value = WriteState.Loading
        writeStateLiveData.value = WriteState.Error(message)
    }

}