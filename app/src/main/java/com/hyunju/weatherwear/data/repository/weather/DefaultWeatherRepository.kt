package com.hyunju.weatherwear.data.repository.weather

import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
}