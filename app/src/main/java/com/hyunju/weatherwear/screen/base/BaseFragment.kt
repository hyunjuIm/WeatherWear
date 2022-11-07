package com.hyunju.weatherwear.screen.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.util.date.getStringCurrentTime
import com.hyunju.weatherwear.util.weather.Time
import kotlinx.coroutines.Job

abstract class BaseFragment<VM : BaseViewModel, VB : ViewBinding> : Fragment() {

    abstract val viewModel: VM

    protected lateinit var binding: VB
    abstract fun getViewBinding(): VB

    private lateinit var fetchJob: Job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initState()
    }

    open fun initState() {
        arguments?.let {
            viewModel.storeState(it)
        }
        initViews()
        fetchJob = viewModel.fetchData()
        observeData()
    }

    open fun initViews() = Unit

    // 시간에 따른 상태바 색상, 배경 그라데이션 변경
    fun changeStatusBarForTime(background: ConstraintLayout) {
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars = false

            if (getStringCurrentTime().toInt() in Time.AFTERNOON) {
                statusBarColor = ContextCompat.getColor(requireContext(), R.color.sky_100)
                background.setBackgroundResource(R.drawable.bg_gradient_blue_sky)
            } else {
                statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_500)
                background.setBackgroundResource(R.drawable.bg_gradient_blue_navy)
            }
        }
    }

    fun setBaseStatusBar() {
        requireActivity().window.apply {
            WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars =
                true
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        }
    }

    abstract fun observeData()

    override fun onDestroy() {
        if (fetchJob.isActive) {
            fetchJob.cancel()
        }
        super.onDestroy()
    }

}