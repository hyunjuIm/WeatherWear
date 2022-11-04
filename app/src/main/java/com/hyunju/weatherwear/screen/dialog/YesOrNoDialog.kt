package com.hyunju.weatherwear.screen.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.hyunju.weatherwear.databinding.DialogYesOrNoBinding

class YesOrNoDialog(
    private val yesOrNoDialogInterface: YesOrNoDialogInterface,
    private val title: String?,
    private val message: String,
    private val positiveButton: String,
    private val negativeButton: String
) : DialogFragment() {

    private var _binding: DialogYesOrNoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogYesOrNoBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        title?.let {
            binding.titleTextView.isVisible = true
            binding.titleTextView.text = title
        }

        binding.messageTextView.text = message

        binding.yesButton.text = positiveButton
        binding.yesButton.setOnClickListener {
            this.yesOrNoDialogInterface.onYesButtonClick(true)
            dismiss()
        }

        binding.noButton.text = negativeButton
        binding.noButton.setOnClickListener {
            this.yesOrNoDialogInterface.onYesButtonClick(true)
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface YesOrNoDialogInterface {
    fun onYesButtonClick(value: Boolean)
}