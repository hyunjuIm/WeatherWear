package com.hyunju.weatherwear.screen.main.home

import androidx.annotation.StringRes

sealed class HomeState {

    object Uninitialized : HomeState()

    object Loading : HomeState()

    data class Success(
        val location: String
    ) : HomeState()

    data class Error(
        @StringRes val messageId: Int
    ) : HomeState()
}