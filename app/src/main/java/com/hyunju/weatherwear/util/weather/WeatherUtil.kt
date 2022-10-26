package com.hyunju.weatherwear.util.weather

import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.data.response.wether.CategoryType
import com.hyunju.weatherwear.data.response.wether.Items
import com.hyunju.weatherwear.util.date.getCurrentTime
import kotlin.math.pow

// api를 통해 받은 날씨 정보 파싱
fun parseWeatherData(weatherInfo: Items): WeatherEntity {
    val weatherEntity = WeatherEntity()

    weatherInfo.item?.forEach {
        weatherEntity.run {
            if (it?.category == CategoryType.TMN) TMN = fcstValueToInt(it.fcstValue)
            if (it?.category == CategoryType.TMX) TMX = fcstValueToInt(it.fcstValue)

            if (it?.fcstTime == getCurrentTime()) {
                date = it.baseDate ?: ""
                time = it.fcstTime
                if (it.category == CategoryType.POP) POP = fcstValueToInt(it.fcstValue)
                if (it.category == CategoryType.PTY) PTY = fcstValueToInt(it.fcstValue)
                if (it.category == CategoryType.REH) REH = fcstValueToInt(it.fcstValue)
                if (it.category == CategoryType.SKY) SKY = fcstValueToInt(it.fcstValue)
                if (it.category == CategoryType.TMP) TMP = fcstValueToInt(it.fcstValue)
                if (it.category == CategoryType.WSD) WSD = fcstValueToDouble(it.fcstValue)
                this.x = it.nx ?: -1
                this.y = it.ny ?: -1
            }
        }
    }
    return weatherEntity
}

// 체감온도
fun getSensibleTemperature(temperatures: Int, windSpeed: Double): Int {
    return (13.12 + 0.6215 * temperatures.toFloat()
            - 11.37 * windSpeed.pow(0.16)
            + 0.3965 * temperatures * windSpeed.pow(0.16)).toInt()
}

// 날씨 이미지, 정보 ui 셋팅
fun setMatchingUiWeatherInfo(weatherInfo: WeatherEntity): Weather {
    val time = weatherInfo.time.toInt()
    val sky = weatherInfo.SKY
    val shape = weatherInfo.PTY

    return when (true) {
        (sky in Sky.SUN && shape == Shape.NONE) -> {
            if (time in Time.AFTERNOON) Weather.SUN else Weather.NIGHT
        }
        (sky in Sky.SUN && shape == Shape.RAIN) -> {
            if (time in Time.AFTERNOON) Weather.SUN_RAINY else Weather.NIGHT_RAINY
        }
        (sky in Sky.SUN && shape == Shape.RAIN_SNOW) -> {
            if (time in Time.AFTERNOON) Weather.SUN_RAINY_SNOWY else Weather.NIGHT_RAINY_SNOWY
        }
        (sky in Sky.SUN && shape == Shape.SNOW) -> {
            if (time in Time.AFTERNOON) Weather.SUN_SNOWY else Weather.NIGHT_SNOWY
        }
        (sky in Sky.SUN && shape == Shape.SHOWER) -> {
            if (time in Time.AFTERNOON) Weather.SUN_SHOWER else Weather.NIGHT_SHOWER
        }

        (sky in Sky.CLOUDY && shape == Shape.NONE) -> Weather.CLOUDY
        (sky in Sky.CLOUDY && shape == Shape.RAIN) -> Weather.CLOUDY_RAINY
        (sky in Sky.CLOUDY && shape == Shape.RAIN_SNOW) -> Weather.CLOUDY_RAINY_SNOWY
        (sky in Sky.CLOUDY && shape == Shape.SNOW) -> Weather.CLOUDY_SNOWY
        (sky in Sky.CLOUDY && shape == Shape.SHOWER) -> Weather.CLOUDY_SHOWER

        (sky in Sky.GRAY_CLOUDY && shape == Shape.NONE) -> Weather.GRAY_CLOUDY
        (sky in Sky.GRAY_CLOUDY && shape == Shape.RAIN) -> Weather.GRAY_CLOUDY_RAINY
        (sky in Sky.GRAY_CLOUDY && shape == Shape.RAIN_SNOW) -> Weather.GRAY_CLOUDY_RAINY_SNOWY
        (sky in Sky.GRAY_CLOUDY && shape == Shape.SNOW) -> Weather.GRAY_CLOUDY_SNOWY
        (sky in Sky.GRAY_CLOUDY && shape == Shape.SHOWER) -> Weather.GRAY_CLOUDY_SHOWER

        else -> Weather.UNKNOWN
    }
}