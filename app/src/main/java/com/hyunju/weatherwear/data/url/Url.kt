package com.hyunju.weatherwear.data.url

import com.hyunju.weatherwear.BuildConfig

object Url {

    const val TMAP_URL = "https://apis.openapi.sk.com"

    const val GET_TMAP_LOCATION = "/tmap/pois"

    const val GET_TMAP_REVERSE_GEO_CODE = "/tmap/geo/reversegeocoding"

    const val WEATHER_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"

    const val GET_VILAGE_FCST = "getVilageFcst?serviceKey=${BuildConfig.WEATHER_API}"

}