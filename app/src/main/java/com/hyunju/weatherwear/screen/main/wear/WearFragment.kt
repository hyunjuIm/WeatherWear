package com.hyunju.weatherwear.screen.main.wear

import android.app.Activity
import android.app.AlertDialog
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
import com.hyunju.weatherwear.screen.main.wear.search.SearchWeatherWearActivity
import com.hyunju.weatherwear.screen.write.WriteActivity
import com.hyunju.weatherwear.util.view.GridSpacingItemDecoration
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) setBaseStatusBar()
    }

    override fun initViews() = with(binding) {
        setBaseStatusBar()

        titleTextView.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        searchButton.setOnClickListener { showSearchDialog() }

        recyclerView.adapter = adapter
        recyclerView.itemAnimator = null
        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(spanCount = 2, spacing = 16f.fromDpToPx())
        )

        // 스크롤 업 대신에 리프레쉬 이벤트가 트리거 되는걸 방지하기 위해서
        recyclerView.viewTreeObserver.addOnScrollChangedListener {
            recyclerView.isEnabled = (recyclerView.scrollY == 0)
        }

        refresh.setOnRefreshListener {
            viewModel.fetchData()
        }

        addButton.setOnClickListener {
            startActivity(
                WriteActivity.newIntent(requireContext())
            )
        }
    }

    private fun showSearchDialog() {
        val options = arrayOf<CharSequence>(
            getString(R.string.search_date),
            getString(R.string.search_temperature)
        )

        AlertDialog.Builder(requireContext()).setItems(options) { _, index ->
            val option = when (options[index]) {
                getString(R.string.search_date) -> SearchWeatherWearActivity.DATE
                getString(R.string.search_temperature) -> SearchWeatherWearActivity.TEMPERATURES
                else -> ""
            }
            startActivity(
                SearchWeatherWearActivity.newIntent(requireContext(), option)
            )
        }.setCancelable(true)
            .create()
            .show()
    }

    override fun observeData() {
        viewModel.wearStateLiveDate.observe(this) {
            when (it) {
                is WearState.Loading -> handleLoadingState()
                is WearState.Success -> handleSuccessState(it)
                is WearState.Error -> handleErrorState(it)
                else -> Unit
            }
        }

        viewModel.updateUIState.observe(this) {
            if (it) viewModel.fetchData()
        }
    }

    private fun handleLoadingState() = with(binding) {
        loadingView.root.isVisible = true
        recyclerView.isGone = true
    }

    private fun handleSuccessState(state: WearState.Success) = with(binding) {
        loadingView.root.isGone = true
        refresh.isRefreshing = false
        recyclerView.isVisible = true

        adapter.submitList(state.weatherWearList.toMutableList())
    }

    private fun handleErrorState(state: WearState.Error) = with(binding) {
        loadingView.root.isGone = true
        refresh.isRefreshing = false

        Toast.makeText(requireContext(), state.messageId, Toast.LENGTH_SHORT).show()
    }

}