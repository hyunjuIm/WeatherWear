package com.hyunju.weatherwear.data.repository.map

import com.hyunju.weatherwear.data.response.address.AddressInfo

interface MapRepository {

    suspend fun getReverseGeoInformation(latitude: Double, longitude: Double): AddressInfo?

}