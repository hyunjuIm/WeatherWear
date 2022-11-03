package com.hyunju.weatherwear.work

import androidx.work.DelegatingWorkerFactory
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import javax.inject.Inject

class AppWorkerFactory @Inject constructor(
    val mapRepository: MapRepository,
    val weatherRepository: WeatherRepository
) : DelegatingWorkerFactory() {

    init {
        addFactory(WeatherWearWorkerFactory(mapRepository, weatherRepository))
    }

}