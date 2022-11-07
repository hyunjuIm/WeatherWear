package com.hyunju.weatherwear.model

import androidx.annotation.DrawableRes

data class WeekWeatherModel(
    val date: String,
    var dayOfWeek: String,
    @DrawableRes val icon: Int,
    val maxTemperature: Int,
    val minTemperature: Int
)
