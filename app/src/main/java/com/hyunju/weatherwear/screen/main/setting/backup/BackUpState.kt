package com.hyunju.weatherwear.screen.main.setting.backup

import android.net.Uri
import androidx.annotation.StringRes

sealed class BackUpState {

    object Uninitialized : BackUpState()

    object Loading : BackUpState()

    data class Login(
        val idToken: String
    ) : BackUpState()

    sealed class Success : BackUpState() {

        data class Registered(
            val name: String,
            val email: String,
            val profileImageUri: Uri?
        ) : Success()

        object NotRegistered : Success()

        object BackUp : Success()

        object Restore : Success()
    }

    data class Error(
        @StringRes val messageId: Int
    ) : BackUpState()

}
