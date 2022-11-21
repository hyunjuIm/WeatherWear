package com.hyunju.weatherwear.screen.intro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ActivityIntroBinding
import com.hyunju.weatherwear.screen.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroActivity : AppCompatActivity() {

    private val time: Long = 1800

    private val binding by lazy { ActivityIntroBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()

        actionSplashView()
    }

    private fun initViews() {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        window.statusBarColor = ContextCompat.getColor(this, R.color.sky_100)

        this.supportActionBar?.hide()
    }

    private fun actionSplashView() {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(time)

            startActivity(
                MainActivity.newIntent(this@IntroActivity)
            )
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

            finish()
        }
    }
}