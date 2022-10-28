package com.hyunju.weatherwear.data.response.weather


import com.google.gson.annotations.SerializedName
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.util.date.getCurrentTime

data class Items(
    @SerializedName("item")
    val item: List<Item?>?
) {
    private fun fcstValueToInt(value: String?) = value?.toFloat()?.toInt() ?: -99
    private fun fcstValueToDouble(value: String?) = value?.toDouble() ?: -99.0

    fun toEntity(): WeatherEntity? {
        item?.let { item ->
            val maxTemperatures = item.last { it?.category == CategoryType.TMX }?.fcstValue
            val minTemperatures = item.last { it?.category == CategoryType.TMN }?.fcstValue

            // 오늘 날짜 데이터 파싱
            item.filter { it?.fcstTime == getCurrentTime() }.run {
                return WeatherEntity(
                    date = last()?.fcstDate.orEmpty(),
                    time = last()?.fcstTime.orEmpty(),
                    POP = fcstValueToInt(first { it?.category == CategoryType.POP }?.fcstValue),
                    PTY = fcstValueToInt(first { it?.category == CategoryType.PTY }?.fcstValue),
                    REH = fcstValueToInt(first { it?.category == CategoryType.REH }?.fcstValue),
                    SKY = fcstValueToInt(first { it?.category == CategoryType.SKY }?.fcstValue),
                    TMN = fcstValueToInt(minTemperatures),
                    TMX = fcstValueToInt(maxTemperatures),
                    TMP = fcstValueToInt(first { it?.category == CategoryType.TMP }?.fcstValue),
                    WSD = fcstValueToDouble(first { it?.category == CategoryType.WSD }?.fcstValue),
                    x = last()?.nx ?: -1,
                    y = last()?.ny ?: -1
                )
            }
        }

        return null
    }
}