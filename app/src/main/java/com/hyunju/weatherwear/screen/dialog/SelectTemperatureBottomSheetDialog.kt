package com.hyunju.weatherwear.screen.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.BottomSheetSelectTemperatureBinding
import com.hyunju.weatherwear.util.weather.Temperatures
import kotlin.math.max

class SelectTemperatureBottomSheetDialog(
    val itemClick: (String, Temperatures) -> Unit
) : BottomSheetDialogFragment() {

    companion object {
        const val MAX = "max"
        const val MIN = "min"
    }

    private var _binding: BottomSheetSelectTemperatureBinding? = null
    private val binding get() = _binding!!

    private var selectStandard: String? = MAX
    private var selectTemperature: Temperatures? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetSelectTemperatureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme() = R.style.Widget_WeatherWear_BottomSheetDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.radioGroup.setOnCheckedChangeListener { _, id ->
            selectTemperature = when (id) {
                R.id.temperatureHigh -> Temperatures.TEMPERATURE_HIGH
                R.id.temperature23 -> Temperatures.TEMPERATURE_23
                R.id.temperature20 -> Temperatures.TEMPERATURE_20
                R.id.temperature17 -> Temperatures.TEMPERATURE_17
                R.id.temperature12 -> Temperatures.TEMPERATURE_12
                R.id.temperature9 -> Temperatures.TEMPERATURE_9
                R.id.temperature5 -> Temperatures.TEMPERATURE_5
                R.id.temperatureLow -> Temperatures.TEMPERATURE_LOW
                else -> null
            }

            toggleEnableSearchButton()
        }

        binding.standardRadioGroup.setOnCheckedChangeListener { _, id ->
            selectStandard = when (id) {
                R.id.maxTemperatureButton -> MAX
                R.id.minTemperatureButton -> MIN
                else -> null
            }

            toggleEnableSearchButton()
        }

        binding.searchButton.setOnClickListener {
            selectTemperature?.let {
                itemClick(selectStandard!!, it)
                dismiss()
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "옵션을 선택해주세요!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleEnableSearchButton() {
        binding.searchButton.isEnabled = (selectStandard != null) && (selectTemperature != null)
    }
}