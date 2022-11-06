package com.hyunju.weatherwear.screen.main.weather

import androidx.fragment.app.viewModels
import com.hyunju.weatherwear.databinding.FragmentWeatherBinding
import com.hyunju.weatherwear.screen.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : BaseFragment<WeatherViewModel, FragmentWeatherBinding>() {

    companion object {
        fun newInstance() = WeatherFragment()

        const val TAG = "WeatherFragment"
    }

    override val viewModel by viewModels<WeatherViewModel>()

    override fun getViewBinding() = FragmentWeatherBinding.inflate(layoutInflater)

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) changeStatusBarForTime(binding.backgroundLayout)
    }

    override fun initViews() {
        // 시간에 따른 상태바 색상, 배경 그라데이션 변경
        changeStatusBarForTime(binding.backgroundLayout)
    }

    override fun observeData() {

    }

}