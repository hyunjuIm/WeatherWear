package com.hyunju.weatherwear.screen.main.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hyunju.weatherwear.WeatherWearApplication.Companion.appContext
import com.hyunju.weatherwear.data.preference.AppPreferenceManager
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.util.date.getTimeUsingInWorkRequest
import com.hyunju.weatherwear.work.WeatherWearWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val appPreferenceManager: AppPreferenceManager
) : BaseViewModel() {

    companion object {
        const val YET = "yet"
        const val YES = "yes"
        const val NO = "no"

        const val NOTIFICATION = "notification"

        const val PUSH_ALERT = "pushAlert"
    }

    val settingLiveData = MutableLiveData<String?>()

    override fun fetchData(): Job = viewModelScope.launch {
        val notification = appPreferenceManager.getString(NOTIFICATION)

        if (notification.isNullOrEmpty() || notification == YET) {
            appPreferenceManager.setString(NOTIFICATION, YET)
            settingLiveData.value = YET
            return@launch
        }

        settingLiveData.value = notification
    }

    fun updateAgreeNotification(isChecked: Boolean) = viewModelScope.launch {
        if (isChecked) {
            appPreferenceManager.setString(NOTIFICATION, YES)
            settingLiveData.value = YES
        } else {
            appPreferenceManager.setString(NOTIFICATION, NO)
            settingLiveData.value = NO
        }

        doWorkChaining()
    }

    private fun doWorkChaining() {
        val notification = appPreferenceManager.getString(NOTIFICATION)

        val input = mapOf(PUSH_ALERT to notification)
        val inputData = Data.Builder().putAll(input).build()

        val workManager = WorkManager.getInstance(appContext!!)

        if (notification == YES) {
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
    }

}