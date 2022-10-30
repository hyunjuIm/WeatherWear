package com.hyunju.weatherwear.model

import android.net.Uri
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.data.entity.WeatherEntity
import java.util.*

data class WriteModel(
    val date: Calendar,
    val location: SearchResultEntity,
    val weather:WeatherEntity,
    val photo: Uri,
    val diary: String
)