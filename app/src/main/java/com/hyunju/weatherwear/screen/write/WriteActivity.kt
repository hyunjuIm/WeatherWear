package com.hyunju.weatherwear.screen.write

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.databinding.ActivityWriteBinding
import com.hyunju.weatherwear.screen.base.BaseActivity
import com.hyunju.weatherwear.screen.write.location.SearchLocationActivity
import com.hyunju.weatherwear.util.date.setMillisDateFormat
import com.hyunju.weatherwear.util.date.setMillisDateFormatForApi
import com.hyunju.weatherwear.util.date.setStringDateFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class WriteActivity : BaseActivity<WriteViewModel, ActivityWriteBinding>() {

    override val viewModel by viewModels<WriteViewModel>()

    override fun getViewBinding() = ActivityWriteBinding.inflate(layoutInflater)

    private val searchLocationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectLocation =
                    result.data?.getParcelableExtra(SearchLocationActivity.LOCATION_KEY)
                binding.selectLocationTextView.text = selectLocation?.name

                getWeatherInfo()
            }
        }

    private val weatherInfo by lazy { intent.getParcelableExtra<WeatherEntity>(WEATHER_KEY) }
    private val weatherType by lazy { intent.getStringExtra(WEATHER_TYPE_KEY) }
    private val location by lazy { intent.getStringExtra(LOCATION_KEY) }

    private var selectDate: Calendar = Calendar.getInstance()
    private var selectLocation: SearchResultEntity? = null

    @SuppressLint("SetTextI18n")
    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }

        weatherInfo?.let {
            weatherTextView.text = "최고 기온 ${it.TMX}°/ 최저 기온 ${it.TMN}°/ $weatherType"
            selectDateTextView.text = setStringDateFormat(it.date)
            selectLocationTextView.text = location
        }

        selectDateTextView.setOnClickListener {
            showDatePickerDialog()
        }

        selectLocationTextView.setOnClickListener {
            searchLocationLauncher.launch(
                SearchLocationActivity.newIntent(this@WriteActivity)
            )
        }
    }

    // 날짜 선택
    private fun showDatePickerDialog() {
        DatePickerDialog(
            this, R.style.Widget_WeatherWear_DatePickerDialog,
            { _, year, monthOfYear, dayOfMonth ->
                // 선택한 날짜
                val currentDate =
                    Calendar.getInstance().apply { set(year, monthOfYear, dayOfMonth) }
                binding.selectDateTextView.text = setMillisDateFormat(currentDate.timeInMillis)
                selectDate = currentDate
                getWeatherInfo()
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

    override fun observeData() = viewModel.writeStateLiveData.observe(this) {
        when (it) {
            is WriteState.Uninitialized -> handleUninitializedState()
            is WriteState.Loading -> handleLoadingState()
            is WriteState.Success -> handleSuccessState(it)
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
        writeButton.isEnabled = true

        weatherTextView.text =
            "최고 기온 ${state.weatherInfo.TMX}°/ 최저 기온 ${state.weatherInfo.TMN}°/ ${state.weatherType.text}"
    }

    private fun handleErrorState(state: WriteState.Error) = with(binding) {
        loadingView.isGone = true
        writeButton.isEnabled = false

        weatherTextView.text = ""
        Toast.makeText(this@WriteActivity, getString(state.messageId), Toast.LENGTH_SHORT).show()
    }

    private fun getWeatherInfo() {
        selectLocation?.let {
            viewModel.getWeatherInformation(
                locationLatLngEntity = it.locationLatLng,
                date = setMillisDateFormatForApi(selectDate.timeInMillis)
            )
        }
    }

    companion object {
        const val WEATHER_KEY = "weather"
        const val WEATHER_TYPE_KEY = "weatherType"
        const val LOCATION_KEY = "location"

        fun newIntent(
            context: Context,
            weatherEntity: WeatherEntity,
            weatherType: String,
            location: String
        ) =
            Intent(context, WriteActivity::class.java).apply {
                putExtra(WEATHER_KEY, weatherEntity)
                putExtra(WEATHER_TYPE_KEY, weatherType)
                putExtra(LOCATION_KEY, location)
            }

    }

}