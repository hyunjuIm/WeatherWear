package com.hyunju.weatherwear.screen.modify

import androidx.annotation.StringRes
import com.hyunju.weatherwear.data.entity.WeatherWearEntity

sealed class ModifyState {

    object Uninitialized : ModifyState()

    object Loading : ModifyState()

    data class Success(
        val weatherWearInfo: WeatherWearEntity
    ) : ModifyState()

    object Modify : ModifyState()

    data class Error(
        @StringRes val messageId: Int
    ) : ModifyState()

}