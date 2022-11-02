package com.hyunju.weatherwear.screen.main.wear.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.data.repository.wear.WeatherWearRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
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
            weatherWearList = weatherWearRepository.getSearchDateWeatherWears(date)
        )
    }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Loading
        searchWeatherWearStateLiveData.value = SearchWeatherWearState.Error(message)
    }
}