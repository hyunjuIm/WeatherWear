package com.hyunju.weatherwear.screen.main.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.data.preference.AppPreferenceManager
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val appPreferenceManager: AppPreferenceManager
) : BaseViewModel() {

    val settingLiveData = MutableLiveData<String?>()

    override fun fetchData(): Job = viewModelScope.launch {
        val notification = appPreferenceManager.getString(WeatherWearWorker.NOTIFICATION)

        if (notification.isNullOrEmpty() || notification == WeatherWearWorker.YET) {
            appPreferenceManager.setString(WeatherWearWorker.NOTIFICATION, WeatherWearWorker.YET)
            settingLiveData.value = WeatherWearWorker.YET
            return@launch
        }

        settingLiveData.value = notification
    }

    fun updateAgreeNotification(isChecked: Boolean) = viewModelScope.launch {
        val value = if (isChecked) WeatherWearWorker.ON else WeatherWearWorker.OFF

        appPreferenceManager.setString(WeatherWearWorker.NOTIFICATION, value)
        settingLiveData.value = value

        doWorkChaining(appPreferenceManager.getString(WeatherWearWorker.NOTIFICATION))
    }

}