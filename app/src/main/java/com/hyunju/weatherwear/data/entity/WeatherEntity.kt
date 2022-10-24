package com.hyunju.weatherwear.data.entity

data class WeatherEntity(
    var date: String,
    var time: String,
    var POP: Int, // 강수확률
    var PTY: Int, // 강수형태
    var REH: Int, // 습도
    var SKY: Int, // 하늘상태
    var TMP: Int, // 1시간 기온
    var TMN: Int, // 일 최저기온
    var TMX: Int, // 일 최고기온
    var WSD: Double, // 풍속
    var x: Int,
    var y: Int
) {
    constructor() : this(
        "", "", -1, -1, -1, -1, -1, -1, -1, -1.0, -1, -1
    )

    fun fcstValueToInt(value: String?) = value?.toFloat()?.toInt() ?: -1
    fun fcstValueToDouble(value: String?) = value?.toDouble() ?: -1.0
}
