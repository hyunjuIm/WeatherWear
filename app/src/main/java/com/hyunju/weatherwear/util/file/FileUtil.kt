package com.hyunju.weatherwear.util.file

import java.text.SimpleDateFormat

fun newJpgFileName(): String {
    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
    val filename = sdf.format(System.currentTimeMillis())
    return "${filename}.jpg"
}