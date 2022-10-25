package com.hyunju.weatherwear.util.weather

import androidx.annotation.DrawableRes
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.WeatherEntity
import kotlin.math.pow

fun getSensibleTemperature(temperatures: Int, windSpeed: Double): Int {
    return (13.12 + 0.6215 * temperatures.toFloat()
            - 11.37 * windSpeed.pow(0.16)
            + 0.3965 * temperatures * windSpeed.pow(0.16)).toInt()
}

fun setMatchingUiWeatherInfo(weatherInfo: WeatherEntity): Weather {
    return when (true) {
        (weatherInfo.SKY in Sky.SUN && weatherInfo.PTY == Shape.NONE) -> {
            if (weatherInfo.time.toInt() in AFTERNOON) Weather.SUN else Weather.NIGHT
        }
        (weatherInfo.SKY in Sky.SUN && weatherInfo.PTY == Shape.RAIN) -> {
            if (weatherInfo.time.toInt() in AFTERNOON) Weather.SUN_RAINY else Weather.NIGHT_RAINY
        }
        (weatherInfo.SKY in Sky.SUN && weatherInfo.PTY == Shape.RAIN_SNOW) -> {
            if (weatherInfo.time.toInt() in AFTERNOON) Weather.SUN_RAINY_SNOWY else Weather.NIGHT_RAINY_SNOWY
        }
        (weatherInfo.SKY in Sky.SUN && weatherInfo.PTY == Shape.SNOW) -> {
            if (weatherInfo.time.toInt() in AFTERNOON) Weather.SUN_SNOWY else Weather.NIGHT_SNOWY
        }
        (weatherInfo.SKY in Sky.SUN && weatherInfo.PTY == Shape.SHOWER) -> {
            if (weatherInfo.time.toInt() in AFTERNOON) Weather.SUN_SHOWER else Weather.NIGHT_SHOWER
        }

        (weatherInfo.SKY in Sky.CLOUDY && weatherInfo.PTY == Shape.NONE) -> Weather.CLOUDY
        (weatherInfo.SKY in Sky.CLOUDY && weatherInfo.PTY == Shape.RAIN) -> Weather.CLOUDY_RAINY
        (weatherInfo.SKY in Sky.CLOUDY && weatherInfo.PTY == Shape.RAIN_SNOW) -> Weather.CLOUDY_RAINY_SNOWY
        (weatherInfo.SKY in Sky.CLOUDY && weatherInfo.PTY == Shape.SNOW) -> Weather.CLOUDY_SNOWY
        (weatherInfo.SKY in Sky.CLOUDY && weatherInfo.PTY == Shape.SHOWER) -> Weather.CLOUDY_SHOWER

        (weatherInfo.SKY in Sky.GRAY_CLOUDY && weatherInfo.PTY == Shape.NONE) -> Weather.GRAY_CLOUDY
        (weatherInfo.SKY in Sky.GRAY_CLOUDY && weatherInfo.PTY == Shape.RAIN) -> Weather.GRAY_CLOUDY_RAINY
        (weatherInfo.SKY in Sky.GRAY_CLOUDY && weatherInfo.PTY == Shape.RAIN_SNOW) -> Weather.GRAY_CLOUDY_RAINY_SNOWY
        (weatherInfo.SKY in Sky.GRAY_CLOUDY && weatherInfo.PTY == Shape.SNOW) -> Weather.GRAY_CLOUDY_SNOWY
        (weatherInfo.SKY in Sky.GRAY_CLOUDY && weatherInfo.PTY == Shape.SHOWER) -> Weather.GRAY_CLOUDY_SHOWER

        else -> Weather.UNKNOWN
    }
}

val AFTERNOON = 7..18

object Sky {
    val SUN = 0..5
    val CLOUDY = 6..8
    val GRAY_CLOUDY = 9..10
}

object Shape {
    const val NONE = 0
    const val RAIN = 1
    const val RAIN_SNOW = 2
    const val SNOW = 3
    const val SHOWER = 4
}

enum class Weather(
    val text: String,
    @DrawableRes val image: Int
) {

    SUN("맑음", R.drawable.weather_sun),
    SUN_RAINY("비", R.drawable.weather_sun_rainy),
    SUN_RAINY_SNOWY("비/눈", R.drawable.weather_cloudy_rainy_snowy),
    SUN_SNOWY("눈", R.drawable.weather_sun_snowy),
    SUN_SHOWER("소나기", R.drawable.weather_sun_rainy),

    NIGHT("맑음", R.drawable.weather_night),
    NIGHT_RAINY("비", R.drawable.weather_night_rainy),
    NIGHT_RAINY_SNOWY("비/눈", R.drawable.weather_cloudy_rainy_snowy),
    NIGHT_SNOWY("눈", R.drawable.weather_night_snowy),
    NIGHT_SHOWER("소나기", R.drawable.weather_night_rainy),

    CLOUDY("구름 많음", R.drawable.weather_cloudy),
    CLOUDY_RAINY("구름 많고 비", R.drawable.weather_rainy),
    CLOUDY_RAINY_SNOWY("구름 많고 비/눈", R.drawable.weather_cloudy_rainy_snowy),
    CLOUDY_SNOWY("구름 많고 눈", R.drawable.weather_snowy),
    CLOUDY_SHOWER("구름 많고 소나기", R.drawable.weather_rainy),

    GRAY_CLOUDY("흐림", R.drawable.weather_gray_cloudy),
    GRAY_CLOUDY_RAINY("흐리고 비", R.drawable.weather_gray_cloudy_rainy),
    GRAY_CLOUDY_RAINY_SNOWY("흐리고 비/눈", R.drawable.weather_cloudy_rainy_snowy),
    GRAY_CLOUDY_SNOWY("흐리고 눈", R.drawable.weather_gray_cloudy_snowy),
    GRAY_CLOUDY_SHOWER("흐리고 소나기", R.drawable.weather_gray_cloudy_rainy),

    UNKNOWN("미측정", R.drawable.weather_cloudy)

}