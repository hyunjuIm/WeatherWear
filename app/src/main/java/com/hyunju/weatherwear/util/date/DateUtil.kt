package com.hyunju.weatherwear.util.date

import java.text.SimpleDateFormat
import java.util.*

val AM = 0..1100
val PM = 1200..2300

fun getTodayDate() = toDateFormat(0)
fun getYesterdayDate(): String = toDateFormat(-1 * (1000 * 60 * 60 * 24))
fun getTomorrowDate(): String = toDateFormat((1000 * 60 * 60 * 24))
fun getYesterdayDate(date: String): String {
    val dataFormat = SimpleDateFormat("yyyyMMdd")
    val currentTime = setStringToCalendar(date).timeInMillis - (1000 * 60 * 60 * 24)
    return dataFormat.format(currentTime)
}

fun toDateFormat(num: Int): String {
    val currentTime: Long = System.currentTimeMillis() + num
    val dataFormat = SimpleDateFormat("yyyyMMdd")
    return dataFormat.format(currentTime)
}

fun getStringCurrentTime(): String = toTimeFormat() + "00"
fun getIntCurrentTime(): Int = toTimeFormat().toInt()
fun toTimeFormat(): String {
    val currentTime: Long = System.currentTimeMillis()
    val dataFormat = SimpleDateFormat("HH")
    return dataFormat.format(currentTime)
}

fun setDateFromString(date: String): Long {
    val dataFormat = SimpleDateFormat("yyyyMMddHHmm")
    return dataFormat.parse(date).time
}

fun setDayOfWeek(date: String): String {
    val input = SimpleDateFormat("yyyyMMdd")
    val output = SimpleDateFormat("E")

    return output.format(input.parse(date) as Date)
}

fun setAmPmFormat(time: Int?): String {
    time ?: return "정보 없음"

    val am = time / 100
    val pm = (time / 100) - 12

    when (time) {
        in AM -> return "오전 ${if (am == 0) 12 else am}시"
        in PM -> return "오후 ${if (pm == 0) 12 else pm}시"
    }

    return "정보 없음"
}

fun setStringToCalendar(date: String): Calendar {
    val input = SimpleDateFormat("yyyyMMdd")
    input.parse(date)
    return input.calendar
}

fun setHangulDateFormat(date: String): String {
    val input = SimpleDateFormat("yyyyMMdd")
    val output = SimpleDateFormat("yyyy년 MM월 dd일 E요일")

    return output.format(input.parse(date) as Date)
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

fun calculateSubtractionDate(start: String, end: String): Int {
    val format = SimpleDateFormat("yyyyMMdd")

    val sec = (format.parse(start).time - format.parse(end).time) / 1000
    return (sec / (24 * 60 * 60)).toInt()
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