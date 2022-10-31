package com.hyunju.weatherwear.extension

import android.graphics.Canvas
import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.removeEdgeEffect() = object : RecyclerView.EdgeEffectFactory() {
    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return object : EdgeEffect(view.context) {
            override fun draw(canvas: Canvas?): Boolean {
                return false
            }
        }
    }
}