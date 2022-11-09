package com.hyunju.weatherwear.screen.main.weather

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.LocationLatLngEntity
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.databinding.FragmentWeatherBinding
import com.hyunju.weatherwear.extension.clear
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.screen.base.BaseFragment
import com.hyunju.weatherwear.screen.write.location.SearchLocationActivity
import com.hyunju.weatherwear.util.date.setStringToHangeulFullDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : BaseFragment<WeatherViewModel, FragmentWeatherBinding>() {

    companion object {
        fun newInstance() = WeatherFragment()

        const val TAG = "WeatherFragment"
    }

    override val viewModel by viewModels<WeatherViewModel>()

    override fun getViewBinding() = FragmentWeatherBinding.inflate(layoutInflater)

    private val searchLocationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getParcelableExtra<SearchResultEntity?>(SearchLocationActivity.LOCATION_KEY)
                    ?.let {
                        viewModel.getLocationData(
                            LocationLatLngEntity(
                                latitude = it.locationLatLng.latitude,
                                longitude = it.locationLatLng.longitude
                            )
                        )
                    }
            }
        }

    private val timeAdapter by lazy { TimeWeatherAdapter() }
    private val weekAdapter by lazy { WeekWeatherAdapter() }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) changeStatusBarForTime(binding.backgroundLayout)
    }

    override fun initViews() = with(binding) {
        // 시간에 따른 상태바 색상, 배경 그라데이션 변경
        changeStatusBarForTime(backgroundLayout)

        timeRecyclerView.adapter = timeAdapter
        timeRecyclerView.itemAnimator = null

        weekRecyclerView.adapter = weekAdapter
        weekRecyclerView.itemAnimator = null

        // SwipeRefreshLayout
        refresh.setOnRefreshListener {
            changeStatusBarForTime(backgroundLayout)
            viewModel.fetchData()
        }
    }

    override fun observeData() = viewModel.weatherStateLiveData.observe(this) {
        when (it) {
            is WeatherState.Loading -> handleLoadingState()
            is WeatherState.Success -> handleSuccessState(it)
            is WeatherState.Find -> handleFindState(it)
            is WeatherState.Error -> handleErrorState(it)
            else -> Unit
        }
    }

    private fun handleLoadingState() = with(binding) {
        locationTextView.text = getString(R.string.loading)
        loadingView.root.isVisible = true
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessState(state: WeatherState.Success) = with(binding) {
        refresh.isRefreshing = false
        loadingView.root.isGone = true

        when (state) {
            is WeatherState.Success.Today -> handleTodayState(state)
            is WeatherState.Success.Time -> handleTimeState(state)
            is WeatherState.Success.Week -> handleWeekState(state)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleTodayState(state: WeatherState.Success.Today) = with(binding) {
        locationTextView.text = state.location.name
        dateTextView.text = setStringToHangeulFullDate(state.weatherInfo.date)

        weatherIconImageView.clear()
        weatherIconImageView.load(state.weatherType.image)

        nowTemperatureTextView.text = "${state.weatherInfo.TMP}°"
        weatherTextView.text = state.weatherType.text
        simpleTemperatureTextView.text =
            "최고 ${state.weatherInfo.TMX}° / 최저 ${state.weatherInfo.TMN}°"
        sensibleTemperatureTextView.text = "(체감온도 ${state.sensibleTemperature}°)"
    }

    @SuppressLint("SetTextI18n")
    private fun handleTimeState(state: WeatherState.Success.Time) {
        timeAdapter.submitList(state.timeWeatherInfo)
    }

    @SuppressLint("SetTextI18n")
    private fun handleWeekState(state: WeatherState.Success.Week) {
        weekAdapter.submitList(state.weekWeatherList)
    }

    private fun handleFindState(state: WeatherState.Find) = with(binding) {
        refresh.isRefreshing = false
        loadingView.root.isGone = true

        state.location?.let {
            viewModel.updateLocationWeather(it)

            locationTextView.setOnClickListener {
                searchLocationLauncher.launch(
                    SearchLocationActivity.newIntent(requireContext())
                )
            }

            return@with
        }
    }

    private fun handleErrorState(state: WeatherState.Error) = with(binding) {
        refresh.isRefreshing = false
        loadingView.root.isGone = true

        locationTextView.text = getString(state.messageId)
    }

}