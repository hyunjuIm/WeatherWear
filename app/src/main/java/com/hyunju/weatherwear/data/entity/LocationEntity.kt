package com.hyunju.weatherwear.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class LocationEntity(
    @PrimaryKey val name: String,
    val latitude: Double,
    val longitude: Double,
    val x: Int,
    val y: Int
) : Parcelable