package com.hyunju.weatherwear.screen.main.weather

import androidx.annotation.StringRes
import com.hyunju.weatherwear.data.entity.LocationEntity
import com.hyunju.weatherwear.model.TimeWeatherModel
import com.hyunju.weatherwear.model.WeatherModel
import com.hyunju.weatherwear.model.WeekWeatherModel
import com.hyunju.weatherwear.util.weather.Weather

sealed class WeatherState {

    object Uninitialized : WeatherState()

    object Loading : WeatherState()

    sealed class Success : WeatherState() {

        data class Today(
            val location: LocationEntity,
            val weatherInfo: WeatherModel,
            val weatherType: Weather,
            val sensibleTemperature: Int
        ) : Success()

        data class Time(
            val timeWeatherInfo: List<TimeWeatherModel>
        ) : Success()

        data class Week(
            val weekWeatherList: List<WeekWeatherModel>
        ) : Success()
    }

    data class Find(
        val location: LocationEntity?
    ) : WeatherState()

    data class Error(
        @StringRes val messageId: Int
    ) : WeatherState()

}