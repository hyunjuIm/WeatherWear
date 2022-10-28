package com.hyunju.weatherwear.data.response.weather

import com.google.gson.annotations.SerializedName

enum class CategoryType(
    val title: String,
    val unit: String
) {

    @SerializedName("POP")
    POP("강수확률", "강수확률"),

    @SerializedName("PTY")
    PTY("강수형태", "코드값"),

    @SerializedName("PCP")
    PCP("1시간 강수량", "범주 (1mm)"),

    @SerializedName("REH")
    REH("습도", "%"),

    @SerializedName("SNO")
    SNO("1시간 신적설", "범주 (1cm)"),

    @SerializedName("SKY")
    SKY("하늘상태", "코드값"),

    @SerializedName("TMP")
    TMP("1시간 기온", "°C"),

    @SerializedName("TMN")
    TMN("일 최저기온", "°C"),

    @SerializedName("TMX")
    TMX("일 최고기온", "°C"),

    @SerializedName("UUU")
    UUU("풍속(동서성분)", "m/s"),

    @SerializedName("VVV")
    VVV("풍속(남북성분)", "m/s"),

    @SerializedName("WAV")
    WAV("파고", "M"),

    @SerializedName("VEC")
    VEC("풍향", "deg"),

    @SerializedName("WSD")
    WSD("풍속", "m/s")

}