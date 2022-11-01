package com.hyunju.weatherwear.screen.main.wear

import android.app.Activity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.FragmentWearBinding
import com.hyunju.weatherwear.extension.fromDpToPx
import com.hyunju.weatherwear.screen.base.BaseFragment
import com.hyunju.weatherwear.screen.dailylook.detail.WeatherWearDetailActivity
import com.hyunju.weatherwear.screen.write.WriteActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WearFragment : BaseFragment<WearViewModel, FragmentWearBinding>() {

    companion object {
        fun newInstance() = WearFragment()

        const val TAG = "WearFragment"
    }

    override val viewModel by viewModels<WearViewModel>()

    override fun getViewBinding() = FragmentWearBinding.inflate(layoutInflater)

    private val detailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getBooleanExtra(WeatherWearDetailActivity.DELETE_KEY, false)
                    ?.let { delete -> if (delete) viewModel.fetchData() } ?: kotlin.run {
                    Toast.makeText(requireContext(), R.string.request_error, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private val adapter by lazy {
        WearAdapter(clickItem = {
            detailLauncher.launch(
                WeatherWearDetailActivity.newIntent(requireContext(), it)
            )
        })
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(spanCount = 2, spacing = 16f.fromDpToPx())
        )

        refresh.setOnRefreshListener { viewModel.fetchData() }

        addButton.setOnClickListener {
            startActivity(
                WriteActivity.newIntent(requireContext())
            )
        }
    }

    override fun observeData() = viewModel.wearStateLiveDate.observe(this) {
        when (it) {
            is WearState.Loading -> handleLoadingState()
            is WearState.Success -> handleSuccessState(it)
            is WearState.Error -> handleErrorState(it)
            else -> Unit
        }
    }

    private fun handleLoadingState() = with(binding) {
        loadingView.isVisible = true
        recyclerView.isGone = true
    }

    private fun handleSuccessState(state: WearState.Success) = with(binding) {
        loadingView.isGone = true
        refresh.isRefreshing = false
        recyclerView.isVisible = true

        adapter.submitList(state.weatherWearList.toMutableList())
    }

    private fun handleErrorState(state: WearState.Error) = with(binding) {
        loadingView.isGone = true
        refresh.isRefreshing = false

        Toast.makeText(requireContext(), state.messageId, Toast.LENGTH_SHORT).show()
    }

}