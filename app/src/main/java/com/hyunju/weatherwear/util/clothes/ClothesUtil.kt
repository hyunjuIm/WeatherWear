package com.hyunju.weatherwear.util.clothes

import com.hyunju.weatherwear.util.weather.Temperatures

// 기온별 랜덤 옷 추천
fun pickClothes(tmx: Int): List<Clothes> {

    val clothesList = when (tmx) {
        in Temperatures.TEMPERATURE_HIGH.range -> Temperatures.TEMPERATURE_HIGH.clothesList
        in Temperatures.TEMPERATURE_23.range -> Temperatures.TEMPERATURE_23.clothesList
        in Temperatures.TEMPERATURE_20.range -> Temperatures.TEMPERATURE_20.clothesList
        in Temperatures.TEMPERATURE_17.range -> Temperatures.TEMPERATURE_17.clothesList
        in Temperatures.TEMPERATURE_12.range -> Temperatures.TEMPERATURE_12.clothesList
        in Temperatures.TEMPERATURE_9.range -> Temperatures.TEMPERATURE_9.clothesList
        in Temperatures.TEMPERATURE_5.range -> Temperatures.TEMPERATURE_5.clothesList
        in Temperatures.TEMPERATURE_LOW.range -> Temperatures.TEMPERATURE_LOW.clothesList
        else -> listOf()
    }

    return clothesList.shuffled().subList(0, 3)
}