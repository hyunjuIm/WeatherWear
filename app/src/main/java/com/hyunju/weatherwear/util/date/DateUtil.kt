package com.hyunju.weatherwear.util.date

import java.text.SimpleDateFormat
import java.util.*

val AM = 0..1100
val PM = 1200..2300

// String 형태로 날짜 반환 : yyyyMMdd
fun getTodayDate(): String = toDateFormat(0)
fun getYesterdayDate(): String = toDateFormat(-1 * (1000 * 60 * 60 * 24))
fun getTomorrowDate(): String = toDateFormat((1000 * 60 * 60 * 24))
fun toDateFormat(num: Int): String {
    val currentTime: Long = System.currentTimeMillis() + num
    val dataFormat = SimpleDateFormat("yyyyMMdd")
    return dataFormat.format(currentTime)
}

// String 형태 -1일 -> 어제 날짜 계산
fun getStringYesterdayDate(date: String): String {
    val dataFormat = SimpleDateFormat("yyyyMMdd")
    val currentTime = setStringToCalendar(date).timeInMillis - (1000 * 60 * 60 * 24)
    return dataFormat.format(currentTime)
}

// String 형태로 시간 반환 -> HH00
fun getNowTime(): String = toTimeFormat() + "00"
fun toTimeFormat(): String {
    val currentTime: Long = System.currentTimeMillis()
    val dataFormat = SimpleDateFormat("HH")
    return dataFormat.format(currentTime)
}

// String(HH:mm) -> 현재 시간 가져오기
fun getNowFullTime(): String {
    val currentTime: Long = System.currentTimeMillis()
    val dataFormat = SimpleDateFormat("HH:mm")
    return dataFormat.format(currentTime)
}

// String(yyyyMMddHHmm) -> timeInMillis
fun setStringToTimeInMillis(date: String): Long {
    val dataFormat = SimpleDateFormat("yyyyMMddHHmm")
    return (dataFormat.parse(date) as Date).time
}

// String(yyyyMMdd) -> 요일 구하기
fun setStringToDayOfWeek(date: String): String {
    val input = SimpleDateFormat("yyyyMMdd")
    val output = SimpleDateFormat("E")
    return output.format(input.parse(date) as Date)
}

// String(yyyyMMdd) -> Calendar
fun setStringToCalendar(date: String): Calendar {
    val input = SimpleDateFormat("yyyyMMdd")
    input.parse(date)
    return input.calendar
}

// String(yyyyMMdd) -> String(yyyy년 MM월 dd일 E요일)
fun setStringToHangeulFullDate(date: String): String {
    val input = SimpleDateFormat("yyyyMMdd")
    val output = SimpleDateFormat("yyyy년 MM월 dd일 E요일")
    return output.format(input.parse(date) as Date)
}

// String(yyyyMMdd) -> String(yyyy년 MM월 dd일 E요일)
fun setStringToHangeulDateWithDot(date: String): String {
    val input = SimpleDateFormat("yyyyMMdd")
    val output = SimpleDateFormat("yyyy.MM.dd (E) ")
    return output.format(input.parse(date) as Date)
}

// String(yyyyMMdd) -> String(yyyy.MM.dd)
fun setStringToStringWithDot(date: String): String {
    val input = SimpleDateFormat("yyyyMMdd")
    val output = SimpleDateFormat("yyyy.MM.dd")
    return output.format(input.parse(date) as Date)
}

// Long -> String(yyyyMMdd)
fun setTimeInMillisToString(date: Long): String {
    val dataFormat = SimpleDateFormat("yyyyMMdd")
    return dataFormat.format(date)
}

// Long -> String(yyyy.MM.dd)
fun setTimeInMillisToStringWithDot(date: Long): String {
    val dataFormat = SimpleDateFormat("yyyy.MM.dd")
    return dataFormat.format(date)
}

// Long -> String(yyyy년 MM월 dd일 (E))
fun setTimeInMillisToHangeulFullDate(date: Long): String {
    val dataFormat = SimpleDateFormat("yyyy년 MM월 dd일 (E)")
    return dataFormat.format(date)
}

// 날짜 차이 값 계산
fun calculateIntervalDate(start: String, end: String): Int {
    val dataFormat = SimpleDateFormat("yyyyMMdd")
    val sec = (dataFormat.parse(start).time - dataFormat.parse(end).time) / 1000
    return (sec / (24 * 60 * 60)).toInt()
}

// 기간 검색 어제 59분
fun setLongToYesterdayLong(date: Long): Long = Calendar.getInstance()
    .apply { timeInMillis = date - (1000 * 60 * 60 * 24) }
    .apply {
        set(
            this.get(Calendar.YEAR), this.get(Calendar.MONTH), this.get(Calendar.DAY_OF_MONTH),
            23, 59, 59
        )
    }.timeInMillis

// 기간 검색 내일 0분
fun setLongToTomorrowLong(date: Long): Long = Calendar.getInstance()
    .apply { timeInMillis = date + (1000 * 60 * 60 * 24) }
    .apply {
        set(
            this.get(Calendar.YEAR), this.get(Calendar.MONTH), this.get(Calendar.DAY_OF_MONTH),
            0, 0, 0
        )
    }.timeInMillis

// Int -> 오전/오후 구하기
fun setIntToAmPm(time: Int?): String {
    time ?: return "정보 없음"

    val am = time / 100
    val pm = (time / 100) - 12

    when (time) {
        in AM -> return "오전 ${if (am == 0) 12 else am}시"
        in PM -> return "오후 ${if (pm == 0) 12 else pm}시"
    }

    return "정보 없음"
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