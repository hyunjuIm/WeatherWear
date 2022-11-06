package com.hyunju.weatherwear.screen.main.setting

import androidx.fragment.app.viewModels
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.FragmentSettingBinding
import com.hyunju.weatherwear.screen.base.BaseFragment
import com.hyunju.weatherwear.screen.dialog.YesOrNoDialog
import com.hyunju.weatherwear.screen.dialog.YesOrNoDialogInterface
import com.hyunju.weatherwear.screen.main.setting.backup.BackUpActivity
import com.hyunju.weatherwear.work.WeatherWearWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>(),
    YesOrNoDialogInterface {

    companion object {
        fun newInstance() = SettingFragment()

        const val TAG = "SettingFragment"
    }

    override val viewModel by viewModels<SettingViewModel>()

    override fun getViewBinding() = FragmentSettingBinding.inflate(layoutInflater)

    override fun initViews() = with(binding) {
        backUpButton.setOnClickListener {
            startActivity(
                BackUpActivity.newIntent(requireContext())
            )
        }
        pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateAgreeNotification(isChecked)
        }
    }

    override fun observeData() = viewModel.settingLiveData.observe(this) {
        when (it) {
            WeatherWearWorker.YET -> showPushCheckDialog()
            WeatherWearWorker.ON -> binding.pushSwitch.isChecked = true
            WeatherWearWorker.OFF -> binding.pushSwitch.isChecked = false
        }
    }

    private fun showPushCheckDialog() {
        activity?.supportFragmentManager?.let {
            YesOrNoDialog(
                yesOrNoDialogInterface = this,
                title = getString(R.string.push_title),
                message = getString(R.string.push_message),
                positiveButton = getString(R.string.push_allow),
                negativeButton = getString(R.string.push_not_allow)
            ).show(it, "YesOrNoDialog")
        }
    }

    override fun onYesButtonClick(value: Boolean, tag: String) {
        viewModel.updateAgreeNotification(value)
    }

}