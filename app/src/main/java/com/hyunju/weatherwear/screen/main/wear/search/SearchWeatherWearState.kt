package com.hyunju.weatherwear.screen.main.wear.search

import androidx.annotation.StringRes
import com.hyunju.weatherwear.data.entity.WeatherWearEntity

sealed class SearchWeatherWearState {

    object Uninitialized : SearchWeatherWearState()

    object Loading : SearchWeatherWearState()

    data class Success(
        val weatherWearList: List<WeatherWearEntity>
    ) : SearchWeatherWearState()

    data class Error(
        @StringRes val messageId: Int
    ) : SearchWeatherWearState()
}