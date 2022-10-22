package com.hyunju.weatherwear.di

import com.hyunju.weatherwear.data.repository.map.DefaultMapRepository
import com.hyunju.weatherwear.data.repository.map.MapRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindMapRepository(mapRepositoryImpl: DefaultMapRepository): MapRepository

}