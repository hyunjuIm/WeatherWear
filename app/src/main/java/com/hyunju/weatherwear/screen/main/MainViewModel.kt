package com.hyunju.weatherwear.screen.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hyunju.weatherwear.WeatherWearApplication
import com.hyunju.weatherwear.data.preference.AppPreferenceManager
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.screen.main.setting.SettingViewModel
import com.hyunju.weatherwear.util.date.getTimeUsingInWorkRequest
import com.hyunju.weatherwear.work.WeatherWearWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appPreferenceManager: AppPreferenceManager
) : BaseViewModel() {

    val mainLiveData = MutableLiveData<String?>()

    override fun fetchData(): Job = viewModelScope.launch {
        val notification = appPreferenceManager.getString(SettingViewModel.NOTIFICATION)

        val input = mapOf(SettingViewModel.PUSH_ALERT to notification)
        val inputData = Data.Builder().putAll(input).build()

        val workManager = WorkManager.getInstance(WeatherWearApplication.appContext!!)

        if (notification == SettingViewModel.YES) {
            val oneTimeWorkRequest = OneTimeWorkRequestBuilder<WeatherWearWorker>()
                .setInputData(inputData)
                .setInitialDelay(getTimeUsingInWorkRequest(), TimeUnit.MILLISECONDS)
                .build()

            workManager.enqueueUniqueWork(
                "weather_wear_worker",
                ExistingWorkPolicy.REPLACE,
                oneTimeWorkRequest
            )
        } else {
            workManager.cancelUniqueWork("weather_wear_worker")
        }

        mainLiveData.value = notification
    }

    fun updateAgreeNotification(isChecked: Boolean) = viewModelScope.launch {
        appPreferenceManager.setString(
            SettingViewModel.NOTIFICATION,
            if (isChecked) SettingViewModel.YES else SettingViewModel.NO
        )
        fetchData()
    }

}