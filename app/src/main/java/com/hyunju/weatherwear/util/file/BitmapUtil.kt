package com.hyunju.weatherwear.util.file

import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.hyunju.weatherwear.WeatherWearApplication.Companion.appContext

object BitmapUtil {

    private const val MAX_RESOLUTION = 1280

    fun setImageBitmap(uri: Uri): Bitmap? {
        val bitmap = Glide.with(appContext!!).asBitmap().load(uri).submit().get()
        return resizeBitmapImage(bitmap)
    }

    private fun resizeBitmapImage(source: Bitmap): Bitmap? {
        val width = source.width
        val height = source.height
        var newWidth = width
        var newHeight = height
        val rate: Float
        if (width > height) {
            if (MAX_RESOLUTION < width) {
                rate = MAX_RESOLUTION / width.toFloat()
                newHeight = (height * rate).toInt()
                newWidth = MAX_RESOLUTION
            }
        } else {
            if (MAX_RESOLUTION < height) {
                rate = MAX_RESOLUTION / height.toFloat()
                newWidth = (width * rate).toInt()
                newHeight = MAX_RESOLUTION
            }
        }
        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true)
    }

}