package com.hyunju.weatherwear.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherEntity(
    var date: String,
    var time: String,
    var POP: Int, // 강수확률 - 맑음(0~5), 구름많음(6~8), 흐림(9~10)
    var PTY: Int, // 강수형태 - 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
    var REH: Int, // 습도
    var SKY: Int, // 하늘상태
    var TMP: Int, // 1시간 기온
    var TMN: Int, // 일 최저기온
    var TMX: Int, // 일 최고기온
    var WSD: Double, // 풍속 - 약함(0~3), 약간강(4~8), 강(9~13), 매우강(14~)
    var x: Int,
    var y: Int
) : Parcelable {
    constructor() : this(
        "", "", -1, -1, -1, -1, -1, -1, -1, -1.0, -1, -1
    )

    fun fcstValueToInt(value: String?) = value?.toFloat()?.toInt() ?: -1
    fun fcstValueToDouble(value: String?) = value?.toDouble() ?: -1.0
}
