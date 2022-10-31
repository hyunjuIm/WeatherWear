package com.hyunju.weatherwear.screen.dailylook.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.hyunju.weatherwear.databinding.ActivityWeatherWearDetailBinding
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.screen.base.BaseActivity
import com.hyunju.weatherwear.util.date.setMillisDateFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherWearDetailActivity :
    BaseActivity<WeatherWearDetailViewModel, ActivityWeatherWearDetailBinding>() {

    companion object {
        const val ID_KEY = "id"
        const val DELETE_KEY = "delete"

        fun newIntent(context: Context, id: Long) =
            Intent(context, WeatherWearDetailActivity::class.java).apply {
                putExtra(ID_KEY, id)
            }
    }

    override val viewModel by viewModels<WeatherWearDetailViewModel>()

    override fun getViewBinding() = ActivityWeatherWearDetailBinding.inflate(layoutInflater)

    private val weatherWearId by lazy { intent.getLongExtra(ID_KEY, -1) }

    override fun initViews() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        viewModel.getWeatherWearData(weatherWearId)
    }

    @SuppressLint("SetTextI18n")
    override fun observeData() = viewModel.weatherWearDetailLiveData.observe(this) {
        when (it) {
            is WeatherWearDetailState.Loading -> handleLoadingState()
            is WeatherWearDetailState.Success -> handleSuccessState(it)
            is WeatherWearDetailState.Delete -> handleDelete()
            is WeatherWearDetailState.Error -> handleErrorState(it)
            else -> Unit
        }
    }

    private fun handleLoadingState() = with(binding) {
        loadingView.isVisible = true
    }

    private fun handleSuccessState(state: WeatherWearDetailState.Success) = with(binding) {
        loadingView.isGone = true

        titleTextView.text = setMillisDateFormat(state.weatherWearInfo.date)
        photoImageView.load(state.weatherWearInfo.photo)
        locationTextView.text = state.weatherWearInfo.location
        maxTemperatureTextView.text = "${state.weatherWearInfo.maxTemperature}°"
        minTemperatureTextView.text = "${state.weatherWearInfo.minTemperature}°"
        weatherTypeTextView.text = state.weatherWearInfo.weatherType
        diaryTextView.text = state.weatherWearInfo.diary

        deleteButton.setOnClickListener {
            AlertDialog.Builder(this@WeatherWearDetailActivity)
                .setMessage("삭제한 데이터는 되돌릴 수 없습니다.\n그래도 삭제하시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    viewModel.deleteWeatherWearDate(weatherWearId)
                }
                .setNegativeButton("아니오") { _, _ ->
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                .create()
                .show()
        }
    }

    private fun handleDelete() = with(binding) {
        loadingView.isGone = true

        Toast.makeText(
            this@WeatherWearDetailActivity,
            "삭제되었습니다.",
            Toast.LENGTH_SHORT
        ).show()

        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(DELETE_KEY, true)
        })

        finish()
    }

    private fun handleErrorState(state: WeatherWearDetailState.Error) = with(binding) {
        loadingView.isGone = true

        Toast.makeText(
            this@WeatherWearDetailActivity,
            getString(state.messageId),
            Toast.LENGTH_SHORT
        ).show()
    }

}