package com.hyunju.weatherwear.screen.write.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hyunju.weatherwear.databinding.ActivityPreviewBinding
import com.hyunju.weatherwear.extension.load

class PreviewActivity : AppCompatActivity() {

    companion object {
        const val URI_KEY = "uri"

        fun newIntent(context: Context, uri: Uri) =
            Intent(context, PreviewActivity::class.java).apply {
                putExtra(URI_KEY, uri)
            }.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    }

    private val uri by lazy { intent.getParcelableExtra<Uri>(URI_KEY) }

    private val binding by lazy { ActivityPreviewBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun initViews() = with(binding) {
        imageViewPreview.load(uri.toString())

        usePhotoButton.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(URI_KEY, uri)
            })
            finish()
        }

        cancelButton.setOnClickListener {
            onBackPressed()
        }
    }
}