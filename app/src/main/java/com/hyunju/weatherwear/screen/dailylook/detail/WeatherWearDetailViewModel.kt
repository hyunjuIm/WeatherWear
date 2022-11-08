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

    val detailStateLiveData =
        MutableLiveData<WeatherWearDetailState>(WeatherWearDetailState.Uninitialized)

    fun getWeatherWearData(id: Long) = viewModelScope.launch(exceptionHandler) {
        detailStateLiveData.value = WeatherWearDetailState.Loading

        val weatherWearInfo = weatherWearRepository.getWeatherWear(id)
        detailStateLiveData.value = WeatherWearDetailState.Success(
            weatherWearInfo = weatherWearInfo
        )

        PhotoDetailActivity.PhotoDetailObject.bitmap = weatherWearInfo.photo
    }

    fun deleteWeatherWearDate(id: Long) = viewModelScope.launch(exceptionHandler) {
        detailStateLiveData.value = WeatherWearDetailState.Loading
        weatherWearRepository.removeWeatherWear(id)
        detailStateLiveData.value = WeatherWearDetailState.Delete

        UpdateEventBus.invokeEvent(UpdateEvent.Updated)
    }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        detailStateLiveData.value = WeatherWearDetailState.Loading
        detailStateLiveData.value = WeatherWearDetailState.Error(message)
    }
}