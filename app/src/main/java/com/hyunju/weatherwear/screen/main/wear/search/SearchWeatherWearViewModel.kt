package com.hyunju.weatherwear.screen.main.wear.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.data.repository.wear.WeatherWearRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.util.date.setMillisDateFormat
import com.hyunju.weatherwear.util.weather.Temperatures
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchWeatherWearViewModel @Inject constructor(
    private val weatherWearRepository: WeatherWearRepository
) : BaseViewModel() {

    val searchWeatherWearStateLiveData =
        MutableLiveData<SearchWeatherWearState>(SearchWeatherWearState.Uninitialized)

    fun searchDate(date: Long) = viewModelScope.launch(exceptionHandler) {
        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Loading
        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Success(
            searchText = setMillisDateFormat(date),
            weatherWearList = weatherWearRepository.getSearchDateWeatherWears(date)
        )
    }

    fun searchTemperature(temperature: Temperatures) = viewModelScope.launch(exceptionHandler) {
        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Loading

        val searchText = when (temperature) {
            Temperatures.TEMPERATURE_HIGH -> "${temperature.range.first}° 이상"
            Temperatures.TEMPERATURE_LOW -> "${temperature.range.last}° 이하"
            else -> "${temperature.range.first}° ~ ${temperature.range.last}°"
        }

        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Success(
            searchText = searchText,
            weatherWearList = weatherWearRepository.getSearchTemperatureRangeWeatherWears(
                temperature.range.first,
                temperature.range.last
            )
        )
    }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Loading
        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Error(message)
    }
}