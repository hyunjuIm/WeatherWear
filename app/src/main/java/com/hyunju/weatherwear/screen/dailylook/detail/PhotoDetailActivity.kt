package com.hyunju.weatherwear.screen.dailylook.detail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyunju.weatherwear.databinding.ActivityPhotoDetailBinding
import com.hyunju.weatherwear.extension.load

class PhotoDetailActivity : AppCompatActivity() {

    companion object {

        const val PHOTO_KEY = "photo"

        fun newIntent(context: Context, photo: String) =
            Intent(context, PhotoDetailActivity::class.java).apply {
                putExtra(PHOTO_KEY, photo)
            }
    }

    private val photo by lazy { intent.getStringExtra(PHOTO_KEY) }

    private val binding by lazy { ActivityPhotoDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.photoView.load(photo.toString())
    }
}