package com.hyunju.weatherwear.screen.write.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ActivityCameraBinding
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.util.file.newJpgFileName
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {

    companion object {
        const val URI_KEY = "uri"

        fun newIntent(context: Context) = Intent(context, CameraActivity::class.java)
    }

    private val binding by lazy { ActivityCameraBinding.inflate(layoutInflater) }

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    // 카메라 활성화 여부
    private var isActivated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        startCamera()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        bindViews()
    }

    private fun bindViews() = with(binding) {
        cameraButton.setOnClickListener { takePhoto() } // 카메라 촬영
        cancelButton.setOnClickListener { onBackPressed() } // 다시 찍기 or 뒤로 가기
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // 카메라의 수명 주기를 수명 주기 소유자에게 바인딩
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // 후면 카메라로 설정

            try {
                // 다시 바인딩하기 전에 사용 사례 바인딩 해제
                cameraProvider.unbindAll()
                // 설정된 카메라의 수명 주기 지정
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.d("CameraX-Debug", "Use case binding failed", exc)
                showErrorMessage()
            }

        }, ContextCompat.getMainExecutor(this))

        isActivated = true
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // 이미지를 저장할 타임 스탬프 출력 파일 생성
        val photoFile = File(outputDirectory, newJpgFileName())

        // 파일 + 메타데이터를 포함하는 출력 옵션 객체 생성
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // 사진 촬영 후 트리거되는 이미지 캡처 수신기 설정
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {

                override fun onError(exc: ImageCaptureException) {
                    showErrorMessage()
                    Log.d("CameraX-Debug", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    showPreviewPhoto(savedUri)
                    Log.d("CameraX-Debug", "Photo capture succeeded: $savedUri")
                }
            })
    }

    private fun showPreviewPhoto(uri: Uri) = with(binding) {
        cameraExecutor.shutdown()

        frameLayoutPreview.isVisible = true
        frameLayoutPreview.isClickable = true
        imageViewPreview.load(uri.toString())

        usePhotoButton.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(URI_KEY, uri)
            })
            finish()
        }

        isActivated = false
    }

    private fun showCamera() = with(binding) {
        frameLayoutPreview.isGone = true
        frameLayoutPreview.isClickable = false
    }

    private fun showErrorMessage() {
        Toast.makeText(this@CameraActivity, R.string.request_error, Toast.LENGTH_SHORT)
            .show()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir
        else filesDir
    }

    override fun onBackPressed() {
        if (isActivated) {
            super.onBackPressed()
        } else {
            showCamera()
            startCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}