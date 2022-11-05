package com.hyunju.weatherwear.screen.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.BottomSheetSelectPhotoOptionBinding

class SelectPhotoOptionBottomSheetDialog(
    val itemClick: (PhotoOption) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSelectPhotoOptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSelectPhotoOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme() = R.style.Widget_WeatherWear_BottomSheetDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.optionCameraView.setOnClickListener {
            itemClick(PhotoOption.CAMARA)
            dismiss()
        }

        binding.optionGalleryView.setOnClickListener {
            itemClick(PhotoOption.GALLERY)
            dismiss()
        }

    }

}

enum class PhotoOption{
    CAMARA, GALLERY
}