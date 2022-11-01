package com.hyunju.weatherwear.data.repository.map

import com.hyunju.weatherwear.data.db.dao.LocationDao
import com.hyunju.weatherwear.data.entity.LocationEntity
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.data.network.MapApiService
import com.hyunju.weatherwear.data.response.address.AddressInfo
import com.hyunju.weatherwear.data.response.search.Poi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultMapRepository @Inject constructor(
    private val mapApiService: MapApiService,
    private val locationDao: LocationDao
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

    override suspend fun getSearchLocationInformation(keyword: String): List<Poi>? =
        withContext(Dispatchers.IO) {
            val response = mapApiService.getSearchLocation(keyword = keyword)
            if (response.isSuccessful) {
                response.body()?.searchPoiInfo?.pois?.poi
            } else {
                null
            }
        }

    override suspend fun getLocationDataFromDevice(): List<LocationEntity> =
        withContext(Dispatchers.IO) {
            locationDao.getAll()
        }

    override suspend fun saveLocationDataToDevice(locationEntity: LocationEntity) =
        withContext(Dispatchers.IO) {
            locationDao.deleteAll()
            locationDao.insert(locationEntity)
        }
}