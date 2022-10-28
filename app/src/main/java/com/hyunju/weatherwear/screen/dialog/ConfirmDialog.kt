package com.hyunju.weatherwear.screen.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hyunju.weatherwear.databinding.DialogConfirmBinding

class ConfirmDialog(
    private val confirmDialogInterface: ConfirmDialogInterface,
    private val text: String
) : DialogFragment() {

    private var _binding: DialogConfirmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogConfirmBinding.inflate(inflater, container, false)
        val view = binding.root

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.confirmTextView.text = text

        binding.completeButton.setOnClickListener {
            this.confirmDialogInterface.onYesButtonClick()
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface ConfirmDialogInterface {
    fun onYesButtonClick()
}