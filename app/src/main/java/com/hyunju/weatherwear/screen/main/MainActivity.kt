package com.hyunju.weatherwear.screen.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.work.*
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ActivityMainBinding
import com.hyunju.weatherwear.screen.main.home.HomeFragment
import com.hyunju.weatherwear.screen.main.setting.SettingFragment
import com.hyunju.weatherwear.screen.main.wear.WearFragment
import com.hyunju.weatherwear.util.date.getTimeUsingInWorkRequest
import com.hyunju.weatherwear.work.WeatherWearWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        initWorker()
    }

    private fun initViews() = with(binding) {
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

    private fun initWorker() {
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<WeatherWearWorker>()
            .setInitialDelay(getTimeUsingInWorkRequest(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniqueWork("WeatherWearCheck", ExistingWorkPolicy.REPLACE, oneTimeWorkRequest)
    }

}