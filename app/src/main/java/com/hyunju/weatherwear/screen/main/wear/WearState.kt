package com.hyunju.weatherwear.screen.main.wear

import androidx.annotation.StringRes
import com.hyunju.weatherwear.data.entity.WeatherWearEntity

sealed class WearState {

    object Uninitialized : WearState()

    object Loading : WearState()

    data class Success(
        val weatherWearList: List<WeatherWearEntity>
    ) : WearState()

    data class Error(
        @StringRes val messageId: Int
    ) : WearState()

}