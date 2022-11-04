package com.hyunju.weatherwear.screen.main.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.LocationLatLngEntity
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.databinding.FragmentHomeBinding
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.screen.base.BaseFragment
import com.hyunju.weatherwear.screen.dailylook.detail.WeatherWearDetailActivity
import com.hyunju.weatherwear.screen.dialog.ConfirmDialog
import com.hyunju.weatherwear.screen.dialog.ConfirmDialogInterface
import com.hyunju.weatherwear.screen.write.WriteActivity
import com.hyunju.weatherwear.screen.write.location.SearchLocationActivity
import com.hyunju.weatherwear.util.date.getCurrentTime
import com.hyunju.weatherwear.util.weather.Time
import com.hyunju.weatherwear.util.clothes.pickClothes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(), ConfirmDialogInterface {

    companion object {
        fun newInstance() = HomeFragment()

        const val TAG = "HomeFragment"

        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override val viewModel by viewModels<HomeViewModel>()

    override fun getViewBinding() = FragmentHomeBinding.inflate(layoutInflater)

    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { permission -> permission.value }) {
                setMyLocationListener()
            } else {
                binding.locationTextView.setText(R.string.please_setup_your_location_permission)
                binding.locationTextView.setOnClickListener {
                    showPermissionContextPopup()
                }
            }
        }

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

    private val adapter by lazy { PickClothesAdapter() }

    override fun initViews() = with(binding) {
        // 시간에 따른 상태바 색상, 배경 그라데이션 변경
        changeStatusBarForTime()

        clothesRecyclerView.adapter = adapter

        // SwipeRefreshLayout
        refresh.setOnRefreshListener {
            changeStatusBarForTime()
            viewModel.fetchData()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (hidden) {
            setBaseStatusBar()
        } else {
            changeStatusBarForTime()
        }
    }

    // 시간에 따른 상태바 색상, 배경 그라데이션 변경
    private fun changeStatusBarForTime() {
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars = false

            if (getCurrentTime().toInt() in Time.AFTERNOON) {
                statusBarColor = ContextCompat.getColor(requireContext(), R.color.sky_100)
                binding.backgroundLayout.setBackgroundResource(R.drawable.bg_gradient_blue_sky)
            } else {
                statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_500)
                binding.backgroundLayout.setBackgroundResource(R.drawable.bg_gradient_blue_navy)
            }
        }
    }

    private fun setBaseStatusBar() {
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars =
                true
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        }
    }

    override fun observeData() {
        viewModel.homeStateLiveData.observe(this) {
            when (it) {
                is HomeState.Loading -> handleLoadingState()
                is HomeState.Pick -> handlePickState(it)
                is HomeState.Success -> handleSuccessState(it)
                is HomeState.Find -> handleFindState(it)
                is HomeState.Error -> handleErrorState(it)
                else -> Unit
            }
        }

        viewModel.updateUIState.observe(this) {
            if (it) viewModel.fetchData()
        }
    }

    private fun handleLoadingState() = with(binding) {
        locationTextView.text = getString(R.string.loading)
        loadingView.isVisible = true
    }

    private fun handlePickState(state: HomeState.Pick) = with(binding) {
        state.weatherWearEntity?.let { item ->
            weatherWearImageView.load(item.photo)
            weatherWearCardView.setOnClickListener {
                startActivity(
                    WeatherWearDetailActivity.newIntent(requireContext(), item.id)
                )
            }
            return@with
        }

        weatherWearCardView.setOnClickListener {
            startActivity(
                WriteActivity.newIntent(requireContext())
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccessState(state: HomeState.Success) = with(binding) {
        refresh.isRefreshing = false
        loadingView.isGone = true

        locationTextView.text = state.location.name
        nowTemperatureTextView.text = state.weatherInfo.TMP.toString() + "°"
        weatherIconImageView.load(state.weatherType.image)
        weatherTextView.text = state.weatherType.text
        commentTextView.text = state.comment
        simpleTemperatureTextView.text =
            "체감온도 ${state.sensibleTemperature}° / 최저 ${state.weatherInfo.TMN}° / 최고 ${state.weatherInfo.TMX}°"

        adapter.submitList(pickClothes(state.weatherInfo.TMX))
    }

    private fun handleFindState(state: HomeState.Find) = with(binding) {
        refresh.isRefreshing = false
        loadingView.isGone = true

        state.location?.let {
            viewModel.updateLocationWeather(it)

            locationTextView.setOnClickListener {
                searchLocationLauncher.launch(
                    SearchLocationActivity.newIntent(requireContext())
                )
            }

            return@with
        }

        getMyLocation()
    }

    private fun handleErrorState(state: HomeState.Error) = with(binding) {
        refresh.isRefreshing = false
        loadingView.isGone = true

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

    private fun showPermissionContextPopup() {
        activity?.supportFragmentManager?.let {
            ConfirmDialog(
                confirmDialogInterface = this,
                text = getString(R.string.setting_location_permission)
            ).show(it, "ConfirmDialog")
        }
    }

    override fun onYesButtonClick() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            data = uri
        }
        startActivity(intent)
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            viewModel.getLocationData(LocationLatLngEntity(location.latitude, location.longitude))
            removeLocationListener()
        }
    }
}