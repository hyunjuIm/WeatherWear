package com.hyunju.weatherwear.data.response.weather

import com.google.gson.annotations.SerializedName
import com.hyunju.weatherwear.model.TimeWeatherModel
import com.hyunju.weatherwear.model.WeatherModel
import com.hyunju.weatherwear.model.WeekWeatherModel
import com.hyunju.weatherwear.util.date.*
import com.hyunju.weatherwear.util.weather.Time
import com.hyunju.weatherwear.util.weather.getWeatherType

data class Items(
    @SerializedName("item")
    val item: List<Item?>?
) {
    private fun fcstValueToInt(value: String?) = value?.toFloat()?.toInt()
    private fun fcstValueToDouble(value: String?) = value?.toDouble()

    private fun findCategoryTemperatures(date: String, category: CategoryType) =
        item!!.filter { it?.fcstDate == date && it.category == category }

    private fun findCategoryIntValue(itemList: List<Item?>, category: CategoryType) =
        fcstValueToInt(itemList.first { it?.category == category }?.fcstValue)

    private fun findCategoryDoubleValue(itemList: List<Item?>, category: CategoryType) =
        fcstValueToDouble(itemList.first { it?.category == category }?.fcstValue)

    // 해당 날짜 날씨 정보 반환
    fun getDateWeatherModel(date: String): WeatherModel? {
        item ?: return null

        val maxTemperatures = findCategoryTemperatures(date, CategoryType.TMX)
        val minTemperatures = findCategoryTemperatures(date, CategoryType.TMN)

        if (maxTemperatures.isEmpty() || minTemperatures.isEmpty()) return null

        // 해당 날짜로 데이터 파싱
        val dayList = item.filter { it?.fcstDate == date && it.fcstTime == getNowTime() }
        return toWeatherModel(
            dayList,
            fcstValueToInt(maxTemperatures.first()?.fcstValue),
            fcstValueToInt(minTemperatures.first()?.fcstValue)
        )
    }

    private fun toWeatherModel(itemList: List<Item?>, max: Int?, min: Int?): WeatherModel? {
        val data = itemList.first()
        return WeatherModel(
            date = data?.fcstDate.orEmpty(),
            time = data?.fcstTime.orEmpty(),
            POP = findCategoryIntValue(itemList, CategoryType.POP) ?: return null,
            PTY = findCategoryIntValue(itemList, CategoryType.PTY) ?: return null,
            REH = findCategoryIntValue(itemList, CategoryType.REH) ?: return null,
            SKY = findCategoryIntValue(itemList, CategoryType.SKY) ?: return null,
            TMN = min ?: return null,
            TMX = max ?: return null,
            TMP = findCategoryIntValue(itemList, CategoryType.TMP) ?: return null,
            WSD = findCategoryDoubleValue(itemList, CategoryType.WSD) ?: return null,
            x = data?.nx ?: return null,
            y = data?.ny ?: return null
        )
    }

    // 24시간 시간대별 날씨 정보 반환
    fun getTimeWeatherModelList(): List<TimeWeatherModel> {
        val todayTime = setStringToTimeInMillis(getTodayDate() + getNowTime())
        val tomorrowTime = setStringToTimeInMillis(getTomorrowDate() + getNowTime())

        val weatherTypeList = item?.filter {
            setStringToTimeInMillis(it?.fcstDate + it?.fcstTime) in todayTime..tomorrowTime
        } ?: throw Exception()

        return weatherTypeList.map { it?.fcstTime }.distinct().map { time ->
            setTimeWeatherModel(weatherTypeList.filter { it?.fcstTime == time })
        }
    }

    private fun setTimeWeatherModel(itemList: List<Item?>): TimeWeatherModel {
        val data = itemList.first()

        val time = data?.fcstTime?.toInt()
        val sky = findCategoryIntValue(itemList, CategoryType.SKY)
        val shape = findCategoryIntValue(itemList, CategoryType.PTY)
        val temperature = findCategoryIntValue(itemList, CategoryType.TMP)

        return TimeWeatherModel(
            date = data?.fcstDate ?: "",
            time = setIntToAmPm(time),
            icon = getWeatherType(time, sky, shape).image,
            temperature = temperature ?: throw Exception()
        )
    }

    // 요일별 일기 예보 정보 반환
    fun getWeekWeatherModelList(): List<WeekWeatherModel> {
        val dateList = item?.map { it?.fcstDate }?.distinct()?.slice(0..2) ?: throw Exception()

        return dateList.map { date ->
            setWeekWeatherModel(item.filter { it?.fcstDate == date }, date.orEmpty())
        }
    }

    private fun setWeekWeatherModel(itemList: List<Item?>, date: String): WeekWeatherModel {
        val maxSkyValue = itemList.filter { it?.category == CategoryType.SKY }
            .filter { it?.fcstTime?.toInt() in Time.AFTERNOON }
            .map { it?.fcstValue.orEmpty().toInt() }.max()
        val maxShapeValue = itemList.filter { it?.category == CategoryType.PTY }
            .filter { it?.fcstTime?.toInt() in Time.AFTERNOON }
            .map { it?.fcstValue.orEmpty().toInt() }.max()

        return WeekWeatherModel(
            date = date,
            dayOfWeek = setStringToDayOfWeek(date),
            icon = getWeatherType(900, maxSkyValue, maxShapeValue).image,
            maxTemperature = findCategoryIntValue(itemList, CategoryType.TMX) ?: throw Exception(),
            minTemperature = findCategoryIntValue(itemList, CategoryType.TMN) ?: throw Exception()
        )
    }

}