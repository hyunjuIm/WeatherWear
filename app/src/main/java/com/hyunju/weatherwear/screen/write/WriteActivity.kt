package com.hyunju.weatherwear.screen.write

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.hyunju.weatherwear.screen.write.gallery.GalleryActivity
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.databinding.ActivityWriteBinding
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.model.WriteModel
import com.hyunju.weatherwear.screen.base.BaseActivity
import com.hyunju.weatherwear.screen.dailylook.detail.WeatherWearDetailActivity
import com.hyunju.weatherwear.screen.dialog.ConfirmDialog
import com.hyunju.weatherwear.screen.dialog.ConfirmDialogInterface
import com.hyunju.weatherwear.screen.dialog.PhotoOption
import com.hyunju.weatherwear.screen.dialog.SelectPhotoOptionBottomSheetDialog
import com.hyunju.weatherwear.screen.write.camera.CameraActivity
import com.hyunju.weatherwear.screen.write.location.SearchLocationActivity
import com.hyunju.weatherwear.util.date.setTimeInMillisToStringWithDot
import com.hyunju.weatherwear.util.date.setTimeInMillisToString
import com.hyunju.weatherwear.util.date.setStringToStringWithDot
import com.hyunju.weatherwear.util.date.setStringToCalendar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class WriteActivity : BaseActivity<WriteViewModel, ActivityWriteBinding>(), ConfirmDialogInterface {

    companion object {
        fun newIntent(context: Context) = Intent(context, WriteActivity::class.java)

        val photoPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    override val viewModel by viewModels<WriteViewModel>()

    override fun getViewBinding() = ActivityWriteBinding.inflate(layoutInflater)

    override val layoutId = R.layout.activity_write
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

    private val searchLocationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getParcelableExtra<SearchResultEntity>(SearchLocationActivity.LOCATION_KEY)
                    ?.let {
                        selectLocation = it
                        binding.selectLocationTextView.text = selectLocation?.name

                        getSelectedWeatherInfo()
                    }
            }
        }

    private var selectDate: Calendar = Calendar.getInstance().apply {
        set(
            this.get(Calendar.YEAR),
            this.get(Calendar.MONTH),
            this.get(Calendar.DAY_OF_MONTH)
        )
    }
    private var selectLocation: SearchResultEntity? = null
    private var selectPhoto: Uri? = null

    override fun onResume() {
        super.onResume()
        isEnabledWriteButton()
    }

    @SuppressLint("SetTextI18n")
    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }

        // 날짜 선택
        selectDateTextView.setOnClickListener { showDatePickerDialog() }
        // 위치 선택
        selectLocationTextView.setOnClickListener {
            searchLocationLauncher.launch(
                SearchLocationActivity.newIntent(this@WriteActivity)
            )
        }
        // 사진 선택
        selectPhotoView.setOnClickListener {
            checkHasPermission {
                showPictureUploadDialog()
            }
        }
        // 사진 선택 취소
        cancelPhotoButton.setOnClickListener { cancelSelectedPhoto() }
    }

    // 날짜 선택
    private fun showDatePickerDialog() {
        DatePickerDialog(
            this, R.style.Widget_WeatherWear_DatePickerDialog,
            { _, year, monthOfYear, dayOfMonth ->
                // 선택한 날짜
                val currentDate = Calendar.getInstance().apply {
                    set(year, monthOfYear, dayOfMonth)
                }

                binding.selectDateTextView.text = setTimeInMillisToStringWithDot(currentDate.timeInMillis)

                selectDate = currentDate
                getSelectedWeatherInfo()
            },
            selectDate.get(Calendar.YEAR),
            selectDate.get(Calendar.MONTH),
            selectDate.get(Calendar.DAY_OF_MONTH)
        ).apply {
//            datePicker.minDate =
//                Calendar.getInstance().apply { add(Calendar.DATE, -2) }.timeInMillis
            datePicker.maxDate = System.currentTimeMillis()
        }.show()
    }

    // 가져온 사진 셋팅
    private fun setPhotoFromUriData(uri: Uri) = with(binding) {
        selectPhoto = uri

        weatherWearImageView.load(uri.toString())
        selectPhotoView.isGone = true
        cancelPhotoButton.isVisible = true
    }

    // 선택한 사진 취소
    private fun cancelSelectedPhoto() = with(binding) {
        selectPhoto = null

        weatherWearImageView.setImageBitmap(null)
        weatherWearImageView.setBackgroundColor(getColor(R.color.snow))

        selectPhotoView.isVisible = true
        cancelPhotoButton.isGone = true
        writeButton.isEnabled = false
    }

    override fun observeData() = viewModel.writeStateLiveData.observe(this) {
        when (it) {
            is WriteState.Uninitialized -> handleUninitializedState()
            is WriteState.Loading -> handleLoadingState()
            is WriteState.Success -> handleSuccessState(it)
            is WriteState.Fail -> handleFailState()
            is WriteState.Register -> handleRegister(it)
            is WriteState.Error -> handleErrorState(it)
        }
    }

    private fun handleUninitializedState() = with(binding) {
        writeButton.isEnabled = false
    }

    private fun handleLoadingState() = with(binding) {
        loadingView.root.isVisible = true
        writeButton.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessState(state: WriteState.Success) = with(binding) {
        loadingView.root.isGone = true

        weatherTextView.text =
            "최고 기온 ${state.weatherInfo.TMX}°/ 최저 기온 ${state.weatherInfo.TMN}°/ ${state.weatherType}"

        selectDateTextView.text = setStringToStringWithDot(state.weatherInfo.date)
        selectLocationTextView.text = state.location.name

        selectDate = setStringToCalendar(state.weatherInfo.date)
        selectLocation = state.location

        // 등록하기
        writeButton.setOnClickListener {
            viewModel.uploadWeatherWear(
                WriteModel(
                    date = selectDate,
                    location = selectLocation?.name ?: getString(R.string.location_not_found),
                    weather = state.weatherInfo,
                    photo = selectPhoto!!,
                    diary = binding.diaryEditText.text.toString()
                )
            )
        }

        isEnabledWriteButton()
    }

    private fun handleFailState() = with(binding) {
        loadingView.root.isGone = true

        weatherTextView.text = getString(R.string.weather_and_temperature_info_not_found)

        // 등록하기
        writeButton.setOnClickListener {
            viewModel.uploadWeatherWear(
                WriteModel(
                    date = selectDate,
                    location = selectLocation?.name ?: getString(R.string.location_not_found),
                    weather = null,
                    photo = selectPhoto!!,
                    diary = binding.diaryEditText.text.toString()
                )
            )
        }

        isEnabledWriteButton()
    }

    private fun handleRegister(state: WriteState.Register) = with(binding) {
        loadingView.root.isGone = true

        startActivity(
            WeatherWearDetailActivity.newIntent(
                this@WriteActivity,
                state.id
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        )

        finish()
    }

    private fun handleErrorState(state: WriteState.Error) = with(binding) {
        loadingView.root.isGone = true
        writeButton.isEnabled = false

        weatherTextView.text = ""
        Toast.makeText(this@WriteActivity, getString(state.messageId), Toast.LENGTH_SHORT).show()
    }

    // 선택한 날씨 정보 가져오기
    private fun getSelectedWeatherInfo() {
        selectLocation?.let {
            viewModel.getWeatherInformation(
                searchResultEntity = it,
                date = setTimeInMillisToString(selectDate.timeInMillis)
            )
        } ?: run {
            Toast.makeText(
                this@WriteActivity,
                getString(R.string.fail_get_information),
                Toast.LENGTH_SHORT
            ).show()
        }
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
            // 권한이 있을 경우
            hasCameraPermission && hasExternalStoragePermission -> {
                uploadAction()
            }
            !hasCameraPermission && !hasExternalStoragePermission -> {
                photoPermissionLauncher.launch(photoPermissions)
            }
            // 이전에 거부한 경우, 권한 필요성 설명 및 권한 요청 / 다시 묻기까지 거부된 경우
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

    // 설정 메뉴로 이동
    override fun onYesButtonClick() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            data = uri
        }
        startActivity(intent)
    }

    // 등록하기 버튼 활성화 여부
    private fun isEnabledWriteButton() {
        binding.enable = (selectDate.toString().isNotEmpty())
                && (selectLocation != null) && (selectPhoto != null)
    }

}