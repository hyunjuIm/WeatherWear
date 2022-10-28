package com.hyunju.weatherwear.screen.write.camera

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyunju.weatherwear.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {

    companion object {
        const val URI_KEY = "uri"

        fun newIntent(context: Context) = Intent(context, CameraActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }
}