package com.hyunju.weatherwear.screen.main.wear

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
class WearViewModel @Inject constructor(
    private val weatherWearRepository: WeatherWearRepository
) : BaseViewModel() {

    val wearStateLiveDate = MutableLiveData<WearState>(WearState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch(exceptionHandler) {
        wearStateLiveDate.value = WearState.Loading
        wearStateLiveDate.value = WearState.Success(
            weatherWearList = weatherWearRepository.getAllWeatherWears()
        )
    }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        wearStateLiveDate.value = WearState.Loading
        wearStateLiveDate.value = WearState.Error(message)
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