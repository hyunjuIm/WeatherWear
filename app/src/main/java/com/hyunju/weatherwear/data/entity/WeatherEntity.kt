package com.hyunju.weatherwear.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.hyunju.weatherwear.data.response.weather.CategoryType
import com.hyunju.weatherwear.data.response.weather.Item
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class WeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
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
) : Parcelable {

    fun toItem() = Item(
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
