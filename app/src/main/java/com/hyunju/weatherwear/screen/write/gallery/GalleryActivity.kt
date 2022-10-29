package com.hyunju.weatherwear.screen.write.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ActivityGalleryBinding
import com.hyunju.weatherwear.screen.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class GalleryActivity : BaseActivity<GalleryViewModel, ActivityGalleryBinding>() {

    companion object {
        const val URI_KEY = "uri"

        fun newIntent(context: Context) = Intent(context, GalleryActivity::class.java)
    }

    override val viewModel by viewModels<GalleryViewModel>()

    override fun getViewBinding() = ActivityGalleryBinding.inflate(layoutInflater)

    private val adapter by lazy {
        GalleryAdapter(clickItem = {
            viewModel.selectPhoto(it)
        })
    }

    override fun initViews() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            GridDividerDecoration(this@GalleryActivity, R.drawable.bg_rectangle_white)
        )

        binding.confirmButton.setOnClickListener {
            viewModel.confirmSelectedPhoto()
        }
    }

    override fun observeData() = viewModel.galleryStateLiveData.observe(this) {
        when (it) {
            is GalleryState.Loading -> handleLoadingState()
            is GalleryState.Success -> handleSuccessState(it)
            is GalleryState.Confirm -> handleConfirmState(it)
            is GalleryState.Error -> handleErrorState(it)
            else -> Unit
        }
    }

    private fun handleLoadingState() = with(binding) {
        progressBar.isVisible = true
        recyclerView.isGone = true
    }

    private fun handleSuccessState(state: GalleryState.Success) = with(binding) {
        progressBar.isGone = true
        recyclerView.isVisible = true

        adapter.submitList(state.photoList.toMutableList())
    }

    private fun handleConfirmState(state: GalleryState.Confirm) {
        state.photo?.let {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(URI_KEY,it.uri)
            })
        }

        finish()
    }

    private fun handleErrorState(state: GalleryState.Error) = with(binding) {
        progressBar.isGone = true
        Toast.makeText(this@GalleryActivity, state.messageId, Toast.LENGTH_SHORT).show()
    }

}