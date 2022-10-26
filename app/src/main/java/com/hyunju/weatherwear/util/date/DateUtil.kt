package com.hyunju.weatherwear.util.date

import java.text.SimpleDateFormat

fun getTodayDate(): String {
    val currentTime: Long = System.currentTimeMillis()
    val dataFormat = SimpleDateFormat("yyyyMMdd")
    return dataFormat.format(currentTime)
}

fun getCurrentTime(): String {
    val currentTime: Long = System.currentTimeMillis()
    val dataFormat = SimpleDateFormat("HH")

    val now = dataFormat.format(currentTime)

    if (now == "00" || now == "01" || now == "02") return "0300"
    if (now == "24") return "2300"

    return dataFormat.format(currentTime) + "00"
}