package com.hyunju.weatherwear.screen.modify

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.WeatherWearEntity
import com.hyunju.weatherwear.data.repository.wear.WeatherWearRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.screen.write.WriteState
import com.hyunju.weatherwear.util.event.UpdateEvent
import com.hyunju.weatherwear.util.event.UpdateEventBus
import com.hyunju.weatherwear.util.file.BitmapUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ModifyViewModel @Inject constructor(
    private val weatherWearRepository: WeatherWearRepository
) : BaseViewModel() {

    val modifyStateLiveData = MutableLiveData<ModifyState>(ModifyState.Uninitialized)

    lateinit var weatherWearEntity: WeatherWearEntity

    fun getWeatherWearData(id: Long) = viewModelScope.launch(exceptionHandler) {
        modifyStateLiveData.value = ModifyState.Loading

        weatherWearEntity = weatherWearRepository.getWeatherWear(id)
        modifyStateLiveData.value = ModifyState.Success(
            weatherWearInfo = weatherWearEntity
        )
    }

    fun modifyWeatherWear(uri: Uri?, diary: String) = viewModelScope.launch(exceptionHandler) {
        modifyStateLiveData.value = ModifyState.Loading

        try {
            val bitmap = withContext(Dispatchers.IO) {
                uri?.let { BitmapUtil.setImageBitmap(it) }
            } ?: kotlin.run { weatherWearEntity.photo }

            weatherWearRepository.modifyWeatherWear(
                weatherWearEntity.copy(
                    photo = bitmap,
                    diary = diary
                )
            )

            modifyStateLiveData.value = ModifyState.Modify

            UpdateEventBus.invokeEvent(UpdateEvent.Updated)

        } catch (e: Exception) {
            modifyStateLiveData.value = ModifyState.Error(R.string.request_error)
            return@launch
        }
    }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        modifyStateLiveData.value = ModifyState.Loading
        modifyStateLiveData.value = ModifyState.Error(message)
    }

}