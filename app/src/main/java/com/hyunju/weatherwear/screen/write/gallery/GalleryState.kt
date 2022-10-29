package com.hyunju.weatherwear.screen.write.gallery

import androidx.annotation.StringRes
import com.hyunju.weatherwear.model.GalleryModel

sealed class GalleryState {

    object Uninitialized : GalleryState()

    object Loading : GalleryState()

    data class Success(
        val photoList: List<GalleryModel>
    ) : GalleryState()

    data class Confirm(
        val photo: GalleryModel?
    ) : GalleryState()

    data class Error(
        @StringRes val messageId: Int
    ) : GalleryState()

}