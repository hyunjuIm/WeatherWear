package com.hyunju.weatherwear.screen.main

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ActivityMainBinding
import com.hyunju.weatherwear.screen.base.BaseActivity
import com.hyunju.weatherwear.screen.main.home.HomeFragment
import com.hyunju.weatherwear.screen.main.setting.SettingFragment
import com.hyunju.weatherwear.screen.main.setting.SettingViewModel
import com.hyunju.weatherwear.screen.main.wear.WearFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

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
        if (it ==  SettingViewModel.YET) {
            AlertDialog.Builder(this)
                .setTitle("웨더웨어(WeatherWear)에서 알림을 보내고자 합니다.")
                .setMessage("해당 기기로 날씨 및 옷차림에 관련된 정보를 푸시 알림으로 보내드리겠습니다.\n앱 푸시에 수신 동의하시겠습니까?")
                .setPositiveButton("허용") { _, _ ->
                    viewModel.updateAgreeNotification(true)
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                .setNegativeButton("허용 안 함") { _, _ ->
                    viewModel.updateAgreeNotification(false)
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                .create()
                .show()
        }
    }

}