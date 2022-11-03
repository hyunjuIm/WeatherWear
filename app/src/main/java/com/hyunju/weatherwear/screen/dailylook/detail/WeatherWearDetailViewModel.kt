package com.hyunju.weatherwear.screen.dailylook.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.data.repository.wear.WeatherWearRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.util.event.UpdateEventBus
import com.hyunju.weatherwear.util.event.UpdateEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherWearDetailViewModel @Inject constructor(
    private val weatherWearRepository: WeatherWearRepository
) : BaseViewModel() {

    val weatherWearDetailLiveData =
        MutableLiveData<WeatherWearDetailState>(WeatherWearDetailState.Uninitialized)

    fun getWeatherWearData(id: Long) = viewModelScope.launch(exceptionHandler) {
        weatherWearDetailLiveData.value = WeatherWearDetailState.Loading
        weatherWearDetailLiveData.value = WeatherWearDetailState.Success(
            weatherWearInfo = weatherWearRepository.getWeatherWear(id)
        )
    }

    fun deleteWeatherWearDate(id: Long) = viewModelScope.launch(exceptionHandler) {
        weatherWearDetailLiveData.value = WeatherWearDetailState.Loading
        weatherWearRepository.removeWeatherWear(id)
        weatherWearDetailLiveData.value = WeatherWearDetailState.Delete

        UpdateEventBus.invokeEvent(UpdateEvent.Updated)
    }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        weatherWearDetailLiveData.value = WeatherWearDetailState.Loading
        weatherWearDetailLiveData.value = WeatherWearDetailState.Error(message)
    }
}