package com.hyunju.weatherwear.util.date

import java.text.SimpleDateFormat
import java.util.*

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

fun getTimeUsingInWorkRequest(): Long {
    val currentDate = Calendar.getInstance()
    val dueDate = Calendar.getInstance()

    dueDate.set(Calendar.HOUR_OF_DAY, 8)
    dueDate.set(Calendar.MINUTE, 0)
    dueDate.set(Calendar.SECOND, 0)

    if (dueDate.before(currentDate)) {
        dueDate.add(Calendar.HOUR_OF_DAY, 24)
    }

    return dueDate.timeInMillis - currentDate.timeInMillis
}