package com.hyunju.weatherwear.screen.main.wear.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.data.repository.wear.WeatherWearRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.screen.dialog.SelectTemperatureBottomSheetDialog
import com.hyunju.weatherwear.util.date.setMillisDateFormat
import com.hyunju.weatherwear.util.weather.Temperatures
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchWeatherWearViewModel @Inject constructor(
    private val weatherWearRepository: WeatherWearRepository
) : BaseViewModel() {

    val searchWeatherWearStateLiveData =
        MutableLiveData<SearchWeatherWearState>(SearchWeatherWearState.Uninitialized)

    fun searchDate(date: Calendar) = viewModelScope.launch(exceptionHandler) {
        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Loading

        val start = Calendar.getInstance().apply {
            set(
                date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
                0, 0, 0
            )
        }.time
        val end = Calendar.getInstance().apply {
            set(
                date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
                23, 59, 59
            )
        }.time

        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Success(
            searchText = setMillisDateFormat(date.timeInMillis),
            weatherWearList = weatherWearRepository.getSearchDateWeatherWears(start, end)
        )
    }

    fun searchTemperature(standard: String, temperature: Temperatures) =
        viewModelScope.launch(exceptionHandler) {
            searchWeatherWearStateLiveData.value = SearchWeatherWearState.Loading

            val searchText = when (temperature) {
                Temperatures.TEMPERATURE_HIGH -> "${temperature.range.first}° 이상"
                Temperatures.TEMPERATURE_LOW -> "${temperature.range.last}° 이하"
                else -> "${temperature.range.first}° ~ ${temperature.range.last}°"
            }

            val weatherWearList = when (standard) {
                SelectTemperatureBottomSheetDialog.MAX -> {
                    weatherWearRepository.getSearchMaxTemperatureRangeWeatherWears(
                        temperature.range.first,
                        temperature.range.last
                    )
                }
                SelectTemperatureBottomSheetDialog.MIN -> {
                    weatherWearRepository.getSearchMinTemperatureRangeWeatherWears(
                        temperature.range.first,
                        temperature.range.last
                    )
                }
                else -> listOf()
            }

            searchWeatherWearStateLiveData.value = SearchWeatherWearState.Success(
                searchText = searchText,
                weatherWearList = weatherWearList
            )
        }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Loading
        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Error(message)
    }
}