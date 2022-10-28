package com.hyunju.weatherwear.data.response.weather


import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("response")
    val response: Response?
)