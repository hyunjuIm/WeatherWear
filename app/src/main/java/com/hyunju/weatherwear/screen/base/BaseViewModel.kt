package com.hyunju.weatherwear.screen.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    protected var stateBundle: Bundle? = null

    open fun fetchData(): Job = viewModelScope.launch { }

    // 뷰에 대한 상태 저장, 뷰가 종료 되기 전까지는 state 유지
    open fun storeState(stateBundle: Bundle) {
        this.stateBundle = stateBundle
    }
}