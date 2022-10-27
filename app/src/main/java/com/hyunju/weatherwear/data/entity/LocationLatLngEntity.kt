package com.hyunju.weatherwear.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationLatLngEntity(
    val latitude: Double,
    val longitude: Double
) : Parcelable
