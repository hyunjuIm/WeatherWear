package com.hyunju.weatherwear.screen.main.home

import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import com.hyunju.weatherwear.screen.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : BaseViewModel() {
    
}