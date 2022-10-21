package com.hyunju.weatherwear.di

import androidx.viewbinding.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hyunju.weatherwear.data.network.WeatherApiService
import com.hyunju.weatherwear.data.url.Url.WEATHER_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(WEATHER_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(
        gson: Gson
    ): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun buildOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

}