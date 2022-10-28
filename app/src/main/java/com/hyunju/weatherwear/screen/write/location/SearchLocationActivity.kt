package com.hyunju.weatherwear.screen.write.location

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.hyunju.weatherwear.databinding.ActivitySearchLocationBinding
import com.hyunju.weatherwear.screen.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchLocationActivity :
    BaseActivity<SearchLocationViewModel, ActivitySearchLocationBinding>() {

    companion object {
        const val LOCATION_KEY = "location"

        fun newIntent(context: Context) = Intent(context, SearchLocationActivity::class.java)
    }

    override val viewModel by viewModels<SearchLocationViewModel>()

    override fun getViewBinding() = ActivitySearchLocationBinding.inflate(layoutInflater)

    private val adapter by lazy {
        SearchLocationAdapter(clickItem = { location ->
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(LOCATION_KEY, location)
            })
            finish()
        })
    }

    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }

        locationRecyclerView.adapter = adapter

        searchEditText.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                viewModel.searchLocation(searchEditText.text.toString())

                return@setOnKeyListener true
            }
            false
        }

    }

    override fun observeData() = viewModel.searchLocationListLiveData.observe(this) {
        it?.let {
            adapter.submitList(it.toMutableList())

            binding.emptyResultTextView.isGone = true
            binding.locationRecyclerView.isVisible = true
        } ?: kotlin.run {
            adapter.submitList(mutableListOf())

            binding.emptyResultTextView.isVisible = true
            binding.locationRecyclerView.isGone = true
            binding.searchEditText.setText("")
        }
    }
}