package com.hyunju.weatherwear.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import javax.inject.Inject

class WeatherWearWorkerFactory @Inject constructor(
    private val mapRepository: MapRepository,
    private val weatherRepository: WeatherRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = when (workerClassName) {
        WeatherWearWorker::class.java.name -> {
            WeatherWearWorker(
                appContext,
                workerParameters,
                mapRepository,
                weatherRepository
            )
        }
        else -> {
            null
        }
    }
}