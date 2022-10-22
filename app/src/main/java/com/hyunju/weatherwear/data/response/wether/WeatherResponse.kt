package com.hyunju.weatherwear.data.response.wether


import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("response")
    val response: Response?
)