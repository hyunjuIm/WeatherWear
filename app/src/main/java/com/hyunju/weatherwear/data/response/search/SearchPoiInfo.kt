package com.hyunju.weatherwear.data.response.search

import com.hyunju.weatherwear.data.response.search.Pois

data class SearchPoiInfo(
    val totalCount: String,
    val count: String,
    val page: String,
    val pois: Pois
)
