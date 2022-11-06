package com.hyunju.weatherwear.screen.main.weather

import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val weatherRepository: WeatherRepository
) : BaseViewModel() {
}