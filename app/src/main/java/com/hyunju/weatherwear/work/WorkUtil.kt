package com.hyunju.weatherwear.work

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hyunju.weatherwear.WeatherWearApplication.Companion.appContext
import com.hyunju.weatherwear.util.date.getTimeUsingInWorkRequest
import java.util.concurrent.TimeUnit

fun doWorkChaining(notification: String?) {
    val input = mapOf(WeatherWearWorker.PUSH_ALERT to notification)
    val inputData = Data.Builder().putAll(input).build()

    val workManager = WorkManager.getInstance(appContext!!)

    if (notification == WeatherWearWorker.ON) {
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<WeatherWearWorker>()
            .setInputData(inputData)
            .setInitialDelay(getTimeUsingInWorkRequest(), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            WeatherWearWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
    } else {
        workManager.cancelUniqueWork(WeatherWearWorker.WORK_NAME)
    }
}