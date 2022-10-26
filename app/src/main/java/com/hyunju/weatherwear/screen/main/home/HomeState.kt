package com.hyunju.weatherwear.screen.main.home

import androidx.annotation.StringRes
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.util.weather.Weather

sealed class HomeState {

    object Uninitialized : HomeState()

    object Loading : HomeState()

    data class Success(
        val location: String,
        val weatherInfo: WeatherEntity,
        val weatherType:Weather,
        val sensibleTemperature:Int,
        val comment: String
    ) : HomeState()

    data class Error(
        @StringRes val messageId: Int
    ) : HomeState()
}