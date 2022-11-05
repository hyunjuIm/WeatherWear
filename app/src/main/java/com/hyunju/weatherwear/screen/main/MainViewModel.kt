package com.hyunju.weatherwear.screen.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.data.preference.AppPreferenceManager
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.work.WeatherWearWorker
import com.hyunju.weatherwear.work.doWorkChaining
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appPreferenceManager: AppPreferenceManager
) : BaseViewModel() {

    val mainLiveData = MutableLiveData<String?>()

    override fun fetchData(): Job = viewModelScope.launch {
        val notification = appPreferenceManager.getString(WeatherWearWorker.NOTIFICATION)

        doWorkChaining(notification)

        mainLiveData.value = notification
    }

    fun updateAgreeNotification(isChecked: Boolean) = viewModelScope.launch {
        appPreferenceManager.setString(
            WeatherWearWorker.NOTIFICATION,
            if (isChecked) WeatherWearWorker.ON else WeatherWearWorker.OFF
        )
        fetchData()
    }

}