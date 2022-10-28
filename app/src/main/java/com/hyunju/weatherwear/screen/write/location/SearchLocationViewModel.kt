package com.hyunju.weatherwear.screen.write.location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.data.entity.LocationLatLngEntity
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : BaseViewModel() {

    val searchLocationListLiveData = MutableLiveData<List<SearchResultEntity>>()

    fun searchLocation(keyword: String) = viewModelScope.launch {
        searchLocationListLiveData.value =
            mapRepository.getSearchLocationInformation(keyword)?.map {
                SearchResultEntity(
                    name = "${it.upperAddrName} ${it.middleAddrName} ${it.lowerAddrName}",
                    locationLatLng = LocationLatLngEntity(
                        it.noorLat.toDouble(),
                        it.noorLon.toDouble()
                    )
                )
            }?.distinctBy { it.name }
    }

}