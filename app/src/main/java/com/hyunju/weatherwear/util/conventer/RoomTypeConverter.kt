package com.hyunju.weatherwear.util.conventer

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.hyunju.weatherwear.data.entity.LocationLatLngEntity

@ProvidedTypeConverter
object RoomTypeConverter {

    @TypeConverter
    @JvmStatic
    fun listToJson(value: LocationLatLngEntity): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun jsonToList(value: String): LocationLatLngEntity {
        return Gson().fromJson(value, LocationLatLngEntity::class.java)
    }

}