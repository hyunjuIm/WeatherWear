package com.hyunju.weatherwear.screen.dailylook.detail

import androidx.annotation.StringRes
import com.hyunju.weatherwear.data.entity.WeatherWearEntity

sealed class WeatherWearDetailState {

    object Uninitialized : WeatherWearDetailState()

    object Loading : WeatherWearDetailState()

    data class Success(
        val weatherWearInfo: WeatherWearEntity
    ) : WeatherWearDetailState()

    object Delete : WeatherWearDetailState()

    data class Error(
        @StringRes val messageId: Int
    ) : WeatherWearDetailState()
}