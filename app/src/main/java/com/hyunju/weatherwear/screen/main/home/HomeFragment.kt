package com.hyunju.weatherwear.screen.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.FragmentHomeBinding
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.screen.base.BaseFragment
import com.hyunju.weatherwear.util.date.getCurrentTime
import com.hyunju.weatherwear.util.weather.Time
import com.hyunju.weatherwear.util.weather.getSensibleTemperature
import com.hyunju.weatherwear.util.clothes.pickClothes
import com.hyunju.weatherwear.util.weather.setMatchingUiWeatherInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {

    companion object {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        fun newInstance() = HomeFragment()

        const val TAG = "HomeFragment"
    }

    override val viewModel by viewModels<HomeViewModel>()

    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val responsePermissions = permissions.entries.filter {
                it.key == Manifest.permission.ACCESS_FINE_LOCATION
                        || it.key == Manifest.permission.ACCESS_COARSE_LOCATION
            }
            if (responsePermissions.filter { it.value }.size == locationPermissions.size) {
                setMyLocationListener()
            } else {
                with(binding.locationTextView) {
                    setText(R.string.please_setup_your_location_permission)
                    setOnClickListener {
                        getMyLocation()
                    }
                }
                Toast.makeText(
                    requireContext(),
                    R.string.can_not_assigned_permission,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val adapter by lazy { PickClothesAdapter() }

    override fun initViews() = with(binding) {
        // 시간에 따른 상태바 색상, 배경 그라데이션 변경
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars = false

            if (getCurrentTime().toInt() in Time.AFTERNOON) {
                statusBarColor = ContextCompat.getColor(requireContext(), R.color.sky_100)
            } else {
                statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_500)
                backgroundLayout.setBackgroundResource(R.drawable.bg_gradient_blue_navy)
            }
        }

        refresh.setOnRefreshListener {
            getMyLocation()
        }

        weatherWearImageView.setOnClickListener {

        }

        clothesRecyclerView.adapter = adapter
    }

    override fun observeData() = viewModel.homeStateLiveData.observe(this) {
        when (it) {
            is HomeState.Uninitialized -> handleUninitializedState()
            is HomeState.Loading -> handleLoadingState()
            is HomeState.Success -> handleSuccessState(it)
            is HomeState.Error -> handleErrorState(it)
        }
    }

    private fun handleUninitializedState() = with(binding) {
        locationTextView.text = getString(R.string.finding_location)
        getMyLocation()
    }

    private fun handleLoadingState() = with(binding) {
        locationTextView.text = getString(R.string.loading)
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessState(state: HomeState.Success) = with(binding) {
        refresh.isRefreshing = false

        locationTextView.text = state.location
        nowTemperatureTextView.text = state.weatherInfo.TMP.toString() + "°"

        val weatherType = setMatchingUiWeatherInfo(state.weatherInfo)
        weatherIconImageView.load(weatherType.image)
        weatherTextView.text = weatherType.text

        val sensibleTemperature =
            getSensibleTemperature(state.weatherInfo.TMP, state.weatherInfo.WSD)
        simpleTemperatureTextView.text =
            "체감온도 ${sensibleTemperature}° / 최저 ${state.weatherInfo.TMN}° / 최고 ${state.weatherInfo.TMX}°"

        adapter.submitList(pickClothes(state.weatherInfo.TMX))
    }

    private fun handleErrorState(state: HomeState.Error) = with(binding) {
        refresh.isRefreshing = false
        locationTextView.text = getString(state.messageId)
    }

    // 위치 퍼미션 얻기
    private fun getMyLocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnable) {
            locationPermissionLauncher.launch(locationPermissions)
        }
    }

    // 위치 정보 얻어오기
    @SuppressLint("MissingPermission")
    private fun setMyLocationListener() {
        val minTime: Long = 1500
        val minDistance = 100f

        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }

        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )
            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, myLocationListener
            )
        }
    }

    // 리스너로 매번 호출하면 안되기 때문에
    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener)
        }
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            viewModel.updateLocationWeather(location.latitude, location.longitude)
            removeLocationListener()
        }
    }
}