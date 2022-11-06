package com.hyunju.weatherwear.screen.main.setting.backup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.preference.AppPreferenceManager
import com.hyunju.weatherwear.screen.base.BaseViewModel
import com.hyunju.weatherwear.util.event.UpdateEvent
import com.hyunju.weatherwear.util.event.UpdateEventBus
import com.hyunju.weatherwear.util.file.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.*
import java.nio.channels.FileChannel
import javax.inject.Inject

@HiltViewModel
class BackUpViewModel @Inject constructor(
    private val appPreferenceManager: AppPreferenceManager
) : BaseViewModel() {

    private val storage by lazy { FirebaseStorage.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    val backUpStateLiveData = MutableLiveData<BackUpState>(BackUpState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch(exceptionHandler) {
        backUpStateLiveData.value = BackUpState.Loading
        appPreferenceManager.getIdToken()?.let {
            backUpStateLiveData.value = BackUpState.Login(it)
        } ?: kotlin.run {
            backUpStateLiveData.value = BackUpState.Success.NotRegistered
        }
    }

    fun saveToken(idToken: String) = viewModelScope.launch(exceptionHandler) {
        withContext(Dispatchers.IO) {
            appPreferenceManager.putIdToken(idToken)
            fetchData()
        }
    }

    fun setUserInfo(firebaseUser: FirebaseUser?) = viewModelScope.launch(exceptionHandler) {
        firebaseUser?.let { user ->
            backUpStateLiveData.value = BackUpState.Success.Registered(
                name = user.displayName ?: "익명",
                email = user.email ?: "이메일 정보 없음",
                profileImageUri = user.photoUrl
            )
        } ?: kotlin.run {
            backUpStateLiveData.value = BackUpState.Success.NotRegistered
        }
    }

    fun signOut() = viewModelScope.launch(exceptionHandler) {
        withContext(Dispatchers.IO) {
            appPreferenceManager.removeIdToken()
        }
        fetchData()
    }

    fun backUpRoomData() = viewModelScope.launch(exceptionHandler) {
        backUpStateLiveData.value = BackUpState.Loading

        try {
            BackUpUtil.createBackupFileList()?.let { backUpFileList ->
                backUpFileList.map {
                    storage
                        .reference.child("room/" + auth.uid).child(it.name)
                        .putStream(FileInputStream(it.file))
                        .addOnSuccessListener {
                        }.addOnFailureListener {
                            throw Exception()
                        }
                }
            }
        } catch (e: Exception) {
            backUpStateLiveData.value = BackUpState.Error(R.string.fail_data_back_up)
            return@launch
        }

        backUpStateLiveData.value = BackUpState.Success.BackUp
    }

    fun restoreRoomData() = viewModelScope.launch(exceptionHandler) {
        backUpStateLiveData.value = BackUpState.Loading

        val fileNameList = listOf(
            BackUpUtil.BACK_UP_DB_NAME,
            BackUpUtil.BACK_UP_DB_SHM_NAME,
            BackUpUtil.BACK_UP_DB_WAL_NAME
        )

        try {
            fileNameList.map { name ->
                val islandRef = storage.reference.child("room/" + auth.uid).child(name)
                val localFile = File.createTempFile("filename", ".tmp")

                islandRef.getFile(localFile).addOnSuccessListener {
                    val src: FileChannel = FileInputStream(localFile).channel
                    val dst: FileChannel =
                        FileOutputStream(BackUpUtil.readDatabaseFile(name)).channel
                    dst.transferFrom(src, 0, src.size())
                }.addOnFailureListener {
                    throw Exception()
                }

                localFile.deleteOnExit()
            }
        } catch (e: Exception) {
            backUpStateLiveData.value = BackUpState.Error(R.string.fail_data_restore)
            return@launch
        }

        UpdateEventBus.invokeEvent(UpdateEvent.Updated)
        backUpStateLiveData.value = BackUpState.Success.Restore
    }
}