package com.hyunju.weatherwear.util.weather

import com.hyunju.weatherwear.model.WeatherModel
import kotlin.math.pow

// 체감온도
fun getSensibleTemperature(temperatures: Int, windSpeed: Double): Int {
    return (13.12 + 0.6215 * temperatures.toFloat()
            - 11.37 * windSpeed.pow(0.16)
            + 0.3965 * temperatures * windSpeed.pow(0.16)).toInt()
}

// 날씨 이미지, 정보 ui 셋팅
fun getWeatherType(weatherInfo: WeatherModel): Weather {
    val afternoon = (weatherInfo.time.toInt()) in Time.AFTERNOON
    val sky = weatherInfo.SKY
    val shape = weatherInfo.PTY

    return when {
        (sky in Sky.SUN && shape == Shape.NONE) -> if (afternoon) Weather.SUN else Weather.NIGHT
        (sky in Sky.SUN && shape == Shape.RAIN) -> if (afternoon) Weather.SUN_RAINY else Weather.NIGHT_RAINY
        (sky in Sky.SUN && shape == Shape.RAIN_SNOW) -> if (afternoon) Weather.SUN_RAINY_SNOWY else Weather.NIGHT_RAINY_SNOWY
        (sky in Sky.SUN && shape == Shape.SNOW) -> if (afternoon) Weather.SUN_SNOWY else Weather.NIGHT_SNOWY
        (sky in Sky.SUN && shape == Shape.SHOWER) -> if (afternoon) Weather.SUN_SHOWER else Weather.NIGHT_SHOWER

        (sky in Sky.CLOUDY && shape == Shape.NONE) -> Weather.CLOUDY
        (sky in Sky.CLOUDY && shape == Shape.RAIN) -> Weather.CLOUDY_RAINY
        (sky in Sky.CLOUDY && shape == Shape.RAIN_SNOW) -> Weather.CLOUDY_RAINY_SNOWY
        (sky in Sky.CLOUDY && shape == Shape.SNOW) -> Weather.CLOUDY_SNOWY
        (sky in Sky.CLOUDY && shape == Shape.SHOWER) -> Weather.CLOUDY_SHOWER

        (sky in Sky.GRAY_CLOUDY && shape == Shape.NONE) -> Weather.GRAY_CLOUDY
        (sky in Sky.GRAY_CLOUDY && shape == Shape.RAIN) -> Weather.GRAY_CLOUDY_RAINY
        (sky in Sky.GRAY_CLOUDY && shape == Shape.RAIN_SNOW) -> Weather.GRAY_CLOUDY_RAINY_SNOWY
        (sky in Sky.GRAY_CLOUDY && shape == Shape.SNOW) -> Weather.GRAY_CLOUDY_SNOWY
        (sky in Sky.GRAY_CLOUDY && shape == Shape.SHOWER) -> Weather.GRAY_CLOUDY_SHOWER

        else -> Weather.UNKNOWN
    }
}

// 날씨 코멘트
fun getCommentWeather(weatherInfo: WeatherModel): List<String> {
    val commentList = arrayListOf<String>()

    if (weatherInfo.TMX - weatherInfo.TMN >= 10) {
        commentList.add("일교차가 심해요! 아우터를 챙겨야 할 것 같아요.")
    }

    when (weatherInfo.TMX) {
        in Temperatures.TEMPERATURE_HIGH.range -> {
            commentList.add("한여름 날씨네요. 시원한 옷차림을 추천합니다!")
            commentList.add("정말 덥네요. 최대한 얇고 가벼운 옷차림이 좋겠죠?")
            commentList.add("냉방병 조심! 실내라면 반팔에 얇은 가디건을 챙겨가세요 :)")
        }
        in Temperatures.TEMPERATURE_23.range -> {
            commentList.add("한여름은 아니지만 그래도 더운 날씨예요.")
            commentList.add("하의는 반바지 혹은 면바지를 추천해요 :)")
            commentList.add("냉방병 조심! 실내라면 반팔에 얇은 가디건을 챙겨가세요 :)")
        }
        in Temperatures.TEMPERATURE_20.range -> {
            commentList.add("긴팔이나 얇은 가디건을 추천합니다 :)")
            commentList.add("셔츠나 맨투맨, 바람막이 어떠세요?")
            commentList.add("하의는 청바지나 롱치마를 추천드려요.")
        }
        in Temperatures.TEMPERATURE_17.range -> {
            commentList.add("여러 스타일로 꾸밀 수 있는 날씨네요!")
            commentList.add("가디건, 셔츠, 맨투맨, 후드티 추천해드려요 :)")
        }
        in Temperatures.TEMPERATURE_12.range -> {
            commentList.add("여러 스타일로 꾸밀 수 있는 날씨네요!")
            commentList.add("얇은 옷을 여러 벌 걸쳐서 레이어드 어떠세요?")
            commentList.add("은근히 춥죠, 스타킹을 신기도 하는 날씨에요 :)")
        }
        in Temperatures.TEMPERATURE_9.range -> {
            commentList.add("지금이에요! 멋스러운 트렌치코트 어떠세요?")
            commentList.add("트렌치코트, 점퍼, 야상을 추천드려요 :)")
            commentList.add("감기를 조심해야할 시기에요.")
        }
        in Temperatures.TEMPERATURE_5.range -> {
            commentList.add("추위를 많이 타시는 분이라면 히트택 어떠세요?")
            commentList.add("체온 유지가 중요한 날씨예요.")
            commentList.add("니트와 경량 패딩을 함께 입기 좋은 날씨예요.")
        }
        in Temperatures.TEMPERATURE_LOW.range -> {
            commentList.add("체온 유지를 위해 울코트나 누빔 옷을 입어보세요.")
            commentList.add("오늘 같은 날씨, 최고의 아우터는 패딩인 거 아시죠?!")
            commentList.add("두꺼운 옷 한 벌 보다 얇은 옷 여러 겹이 보온 효과에 좋아요!")
        }
    }

    return commentList.shuffled()
}