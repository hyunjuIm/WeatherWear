package com.hyunju.weatherwear.extension

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

// drawable 메모리 캐시에서 로드 되었는지 여부와
// drawable 이 대상에 넣을 첫 번째 이미지인지 여부에 따라 달라지는 새 항목을 생성하는 팩토리 클래스
private val factory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

// ImageView 의 캐시를 clear 한다
fun ImageView.clear() = Glide.with(context).clear(this)

fun ImageView.load(url: String, corner: Float = 0f) {
    Glide.with(this)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .transform(CenterCrop(), RoundedCorners(corner.fromDpToPx()))
        .timeout(5000)
        .into(this)
}

fun ImageView.load(url: String) {
    Glide.with(this)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .timeout(5000)
        .into(this)
}

fun ImageView.load(drawable: Int) {
    val image = ContextCompat.getDrawable(context, drawable)
    Glide.with(this)
        .load(image)
        .transition(DrawableTransitionOptions.withCrossFade())
        .timeout(5000)
        .into(this)
}

fun ImageView.load(bitmap: Bitmap) {
    Glide.with(this)
        .asBitmap()
        .load(bitmap)
        .transition(BitmapTransitionOptions.withCrossFade())
        .timeout(5000)
        .into(this)
}

fun ImageView.load(bitmap: Bitmap, corner: Float = 0f) {
    Glide.with(this)
        .asBitmap()
        .load(bitmap)
        .transition(BitmapTransitionOptions.withCrossFade())
        .transform(CenterCrop(), RoundedCorners(corner.fromDpToPx()))
        .timeout(5000)
        .into(this)
}

@GlideModule
class AppGlideModule : AppGlideModule()