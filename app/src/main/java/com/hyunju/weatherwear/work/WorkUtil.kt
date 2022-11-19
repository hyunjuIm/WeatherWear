package com.hyunju.weatherwear.work

import androidx.work.*
import com.hyunju.weatherwear.WeatherWearApplication.Companion.appContext
import com.hyunju.weatherwear.util.date.getTimeUsingInWorkRequest
import java.util.concurrent.TimeUnit

fun doWorkChaining(notification: String?) {

    val input = mapOf(WeatherWearWorker.PUSH_ALERT to notification)
    val inputData = Data.Builder().putAll(input).build()

    val workManager = WorkManager.getInstance(appContext!!)

    if (notification == WeatherWearWorker.ON) {
        val periodicWorkRequest = PeriodicWorkRequestBuilder<WeatherWearWorker>(6, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .setConstraints(createConstraints())
            .setInputData(inputData)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WeatherWearWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )
    } else {
        workManager.cancelUniqueWork(WeatherWearWorker.WORK_NAME)
    }
}

private fun createConstraints() = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
    .setRequiresCharging(false)
    .setRequiresBatteryNotLow(true)
    .build()