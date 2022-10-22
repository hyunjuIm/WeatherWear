package com.hyunju.weatherwear.screen.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : BaseViewModel() {

    val homeStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)

    fun loadReverseGeoInformation(latitude: Double, longitude: Double) =
        viewModelScope.launch {
            homeStateLiveData.value = HomeState.Loading
            val addressInfo = mapRepository.getReverseGeoInformation(latitude, longitude)
            addressInfo?.let { info ->
                homeStateLiveData.value = HomeState.Success(
                    location = info.toSearchInfoEntity(latitude, longitude)
                )
            } ?: run {
                homeStateLiveData.value = HomeState.Error(R.string.can_not_load_address_info)
            }
        }
}