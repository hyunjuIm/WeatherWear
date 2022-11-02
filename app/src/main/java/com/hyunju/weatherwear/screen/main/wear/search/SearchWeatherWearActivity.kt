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
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ActivitySearchWeatherWearBinding
import com.hyunju.weatherwear.extension.fromDpToPx
import com.hyunju.weatherwear.screen.base.BaseActivity
import com.hyunju.weatherwear.screen.dailylook.detail.WeatherWearDetailActivity
import com.hyunju.weatherwear.screen.main.wear.GridSpacingItemDecoration
import com.hyunju.weatherwear.screen.main.wear.WearAdapter
import com.hyunju.weatherwear.util.date.setMillisDateFormat
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

    override val viewModel by viewModels<SearchWeatherWearViewModel>()

    override fun getViewBinding() = ActivitySearchWeatherWearBinding.inflate(layoutInflater)

    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }

        bindingSearchOption()
        searchTextView.setOnClickListener { bindingSearchOption() }

        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(spanCount = 2, spacing = 16f.fromDpToPx())
        )
    }

    private fun bindingSearchOption() {
        when (option) {
            DATE -> {
                showDatePickerDialog()
            }
            TEMPERATURES -> {}
        }
    }

    // 날짜 선택
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this, R.style.Widget_WeatherWear_SpinnerDatePicker,
            { _, year, monthOfYear, dayOfMonth ->
                // 선택한 날짜
                val currentDate = Calendar.getInstance().apply {
                    set(year, monthOfYear, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                binding.searchTextView.text = setMillisDateFormat(currentDate.timeInMillis)
                viewModel.searchDate(currentDate.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }.show()
    }

    override fun observeData() = viewModel.searchWeatherWearStateLiveData.observe(this) {
        when (it) {
            is SearchWeatherWearState.Loading -> handleLoadingState()
            is SearchWeatherWearState.Success -> handleSuccessState(it)
            is SearchWeatherWearState.Error -> handleErrorState(it)
            else -> Unit
        }
    }

    private fun handleLoadingState() = with(binding) {
        loadingView.isVisible = true
        recyclerView.isGone = true
    }

    private fun handleSuccessState(state: SearchWeatherWearState.Success) = with(binding) {
        loadingView.isGone = true

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
        loadingView.isGone = true
        Toast.makeText(this@SearchWeatherWearActivity, state.messageId, Toast.LENGTH_SHORT).show()
    }
}