package com.hyunju.weatherwear.data.repository.gallery

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.hyunju.weatherwear.model.GalleryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class GalleryRepository(
    private val context: Context
) {

    suspend fun getAllPhotos(): MutableList<GalleryModel> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        val galleryPhotoList = mutableListOf<GalleryModel>()

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )

                galleryPhotoList.add(GalleryModel(uri = contentUri, isSelected = false))

            }
        }

        galleryPhotoList
    }

}