package com.hyunju.weatherwear.data.response.address

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddressInfo(
    @SerializedName("fullAddress")
    @Expose
    val fullAddress: String?,
    @SerializedName("addressType")
    @Expose
    val addressType: String?,
    @SerializedName("city_do")
    @Expose
    val cityDo: String?,
    @SerializedName("gu_gun")
    @Expose
    val guGun: String?,
    @SerializedName("eup_myun")
    @Expose
    val eupMyun: String?,
    @SerializedName("adminDong")
    @Expose
    val adminDong: String?,
    @SerializedName("adminDongCode")
    @Expose
    val adminDongCode: String?,
    @SerializedName("legalDong")
    @Expose
    val legalDong: String?,
    @SerializedName("legalDongCode")
    @Expose
    val legalDongCode: String?,
    @SerializedName("ri")
    @Expose
    val ri: String?,
    @SerializedName("bunji")
    @Expose
    val bunji: String?,
    @SerializedName("roadName")
    @Expose
    val roadName: String?,
    @SerializedName("buildingIndex")
    @Expose
    val buildingIndex: String?,
    @SerializedName("buildingName")
    @Expose
    val buildingName: String?,
    @SerializedName("mappingDistance")
    @Expose
    val mappingDistance: String?,
    @SerializedName("roadCode")
    @Expose
    val roadCode: String?
) {
    fun toSearchInfoEntity(latitude: Double, longitude: Double): String {
        if (cityDo.isNullOrEmpty()) return "위치 정보 없음"

        val guGunText = if (!guGun.isNullOrEmpty()) " $guGun" else ""
        val eupMyunText = if (!eupMyun.isNullOrEmpty()) " $eupMyun" else ""
        val dongText =
            if (!legalDong.isNullOrEmpty()) " $legalDong"
            else if (!adminDong.isNullOrEmpty()) " $adminDong"
            else ""

        return "${cityDo}${guGunText}${eupMyunText}${dongText}"
    }
}