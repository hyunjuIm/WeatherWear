package com.hyunju.weatherwear.screen.write

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.hyunju.weatherwear.screen.write.gallery.GalleryActivity
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.databinding.ActivityWriteBinding
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.model.WriteModel
import com.hyunju.weatherwear.screen.base.BaseActivity
import com.hyunju.weatherwear.screen.dialog.ConfirmDialog
import com.hyunju.weatherwear.screen.dialog.ConfirmDialogInterface
import com.hyunju.weatherwear.screen.write.camera.CameraActivity
import com.hyunju.weatherwear.screen.write.location.SearchLocationActivity
import com.hyunju.weatherwear.util.date.setMillisDateFormat
import com.hyunju.weatherwear.util.date.setMillisDateFormatForApi
import com.hyunju.weatherwear.util.date.setStringDateFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class WriteActivity : BaseActivity<WriteViewModel, ActivityWriteBinding>(), ConfirmDialogInterface {

    override val viewModel by viewModels<WriteViewModel>()

    override fun getViewBinding() = ActivityWriteBinding.inflate(layoutInflater)

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

    private val searchLocationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectLocation =
                    result.data?.getParcelableExtra(SearchLocationActivity.LOCATION_KEY)
                binding.selectLocationTextView.text = selectLocation?.name

                getSelectedWeatherInfo()
            }
        }

    private val weatherInfo by lazy { intent.getParcelableExtra<WeatherEntity>(WEATHER_KEY) }
    private val weatherType by lazy { intent.getStringExtra(WEATHER_TYPE_KEY) }
    private val location by lazy { intent.getParcelableExtra<SearchResultEntity>(LOCATION_KEY) }

    private var selectDate: Calendar = Calendar.getInstance()
    private var selectLocation: SearchResultEntity? = null
    private var selectPhoto: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write)
        initState()
    }

    override fun onResume() {
        super.onResume()
        isEnabledWriteButton()
    }

    @SuppressLint("SetTextI18n")
    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }

        weatherInfo?.let {
            weatherTextView.text = "최고 기온 ${it.TMX}°/ 최저 기온 ${it.TMN}°/ $weatherType"
            selectDateTextView.text = setStringDateFormat(it.date)
            selectLocationTextView.text = location?.name

            selectLocation = location
        }

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

        // 등록하기
        writeButton.setOnClickListener {
            uploadWeatherWear()
        }
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
                selectDate = currentDate
                getSelectedWeatherInfo()

                binding.selectDateTextView.text = setMillisDateFormat(currentDate.timeInMillis)
            },
            selectDate.get(Calendar.YEAR),
            selectDate.get(Calendar.MONTH),
            selectDate.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate =
                Calendar.getInstance().apply { add(Calendar.DATE, -2) }.timeInMillis
            datePicker.maxDate = System.currentTimeMillis()
            title = "최근 3일만 선택할 수 있어요 :)"
        }.show()
    }

    // 가져온 사진 셋팅
    private fun setPhotoFromUriData(uri: Uri) = with(binding) {
        selectPhoto = uri

        weatherWearImageView.load(uri.toString(), 0f)
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
            is WriteState.Register -> handleSuccessRegister(it)
            is WriteState.Error -> handleErrorState(it)
        }
    }

    private fun handleUninitializedState() = with(binding) {
        writeButton.isEnabled = false
    }

    private fun handleLoadingState() = with(binding) {
        loadingView.isVisible = true
        writeButton.isEnabled = false
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessState(state: WriteState.Success) = with(binding) {
        loadingView.isGone = true

        weatherTextView.text =
            "최고 기온 ${state.weatherInfo.TMX}°/ 최저 기온 ${state.weatherInfo.TMN}°/ ${state.weatherType.text}"

        isEnabledWriteButton()
    }

    private fun handleSuccessRegister(state: WriteState.Register) = with(binding) {
        loadingView.isGone = true
        Toast.makeText(this@WriteActivity, "업로드 완료 : ${state.id}", Toast.LENGTH_SHORT).show()
    }

    private fun handleErrorState(state: WriteState.Error) = with(binding) {
        loadingView.isGone = true
        writeButton.isEnabled = false

        weatherTextView.text = ""
        Toast.makeText(this@WriteActivity, getString(state.messageId), Toast.LENGTH_SHORT).show()
    }

    // 선택한 날씨 정보 가져오기
    private fun getSelectedWeatherInfo() {
        selectLocation?.let {
            viewModel.getWeatherInformation(
                locationLatLngEntity = it.locationLatLng,
                date = setMillisDateFormatForApi(selectDate.timeInMillis)
            )
        }
    }

    private fun showPictureUploadDialog() {
        AlertDialog.Builder(this)
            .setMessage("사진 첨부 방식을 선택해주세요.")
            .setPositiveButton("카메라") { _, _ ->
                cameraLauncher.launch(
                    CameraActivity.newIntent(this)
                )
            }
            .setNegativeButton("갤러리") { _, _ ->
                galleryLauncher.launch(
                    GalleryActivity.newIntent(this)
                )
            }
            .create()
            .show()
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

    private fun uploadWeatherWear() {
        viewModel.uploadWeatherWear(
            WriteModel(
                date = selectDate,
                location = selectLocation!!,
                weather = weatherInfo!!,
                photo = selectPhoto!!,
                diary = binding.diaryEditText.text.toString()
            )
        )
    }

    // 등록하기 버튼 활성화 여부
    private fun isEnabledWriteButton() {
        binding.enable = (selectDate.toString().isNotEmpty())
                && (selectLocation != null) && (selectPhoto != null)
    }

    companion object {
        const val WEATHER_KEY = "weather"
        const val WEATHER_TYPE_KEY = "weatherType"
        const val LOCATION_KEY = "location"

        fun newIntent(
            context: Context,
            weatherEntity: WeatherEntity,
            weatherType: String,
            location: SearchResultEntity
        ) = Intent(context, WriteActivity::class.java).apply {
            putExtra(WEATHER_KEY, weatherEntity)
            putExtra(WEATHER_TYPE_KEY, weatherType)
            putExtra(LOCATION_KEY, location)
        }

        val photoPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

}