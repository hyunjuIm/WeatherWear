package com.hyunju.weatherwear.util.date

import java.text.SimpleDateFormat
import java.util.*

fun getTodayDate(): String {
    val currentTime: Long = System.currentTimeMillis() - (1000 * 60 * 60 * 24)
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

fun setStringDateFormat(date: String): String {
    val input = SimpleDateFormat("yyyyMMdd")
    val output = SimpleDateFormat("yyyy.MM.dd")

    return output.format(input.parse(date) as Date)
}

fun setMillisDateFormat(date: Long): String {
    val dataFormat = SimpleDateFormat("yyyy.MM.dd")
    return dataFormat.format(date)
}

fun setMillisDateFormatForApi(date: Long): String {
    val dataFormat = SimpleDateFormat("yyyyMMdd")
    return dataFormat.format(date)
}