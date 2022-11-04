package com.hyunju.weatherwear.data.response.weather

import com.google.gson.annotations.SerializedName
import com.hyunju.weatherwear.model.WeatherModel
import com.hyunju.weatherwear.util.date.getCurrentTime

data class Items(
    @SerializedName("item")
    val item: List<Item?>?
) {
    private fun fcstValueToInt(value: String?) = value?.toFloat()?.toInt()
    private fun fcstValueToDouble(value: String?) = value?.toDouble()

    fun toWeatherModel(date: String): WeatherModel? {
        if (item == null) return null

        val maxTemperatures =
            item.filter { it?.fcstDate == date && it.category == CategoryType.TMX }
        val minTemperatures =
            item.filter { it?.fcstDate == date && it.category == CategoryType.TMN }

        if (maxTemperatures.isEmpty() || minTemperatures.isEmpty()) return null

        // 오늘 날짜 데이터 파싱
        item.filter { it?.fcstDate == date && it.fcstTime == getCurrentTime() }.run {
            return WeatherModel(
                date = first()?.fcstDate.orEmpty(),
                time = first()?.fcstTime.orEmpty(),
                POP = fcstValueToInt(first { it?.category == CategoryType.POP }?.fcstValue)
                    ?: return null,
                PTY = fcstValueToInt(first { it?.category == CategoryType.PTY }?.fcstValue)
                    ?: return null,
                REH = fcstValueToInt(first { it?.category == CategoryType.REH }?.fcstValue)
                    ?: return null,
                SKY = fcstValueToInt(first { it?.category == CategoryType.SKY }?.fcstValue)
                    ?: return null,
                TMN = fcstValueToInt(minTemperatures.first()?.fcstValue) ?: return null,
                TMX = fcstValueToInt(maxTemperatures.first()?.fcstValue) ?: return null,
                TMP = fcstValueToInt(first { it?.category == CategoryType.TMP }?.fcstValue)
                    ?: return null,
                WSD = fcstValueToDouble(first { it?.category == CategoryType.WSD }?.fcstValue)
                    ?: return null,
                x = first()?.nx ?: return null,
                y = first()?.ny ?: return null
            )
        }
    }
}