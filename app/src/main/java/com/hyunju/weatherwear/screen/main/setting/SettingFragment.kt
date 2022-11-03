package com.hyunju.weatherwear.screen.main.setting

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.fragment.app.viewModels
import com.hyunju.weatherwear.databinding.FragmentSettingBinding
import com.hyunju.weatherwear.screen.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<SettingViewModel, FragmentSettingBinding>() {

    companion object {
        fun newInstance() = SettingFragment()

        const val TAG = "SettingFragment"
    }

    override val viewModel by viewModels<SettingViewModel>()

    override fun getViewBinding() = FragmentSettingBinding.inflate(layoutInflater)

    override fun initViews() = with(binding) {
        pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateAgreeNotification(isChecked)
        }
    }

    override fun observeData() = viewModel.settingLiveData.observe(this) {
        when (it) {
            SettingViewModel.YET -> showPushCheckDialog()
            SettingViewModel.YES -> binding.pushSwitch.isChecked = true
            SettingViewModel.NO -> binding.pushSwitch.isChecked = false
        }
    }

    private fun showPushCheckDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("웨더웨어(WeatherWear)에서 알림을 보내고자 합니다.")
            .setMessage("해당 기기로 날씨 및 옷차림에 관련된 정보를 푸시 알림으로 보내드리겠습니다.\n앱 푸시에 수신 동의하시겠습니까?")
            .setPositiveButton("허용") { _, _ ->
                viewModel.updateAgreeNotification(true)
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                }
            }
            .setNegativeButton("허용 안 함") { _, _ ->
                viewModel.updateAgreeNotification(false)
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                }
            }
            .create()
            .show()
    }

}