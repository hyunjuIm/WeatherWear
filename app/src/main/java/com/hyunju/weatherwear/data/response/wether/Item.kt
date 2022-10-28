package com.hyunju.weatherwear.data.response.wether

import com.google.gson.annotations.SerializedName
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.util.date.getCurrentTime

data class Item(
    @SerializedName("baseDate")
    val baseDate: String?,
    @SerializedName("baseTime")
    val baseTime: String?,
    @SerializedName("category")
    val category: CategoryType?,
    @SerializedName("fcstDate")
    val fcstDate: String?,
    @SerializedName("fcstTime")
    val fcstTime: String?,
    @SerializedName("fcstValue")
    val fcstValue: String?,
    @SerializedName("nx")
    val nx: Int?,
    @SerializedName("ny")
    val ny: Int?
)