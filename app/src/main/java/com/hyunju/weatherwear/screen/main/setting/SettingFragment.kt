package com.hyunju.weatherwear.screen.main.setting

import androidx.fragment.app.viewModels
import com.hyunju.weatherwear.databinding.FragmentSettingBinding
import com.hyunju.weatherwear.screen.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>() {

    companion object {
        fun newInstance() = SettingFragment()

        const val TAG = "SettingFragment"
    }

    override val viewModel by viewModels<SettingViewModel>()

    override fun getViewBinding() = FragmentSettingBinding.inflate(layoutInflater)

    override fun observeData() {

    }

}