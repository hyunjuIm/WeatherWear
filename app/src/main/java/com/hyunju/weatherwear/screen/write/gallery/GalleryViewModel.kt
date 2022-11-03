package com.hyunju.weatherwear.screen.write.gallery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.WeatherWearApplication.Companion.appContext
import com.hyunju.weatherwear.data.repository.gallery.GalleryRepository
import com.hyunju.weatherwear.model.GalleryModel
import com.hyunju.weatherwear.screen.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor() : BaseViewModel() {

    private val galleryPhotoRepository by lazy { GalleryRepository(appContext!!) }

    private lateinit var photoList: MutableList<GalleryModel>

    val galleryStateLiveData = MutableLiveData<GalleryState>(GalleryState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch(exceptionHandler) {
        galleryStateLiveData.value = GalleryState.Loading

        photoList = galleryPhotoRepository.getAllPhotos()
        galleryStateLiveData.value = GalleryState.Success(
            photoList = photoList
        )
    }

    // 사진 선택 (1장만)
    fun selectPhoto(selectGalleryModel: GalleryModel) {
        if (photoList.filter { it.isSelected }.size == 1 &&
            (photoList.first { it.isSelected } != selectGalleryModel)
        ) {
            galleryStateLiveData.value = GalleryState.Error(R.string.guide_select_only_one_photo)
            return
        }

        photoList.find { it == selectGalleryModel }?.let { photo ->
            photoList[photoList.indexOf(photo)] = photo.copy(
                isSelected = photo.isSelected.not()
            )
            galleryStateLiveData.value = GalleryState.Success(
                photoList = photoList
            )
        }
    }

    // 사진 선택 완료
    fun confirmSelectedPhoto() {
        galleryStateLiveData.value = GalleryState.Loading

        val selectPhoto = photoList.filter { it.isSelected }

        if (selectPhoto.isEmpty()) {
            galleryStateLiveData.value = GalleryState.Error(R.string.selected_photo_empty)
            return
        }

        galleryStateLiveData.value = GalleryState.Confirm(
            photo = if (selectPhoto.size == 1) selectPhoto.first() else null
        )
    }

    override fun errorData(message: Int): Job = viewModelScope.launch {
        galleryStateLiveData.value = GalleryState.Loading
        galleryStateLiveData.value = GalleryState.Error(message)
    }

}