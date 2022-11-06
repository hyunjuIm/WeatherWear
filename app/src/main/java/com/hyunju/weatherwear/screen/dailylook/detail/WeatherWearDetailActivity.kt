package com.hyunju.weatherwear.screen.dailylook.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ActivityWeatherWearDetailBinding
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.screen.base.BaseActivity
import com.hyunju.weatherwear.screen.dialog.YesOrNoDialog
import com.hyunju.weatherwear.screen.dialog.YesOrNoDialogInterface
import com.hyunju.weatherwear.util.date.setMillisDateFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherWearDetailActivity :
    BaseActivity<WeatherWearDetailViewModel, ActivityWeatherWearDetailBinding>(),
    YesOrNoDialogInterface {

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

    override val transitionMode = TransitionMode.HORIZON

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

        titleTextView.text = setMillisDateFormat(state.weatherWearInfo.date.time)

        photoImageView.load(state.weatherWearInfo.photo)
        photoImageView.setOnClickListener {
            startActivity(
                PhotoDetailActivity.newIntent(
                    this@WeatherWearDetailActivity
                )
            )
        }

        locationTextView.text = state.weatherWearInfo.location
        maxTemperatureTextView.text = "${state.weatherWearInfo.maxTemperature}°"
        minTemperatureTextView.text = "${state.weatherWearInfo.minTemperature}°"
        weatherTypeTextView.text = state.weatherWearInfo.weatherType
        diaryTextView.text = state.weatherWearInfo.diary

        deleteButton.setOnClickListener { showDeleteDialog() }
    }

    private fun handleDelete() = with(binding) {
        loadingView.isGone = true

        Toast.makeText(
            this@WeatherWearDetailActivity,
            getString(R.string.deleted),
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

    private fun showDeleteDialog() {
        YesOrNoDialog(
            yesOrNoDialogInterface = this,
            title = null,
            message = getString(R.string.ask_for_deletion),
            positiveButton = getString(R.string.yes),
            negativeButton = getString(R.string.no)
        ).show(this.supportFragmentManager, "YesOrNoDialog")
    }

    override fun onYesButtonClick(value: Boolean, tag: String) {
        if (value) viewModel.deleteWeatherWearDate(weatherWearId)
    }

}