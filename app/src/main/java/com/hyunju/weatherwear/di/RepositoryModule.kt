package com.hyunju.weatherwear.di

import com.hyunju.weatherwear.data.repository.map.DefaultMapRepository
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.data.repository.wear.DefaultWeatherWearRepository
import com.hyunju.weatherwear.data.repository.wear.WeatherWearRepository
import com.hyunju.weatherwear.data.repository.weather.DefaultWeatherRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindWeatherRepository(weatherRepository: DefaultWeatherRepository): WeatherRepository

    @Binds
    abstract fun bindMapRepository(mapRepositoryImpl: DefaultMapRepository): MapRepository

    @Binds
    abstract fun bindWeatherWearRepository(weatherWearRepositoryImpl: DefaultWeatherWearRepository): WeatherWearRepository
}