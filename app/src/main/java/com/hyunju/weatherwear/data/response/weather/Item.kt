package com.hyunju.weatherwear.data.response.weather

import com.google.gson.annotations.SerializedName
import com.hyunju.weatherwear.data.entity.WeatherEntity

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
) {
    fun toEntity() = WeatherEntity(
        baseDate = baseDate,
        baseTime = baseTime,
        category = category,
        fcstDate = fcstDate,
        fcstTime = fcstTime,
        fcstValue = fcstValue,
        nx = nx,
        ny = ny
    )
}
