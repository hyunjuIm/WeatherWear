package com.hyunju.weatherwear.screen.dailylook.detail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyunju.weatherwear.databinding.ActivityPhotoDetailBinding
import com.hyunju.weatherwear.extension.load

class PhotoDetailActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, PhotoDetailActivity::class.java)
    }

    private val binding by lazy { ActivityPhotoDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        PhotoDetailObject.bitmap?.let {
            binding.photoView.load(it)
        }
    }

    object PhotoDetailObject {
        var bitmap: Bitmap? = null
    }

}