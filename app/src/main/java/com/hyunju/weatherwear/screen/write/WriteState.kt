package com.hyunju.weatherwear.screen.write

import androidx.annotation.StringRes
import com.hyunju.weatherwear.data.entity.LocationLatLngEntity
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.util.weather.Weather

sealed class WriteState {

    object Uninitialized : WriteState()

    object Loading : WriteState()

    data class Success(
        val location: LocationLatLngEntity,
        val weatherInfo: WeatherEntity,
        val weatherType: Weather
    ) : WriteState()

    data class Register(
        val id: Long
    ) : WriteState()

    data class Error(
        @StringRes val messageId: Int
    ) : WriteState()
}