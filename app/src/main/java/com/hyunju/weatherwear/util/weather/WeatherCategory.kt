package com.hyunju.weatherwear.util.weather

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.util.clothes.Clothes
import com.hyunju.weatherwear.util.clothes.ClothesList
import kotlinx.parcelize.Parcelize

object Time {
    val AFTERNOON = 700..1700
}

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

@Parcelize
enum class Weather(
    val text: String,
    @DrawableRes val image: Int
) : Parcelable {

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

enum class Temperatures(
    val range: IntRange,
    val clothesList: ArrayList<Clothes>
) {

    TEMPERATURE_HIGH(28..100, ClothesList.temperaturesHigh),
    TEMPERATURE_23(23..27, ClothesList.temperatures23),
    TEMPERATURE_20(20..22, ClothesList.temperatures20),
    TEMPERATURE_17(17..19, ClothesList.temperatures17),
    TEMPERATURE_12(12..16, ClothesList.temperatures12),
    TEMPERATURE_9(9..11, ClothesList.temperatures9),
    TEMPERATURE_5(5..8, ClothesList.temperatures5),
    TEMPERATURE_LOW(-100..4, ClothesList.temperaturesLow)

}