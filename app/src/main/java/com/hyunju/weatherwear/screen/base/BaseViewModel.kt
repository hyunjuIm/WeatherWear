package com.hyunju.weatherwear.screen.base

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.R
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketException
import java.net.UnknownHostException

abstract class BaseViewModel : ViewModel() {

    protected var stateBundle: Bundle? = null

    open fun fetchData(): Job = viewModelScope.launch { }

    open fun errorData(@StringRes message: Int): Job = viewModelScope.launch { }

    // 뷰에 대한 상태 저장, 뷰가 종료 되기 전까지는 state 유지
    open fun storeState(stateBundle: Bundle) {
        this.stateBundle = stateBundle
    }

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()

        when (throwable) {
            is SocketException -> errorData(R.string.fail_get_information)
            is HttpException -> errorData(R.string.fail_get_information)
            is UnknownHostException -> errorData(R.string.fail_get_information)
            else -> errorData(R.string.request_error)
        }
    }
}