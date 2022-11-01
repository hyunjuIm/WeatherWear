package com.hyunju.weatherwear.data.response.weather


import android.util.Log
import com.google.gson.annotations.SerializedName
import com.hyunju.weatherwear.model.WeatherModel
import com.hyunju.weatherwear.util.date.getCurrentTime

data class Items(
    @SerializedName("item")
    val item: List<Item?>?
) {
    private fun fcstValueToInt(value: String?) = value?.toFloat()?.toInt() ?: -99
    private fun fcstValueToDouble(value: String?) = value?.toDouble() ?: -99.0

    fun toEntity(date: String): WeatherModel? {
        Log.d("ㅎㅎ", "toEntity: $item")

        item?.let { item ->
            val maxTemperatures = item.first { it?.category == CategoryType.TMX }?.fcstValue
            val minTemperatures = item.first { it?.category == CategoryType.TMN }?.fcstValue

            // 오늘 날짜 데이터 파싱
            item.filter { it?.fcstDate == date && it.fcstTime == getCurrentTime() }.run {
                return WeatherModel(
                    date = first()?.fcstDate.orEmpty(),
                    time = first()?.fcstTime.orEmpty(),
                    POP = fcstValueToInt(first { it?.category == CategoryType.POP }?.fcstValue),
                    PTY = fcstValueToInt(first { it?.category == CategoryType.PTY }?.fcstValue),
                    REH = fcstValueToInt(first { it?.category == CategoryType.REH }?.fcstValue),
                    SKY = fcstValueToInt(first { it?.category == CategoryType.SKY }?.fcstValue),
                    TMN = fcstValueToInt(minTemperatures),
                    TMX = fcstValueToInt(maxTemperatures),
                    TMP = fcstValueToInt(first { it?.category == CategoryType.TMP }?.fcstValue),
                    WSD = fcstValueToDouble(first { it?.category == CategoryType.WSD }?.fcstValue),
                    x = first()?.nx ?: -1,
                    y = first()?.ny ?: -1
                )
            }
        }

        return null
    }
}