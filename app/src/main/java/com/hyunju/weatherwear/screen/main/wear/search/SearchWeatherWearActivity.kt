package com.hyunju.weatherwear.screen.main.wear.search

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ActivitySearchWeatherWearBinding
import com.hyunju.weatherwear.extension.fromDpToPx
import com.hyunju.weatherwear.screen.base.BaseActivity
import com.hyunju.weatherwear.screen.dailylook.detail.WeatherWearDetailActivity
import com.hyunju.weatherwear.screen.dialog.SelectTemperatureBottomSheetDialog
import com.hyunju.weatherwear.screen.main.wear.WearAdapter
import com.hyunju.weatherwear.util.view.GridSpacingItemDecoration
import com.hyunju.weatherwear.util.weather.Temperatures
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SearchWeatherWearActivity :
    BaseActivity<SearchWeatherWearViewModel, ActivitySearchWeatherWearBinding>() {

    companion object {
        const val OPTION_KEY = "option"

        const val DATE = "date"
        const val TEMPERATURES = "temperatures"

        fun newIntent(context: Context, option: String) =
            Intent(context, SearchWeatherWearActivity::class.java).apply {
                putExtra(OPTION_KEY, option)
            }
    }

    override val viewModel by viewModels<SearchWeatherWearViewModel>()

    override fun getViewBinding() = ActivitySearchWeatherWearBinding.inflate(layoutInflater)

    override val transitionMode = TransitionMode.HORIZON

    private val option by lazy { intent.getStringExtra(OPTION_KEY) }

    private val detailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getBooleanExtra(WeatherWearDetailActivity.DELETE_KEY, false)
                    ?.let { delete -> if (delete) viewModel.fetchData() } ?: kotlin.run {
                    Toast.makeText(this, R.string.request_error, Toast.LENGTH_SHORT).show()
                }
            }
        }

    private val adapter by lazy {
        WearAdapter(clickItem = {
            detailLauncher.launch(
                WeatherWearDetailActivity.newIntent(this, it)
            )
        })
    }

    lateinit var saveStateDate: Pair<Long, Long>
    lateinit var saveStateTemperature: Pair<String, Temperatures>

    private val selectTemperatureBottomSheetDialog by lazy {
        SelectTemperatureBottomSheetDialog { standard, temperature ->
            saveStateTemperature = Pair(standard, temperature)
            viewModel.searchTemperature(standard, temperature)
        }
    }

    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }

        bindingSearchOption()
        searchTextView.setOnClickListener { bindingSearchOption() }

        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(spanCount = 2, spacing = 16f.fromDpToPx())
        )
    }

    private fun bindingSearchOption() {
        when (option) {
            DATE -> showDateRangePicker()
            TEMPERATURES -> showSelectTemperatureBottomSheetDialog()
        }
    }

    private fun showDateRangePicker() {
        MaterialDatePicker.Builder.dateRangePicker().apply {
            setTitleText("조회할 기간을 선택해주세요 :)")
            setPositiveButtonText("선택")
            setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setEnd(System.currentTimeMillis())
                    .setValidator(DateValidatorPointBackward.now())
                    .build()
            )
        }.build().apply {
            show(supportFragmentManager, this.toString())
            addOnNegativeButtonClickListener { this.dismiss() }
            addOnPositiveButtonClickListener {
                saveStateDate = Pair(it.first, it.second)
                viewModel.searchDate(start = it.first, end = it.second)
            }
        }
    }

    private fun showSelectTemperatureBottomSheetDialog() {
        selectTemperatureBottomSheetDialog.show(
            supportFragmentManager,
            "selectTemperatureBottomSheetDialog"
        )
    }

    override fun observeData() {
        viewModel.searchWeatherWearStateLiveData.observe(this) {
            when (it) {
                is SearchWeatherWearState.Loading -> handleLoadingState()
                is SearchWeatherWearState.Success -> handleSuccessState(it)
                is SearchWeatherWearState.Error -> handleErrorState(it)
                else -> Unit
            }
        }

        viewModel.updateUIState.observe(this) {
            if (it) {
                when (option) {
                    DATE -> viewModel.searchDate(saveStateDate.first, saveStateDate.second)
                    TEMPERATURES -> viewModel.searchTemperature(
                        saveStateTemperature.first,
                        saveStateTemperature.second
                    )
                }
            }
        }
    }

    private fun handleLoadingState() = with(binding) {
        loadingView.root.isVisible = true
        recyclerView.isGone = true
    }

    private fun handleSuccessState(state: SearchWeatherWearState.Success) = with(binding) {
        loadingView.root.isGone = true

        binding.searchTextView.text = state.searchText

        if (state.weatherWearList.isNotEmpty()) {
            recyclerView.isVisible = true
            emptyResultTextView.isGone = true
        } else {
            recyclerView.isGone = true
            emptyResultTextView.isVisible = true
        }

        adapter.submitList(state.weatherWearList.toMutableList())
    }

    private fun handleErrorState(state: SearchWeatherWearState.Error) = with(binding) {
        loadingView.root.isGone = true
        Toast.makeText(this@SearchWeatherWearActivity, state.messageId, Toast.LENGTH_SHORT)
            .show()
    }
}