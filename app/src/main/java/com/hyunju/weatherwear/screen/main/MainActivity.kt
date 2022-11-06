package com.hyunju.weatherwear.screen.main

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ActivityMainBinding
import com.hyunju.weatherwear.screen.base.BaseActivity
import com.hyunju.weatherwear.screen.dialog.YesOrNoDialog
import com.hyunju.weatherwear.screen.dialog.YesOrNoDialogInterface
import com.hyunju.weatherwear.screen.main.home.HomeFragment
import com.hyunju.weatherwear.screen.main.setting.SettingFragment
import com.hyunju.weatherwear.screen.main.wear.WearFragment
import com.hyunju.weatherwear.screen.main.weather.WeatherFragment
import com.hyunju.weatherwear.work.WeatherWearWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), YesOrNoDialogInterface {

    override val viewModel by viewModels<MainViewModel>()

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun initViews() = with(binding) {
        showFragment(HomeFragment.newInstance(), HomeFragment.TAG)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
                    true
                }
                R.id.menu_weather -> {
                    showFragment(WeatherFragment.newInstance(), WeatherFragment.TAG)
                    true
                }
                R.id.menu_wear -> {
                    showFragment(WearFragment.newInstance(), WearFragment.TAG)
                    true
                }
                R.id.menu_setting -> {
                    showFragment(SettingFragment.newInstance(), SettingFragment.TAG)
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        val findFragment = supportFragmentManager.findFragmentByTag(tag)
        supportFragmentManager.fragments.forEach { fm ->
            supportFragmentManager.beginTransaction().hide(fm).commitAllowingStateLoss()
        }
        findFragment?.let {
            // 프래그먼트 상태 정보가 있는 경우, 보여주기만
            supportFragmentManager.beginTransaction().show(it).commitAllowingStateLoss()
        } ?: kotlin.run {
            // 프래그먼트 상태 정보가 없는 경우, 추가
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment, tag)
                .commitAllowingStateLoss()
        }
    }

    override fun observeData() = viewModel.mainLiveData.observe(this) {
        if (it == WeatherWearWorker.YET || it.isNullOrEmpty()) {
            YesOrNoDialog(
                yesOrNoDialogInterface = this,
                title = getString(R.string.push_title),
                message = getString(R.string.push_message),
                positiveButton = getString(R.string.push_allow),
                negativeButton = getString(R.string.push_not_allow)
            ).show(this.supportFragmentManager, "YesOrNoDialog")
        }
    }

    override fun onYesButtonClick(value: Boolean, tag: String) {
        viewModel.updateAgreeNotification(value)
    }

}