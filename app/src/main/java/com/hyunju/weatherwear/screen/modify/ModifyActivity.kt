package com.hyunju.weatherwear.screen.modify

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ActivityModifyBinding
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.screen.base.BaseActivity
import com.hyunju.weatherwear.screen.dialog.ConfirmDialog
import com.hyunju.weatherwear.screen.dialog.ConfirmDialogInterface
import com.hyunju.weatherwear.screen.dialog.PhotoOption
import com.hyunju.weatherwear.screen.dialog.SelectPhotoOptionBottomSheetDialog
import com.hyunju.weatherwear.screen.write.camera.CameraActivity
import com.hyunju.weatherwear.screen.write.gallery.GalleryActivity
import com.hyunju.weatherwear.util.date.setTimeInMillisToStringWithDot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ModifyActivity : BaseActivity<ModifyViewModel, ActivityModifyBinding>(),
    ConfirmDialogInterface {

    companion object {

        const val ID_KEY = "id"

        fun newIntent(context: Context, id: Long) =
            Intent(context, ModifyActivity::class.java).apply {
                putExtra(ID_KEY, id)
            }

        val photoPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override val viewModel by viewModels<ModifyViewModel>()

    override fun getViewBinding() = ActivityModifyBinding.inflate(layoutInflater)

    override val layoutId = R.layout.activity_modify
    override val transitionMode = TransitionMode.VERTICAL

    private val photoPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { permission -> permission.value }) {
                showPictureUploadDialog()
            } else {
                Toast.makeText(this, R.string.can_not_assigned_permission, Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getParcelableExtra<Uri>(GalleryActivity.URI_KEY)
                    ?.let { setPhotoFromUriData(it) } ?: kotlin.run {
                    Toast.makeText(this, R.string.fail_photo_to_get, Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getParcelableExtra<Uri>(CameraActivity.URI_KEY)
                    ?.let { setPhotoFromUriData(it) } ?: kotlin.run {
                    Toast.makeText(this, R.string.fail_photo_to_get, Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val selectPhotoOptionBottomSheetDialog by lazy {
        SelectPhotoOptionBottomSheetDialog { option ->
            when (option) {
                PhotoOption.CAMARA -> cameraLauncher.launch(CameraActivity.newIntent(this))
                PhotoOption.GALLERY -> galleryLauncher.launch(GalleryActivity.newIntent(this))
            }
        }
    }

    private val weatherWearId by lazy { intent.getLongExtra(ID_KEY, -1) }

    private var originalPhoto: Bitmap? = null
    private var selectPhoto: Uri? = null

    override fun onResume() {
        super.onResume()
        isEnabledWriteButton()
    }

    @SuppressLint("SetTextI18n")
    override fun initViews() = with(binding) {
        viewModel.getWeatherWearData(weatherWearId)

        toolbar.setNavigationOnClickListener { finish() }

        // ?????? ??????
        selectPhotoView.setOnClickListener {
            checkHasPermission {
                showPictureUploadDialog()
            }
        }
        // ?????? ?????? ??????
        cancelPhotoButton.setOnClickListener { cancelSelectedPhoto() }
    }

    override fun observeData() = viewModel.modifyStateLiveData.observe(this) {
        when (it) {
            is ModifyState.Uninitialized -> handleUninitializedState()
            is ModifyState.Loading -> handleLoadingState()
            is ModifyState.Success -> handleSuccessState(it)
            is ModifyState.Modify -> handleModifyState()
            is ModifyState.Error -> handleErrorState(it)
            else -> Unit
        }
    }

    private fun handleUninitializedState() = with(binding) {
        modifyButton.isEnabled = false
    }

    private fun handleLoadingState() = with(binding) {
        loadingView.root.isVisible = true
        modifyButton.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessState(state: ModifyState.Success) = with(binding) {
        loadingView.root.isGone = true

        state.weatherWearInfo.photo.apply {
            originalPhoto = this
            weatherWearImageView.load(this)
        }
        selectPhotoView.isGone = true
        cancelPhotoButton.isVisible = true

        val maxTemperature = state.weatherWearInfo.maxTemperature
        val minTemperature = state.weatherWearInfo.minTemperature
        val weatherType = state.weatherWearInfo.weatherType

        weatherTextView.text =
            if (maxTemperature != null && minTemperature != null && !weatherType.isNullOrEmpty()) {
                "?????? ?????? ${maxTemperature}??/ ?????? ?????? ${minTemperature}??/ $weatherType"
            } else {
                getString(R.string.weather_and_temperature_info_not_found)
            }

        selectDateTextView.text = setTimeInMillisToStringWithDot(state.weatherWearInfo.date.time)
        selectLocationTextView.text = state.weatherWearInfo.location

        diaryEditText.setText(state.weatherWearInfo.diary)

        // ????????????
        modifyButton.setOnClickListener {
            viewModel.modifyWeatherWear(
                uri = selectPhoto,
                diary = binding.diaryEditText.text.toString()
            )
        }

        isEnabledWriteButton()
    }

    private fun handleModifyState() = with(binding) {
        loadingView.root.isGone = true

        Toast.makeText(
            this@ModifyActivity,
            getString(R.string.completed_modify),
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    private fun handleErrorState(state: ModifyState.Error) = with(binding) {
        loadingView.root.isGone = true

        Toast.makeText(this@ModifyActivity, getString(state.messageId), Toast.LENGTH_SHORT).show()
    }

    // ????????? ?????? ??????
    private fun setPhotoFromUriData(uri: Uri) = with(binding) {
        selectPhoto = uri

        weatherWearImageView.load(uri.toString())
        selectPhotoView.isGone = true
        cancelPhotoButton.isVisible = true
    }

    // ????????? ?????? ??????
    private fun cancelSelectedPhoto() = with(binding) {
        selectPhoto = null

        weatherWearImageView.setImageBitmap(null)
        weatherWearImageView.setBackgroundColor(getColor(R.color.snow))

        selectPhotoView.isVisible = true
        cancelPhotoButton.isGone = true
        modifyButton.isEnabled = false
    }

    private fun showPictureUploadDialog() {
        selectPhotoOptionBottomSheetDialog.show(
            supportFragmentManager,
            "selectPhotoOptionBottomSheetDialog"
        )
    }

    private fun checkHasPermission(uploadAction: () -> Unit) {
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val refusedCameraPermission =
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)

        val refusedExternalStoragePermission =
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
        val hasExternalStoragePermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        when {
            // ????????? ?????? ??????
            hasCameraPermission && hasExternalStoragePermission -> {
                uploadAction()
            }
            !hasCameraPermission && !hasExternalStoragePermission -> {
                photoPermissionLauncher.launch(photoPermissions)
            }
            // ????????? ????????? ??????, ?????? ????????? ?????? ??? ?????? ?????? / ?????? ???????????? ????????? ??????
            (!hasCameraPermission || !hasExternalStoragePermission) ||
                    (refusedCameraPermission || refusedExternalStoragePermission) -> {
                showPermissionContextPopup()
            }
            else -> {
                photoPermissionLauncher.launch(photoPermissions)
            }
        }
    }

    private fun showPermissionContextPopup() {
        ConfirmDialog(
            confirmDialogInterface = this,
            text = getString(R.string.setting_photo_permission)
        ).show(this.supportFragmentManager, "ConfirmDialog")
    }

    // ?????? ????????? ??????
    override fun onYesButtonClick() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            data = uri
        }
        startActivity(intent)
    }

    // ???????????? ?????? ????????? ??????
    private fun isEnabledWriteButton() {
        binding.enable = (originalPhoto != null || selectPhoto != null)
    }
}