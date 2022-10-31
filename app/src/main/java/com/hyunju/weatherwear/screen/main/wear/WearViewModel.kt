package com.hyunju.weatherwear.screen.main.wear

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.data.entity.WeatherWearEntity
import com.hyunju.weatherwear.data.repository.wear.WeatherWearRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.screen.write.gallery.GalleryState
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

}