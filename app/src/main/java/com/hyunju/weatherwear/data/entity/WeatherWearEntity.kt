package com.hyunju.weatherwear.data.entity

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity
data class WeatherWearEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val location: String,
    val createDate: Long,
    val date: Date,
    val maxTemperature: Int,
    val minTemperature: Int,
    val weatherType: String,
    val photo: Bitmap,
    val diary: String
) : Parcelable
