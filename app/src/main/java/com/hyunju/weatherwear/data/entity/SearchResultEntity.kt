package com.hyunju.weatherwear.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchResultEntity(
    val name: String,
    val locationLatLng: LocationLatLngEntity
) : Parcelable