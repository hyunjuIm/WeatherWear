package com.hyunju.weatherwear.data.repository.map

import com.hyunju.weatherwear.data.entity.LocationEntity
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.data.response.address.AddressInfo
import com.hyunju.weatherwear.data.response.search.Poi

interface MapRepository {

    suspend fun getReverseGeoInformation(latitude: Double, longitude: Double): AddressInfo?

    suspend fun getSearchLocationInformation(keyword: String): List<Poi>?

    suspend fun getLocationDataFromDevice(): List<LocationEntity>

    suspend fun saveLocationDataToDevice(locationEntity: LocationEntity)

}