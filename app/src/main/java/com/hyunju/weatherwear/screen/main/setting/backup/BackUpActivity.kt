package com.hyunju.weatherwear.screen.main.setting.backup

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.databinding.ActivityBackUpBinding
import com.hyunju.weatherwear.extension.load
import com.hyunju.weatherwear.screen.base.BaseActivity
import com.hyunju.weatherwear.screen.dialog.ConfirmDialog
import com.hyunju.weatherwear.screen.dialog.ConfirmDialogInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackUpActivity : BaseActivity<BackUpViewModel, ActivityBackUpBinding>(),
    ConfirmDialogInterface {

    companion object {
        fun newIntent(context: Context) = Intent(context, BackUpActivity::class.java)
    }

    override val viewModel by viewModels<BackUpViewModel>()

    override fun getViewBinding() = ActivityBackUpBinding.inflate(layoutInflater)

    override val transitionMode = TransitionMode.VERTICAL

    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private val gsc by lazy { GoogleSignIn.getClient(this, gso) }

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    task.getResult(ApiException::class.java)?.let { account ->
                        viewModel.saveToken(account.idToken ?: throw Exception())
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, R.string.can_not_assigned_permission, Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun initViews() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }

        loginButton.setOnClickListener { signInGoogle() }

        logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            gsc.signOut()

            viewModel.signOut()
        }

        backUpButton.setOnClickListener {
            checkExternalStoragePermission {
                viewModel.backUpRoomData()
            }
        }

        restoreButton.setOnClickListener {
            checkExternalStoragePermission {
                viewModel.restoreRoomData()
            }
        }
    }

    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    override fun observeData() = viewModel.backUpStateLiveData.observe(this) {
        when (it) {
            is BackUpState.Loading -> handleLoadingState()
            is BackUpState.Success -> handleSuccessState(it)
            is BackUpState.Login -> handleLoginState(it)
            is BackUpState.Error -> handleErrorState(it)
            else -> Unit
        }
    }

    private fun handleLoadingState() = with(binding) {
        progressBar.isVisible = true
        loginGroup.isGone = true
    }

    private fun handleSuccessState(state: BackUpState.Success) = with(binding) {
        progressBar.isGone = true

        when (state) {
            is BackUpState.Success.Registered -> {
                handleRegisteredState(state)
            }
            is BackUpState.Success.NotRegistered -> {
                contentGroup.isGone = true
                loginGroup.isVisible = true
            }
            is BackUpState.Success.BackUp -> {
                ConfirmDialog(
                    confirmDialogInterface = this@BackUpActivity,
                    text = getString(R.string.success_data_back_up)
                ).show(supportFragmentManager, "ConfirmDialog")
            }
            is BackUpState.Success.Restore -> {
                ConfirmDialog(
                    confirmDialogInterface = this@BackUpActivity,
                    text = getString(R.string.success_restore)
                ).show(supportFragmentManager, "ConfirmDialog")
            }
        }
    }

    private fun handleRegisteredState(state: BackUpState.Success.Registered) = with(binding) {
        loginGroup.isGone = true
        contentGroup.isVisible = true

        profileImageView.load(state.profileImageUri.toString(), 60f)
        nameTextView.text = state.name
        emailTextView.text = state.email
    }

    private fun handleLoginState(state: BackUpState.Login) {
        binding.progressBar.isVisible = true

        val credential = GoogleAuthProvider.getCredential(state.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                viewModel.setUserInfo(user)
            } else {
                firebaseAuth.signOut()
                viewModel.setUserInfo(null)
            }
        }
    }

    private fun handleErrorState(state: BackUpState.Error) = with(binding) {
        progressBar.isGone = true
        Toast.makeText(this@BackUpActivity, getString(state.messageId), Toast.LENGTH_SHORT).show()
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setMessage("데이터 파일을 백업/복원 하기 위해\n권한이 필요합니다.")
            .setPositiveButton("확인") { _, _ ->
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .create()
            .show()
    }

    private fun checkExternalStoragePermission(uploadAction: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                uploadAction()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showPermissionContextPopup()
            }
            else -> {
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    override fun onYesButtonClick() {

    }

}