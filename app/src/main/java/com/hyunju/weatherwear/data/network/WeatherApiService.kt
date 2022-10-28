package com.hyunju.weatherwear.data.network

import com.hyunju.weatherwear.data.response.weather.WeatherResponse
import com.hyunju.weatherwear.data.url.Url
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET(Url.GET_VILAGE_FCST)
    suspend fun getWeather(
        @Query("dataType") dataType : String,
        @Query("numOfRows") numOfRows : Int,
        @Query("pageNo") pageNo : Int,
        @Query("base_date") baseDate : String,
        @Query("base_time") baseTime : String,
        @Query("nx") nx : Int,
        @Query("ny") ny : Int
    ) : Response<WeatherResponse>

}