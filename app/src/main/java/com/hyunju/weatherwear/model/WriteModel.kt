package com.hyunju.weatherwear.model

import android.net.Uri
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import java.util.*

data class WriteModel(
    val date: Calendar,
    val location: String,
    val weather: WeatherModel,
    val photo: Uri,
    val diary: String
)