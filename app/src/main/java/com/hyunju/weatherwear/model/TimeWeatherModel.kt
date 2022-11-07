package com.hyunju.weatherwear.model

import androidx.annotation.DrawableRes

data class TimeWeatherModel(
    val date: String,
    var time: String,
    @DrawableRes val icon: Int,
    val temperature: Int
)