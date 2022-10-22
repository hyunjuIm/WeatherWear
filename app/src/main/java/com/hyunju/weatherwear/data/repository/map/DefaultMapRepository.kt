package com.hyunju.weatherwear.data.repository.map

import com.hyunju.weatherwear.data.network.MapApiService
import com.hyunju.weatherwear.data.response.address.AddressInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultMapRepository @Inject constructor(
    private val mapApiService: MapApiService
) : MapRepository {

    override suspend fun getReverseGeoInformation(
        latitude: Double,
        longitude: Double
    ): AddressInfo? = withContext(Dispatchers.IO) {
        val response = mapApiService.getReverseGeoCode(
            lat = latitude,
            lon = longitude
        )
        if (response.isSuccessful) {
            response.body()?.addressInfo
        } else {
            null
        }
    }
}