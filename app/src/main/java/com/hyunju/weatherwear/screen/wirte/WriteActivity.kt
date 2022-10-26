package com.hyunju.weatherwear.screen.wirte

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.hyunju.weatherwear.databinding.ActivityWriteBinding
import com.hyunju.weatherwear.screen.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WriteActivity : BaseActivity<WriteViewModel, ActivityWriteBinding>() {

    companion object {
        fun newIntent(context: Context) = Intent(context, WriteActivity::class.java)
    }

    override val viewModel by viewModels<WriteViewModel>()

    override fun getViewBinding() = ActivityWriteBinding.inflate(layoutInflater)

    override fun observeData() {

    }

}