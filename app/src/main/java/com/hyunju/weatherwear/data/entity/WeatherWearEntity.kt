package com.hyunju.weatherwear.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class WeatherWearEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val location: String,
    val date: Long,
    val maxTemperature: Int,
    val minTemperature: Int,
    val weatherType: String,
    val photo: String,
    val diary: String
) : Parcelable