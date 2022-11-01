package com.hyunju.weatherwear.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.hyunju.weatherwear.util.conventer.RoomTypeConverter
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchResultEntity(
    val name: String,
    val locationLatLng: LocationLatLngEntity
) : Parcelable