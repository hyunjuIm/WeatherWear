package com.hyunju.weatherwear.widget

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.LocationLatLngEntity
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import com.hyunju.weatherwear.data.response.weather.Items
import com.hyunju.weatherwear.screen.main.MainActivity
import com.hyunju.weatherwear.util.clothes.Clothes
import com.hyunju.weatherwear.util.clothes.pickClothes
import com.hyunju.weatherwear.util.conventer.LatXLngY
import com.hyunju.weatherwear.util.conventer.TO_GRID
import com.hyunju.weatherwear.util.conventer.convertGridGPS
import com.hyunju.weatherwear.util.date.*
import com.hyunju.weatherwear.util.weather.Time
import com.hyunju.weatherwear.util.weather.Weather
import com.hyunju.weatherwear.util.weather.getCommentWeather
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

class WeatherWearAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val intent = Intent(context, UpdateWidgetService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    @AndroidEntryPoint
    class UpdateWidgetService : LifecycleService() {

        companion object {
            private const val NOTIFICATION_ID = 201
            private const val WIDGET_REFRESH_CHANNEL_ID = "WIDGET_REFRESH"
            private const val WIDGET_REFRESH_CHANNEL_NAME = "위젯 갱신 채널"
        }

        @Inject
        lateinit var mapRepository: MapRepository

        @Inject
        lateinit var weatherRepository: WeatherRepository

        override fun onCreate() {
            super.onCreate()

            createNotificationChannelIfNeeded()
            startForeground(NOTIFICATION_ID, createNotification())
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            if (checkLocationPermission().not()) {
                return super.onStartCommand(intent, flags, startId)
            }

            lifecycleScope.launch {
                try {
                    getWeatherInformation()?.let { weatherInfo ->
                        val updateViews = initRemoteViews(weatherInfo)
                        updateWidget(updateViews)
                    }
                } catch (e: Exception) {
                    Log.e("으악^^", "$e")
                } finally {
                    stopSelf()
                }
            }

            return super.onStartCommand(intent, flags, startId)
        }

        private fun checkLocationPermission(): Boolean {
            val checkPermissionFineLocation = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val checkPermissionCoarseLocation = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!checkPermissionFineLocation || !checkPermissionCoarseLocation) {
                val updateViews = RemoteViews(packageName, R.layout.widget_provider_layout).apply {
                    setTextViewText(
                        R.id.widgetCommentTextView,
                        getString(R.string.please_setup_your_location_permission)
                    )
                }

                updateWidget(updateViews)
                stopSelf()
            }

            return (checkPermissionFineLocation && checkPermissionCoarseLocation)
        }

        private fun initRemoteViews(weatherInfo: WidgetWeatherModel?): RemoteViews {
            weatherInfo ?: throw Exception()

            val context = this@UpdateWidgetService

            return RemoteViews(packageName, R.layout.widget_provider_layout).apply {
                setTextViewText(R.id.widgetLocationTextView, weatherInfo.location)
                setTextViewText(R.id.widgetDateTextView, weatherInfo.date)
                setTextViewText(
                    R.id.widgetTemperatureTextView,
                    "최저 ${weatherInfo.minTemperatures}° / 최고 ${weatherInfo.maxTemperatures}°"
                )
                setImageViewResource(
                    R.id.widgetWeatherImageView, weatherInfo.weatherType.image
                )
                setTextViewText(
                    R.id.widgetNowTemperatureTextView, "${weatherInfo.nowTemperatures}°"
                )
                setImageViewResource(
                    R.id.widgetFirstClothesIcon, weatherInfo.clothes[0].image
                )
                setTextViewText(
                    R.id.widgetFirstClothesTextView, weatherInfo.clothes[0].text
                )
                setImageViewResource(
                    R.id.widgetSecondClothesIcon, weatherInfo.clothes[1].image
                )
                setTextViewText(
                    R.id.widgetSecondClothesTextView, weatherInfo.clothes[1].text
                )
                setImageViewResource(
                    R.id.widgetThirdClothesIcon, weatherInfo.clothes[2].image
                )
                setTextViewText(
                    R.id.widgetThirdClothesTextView, weatherInfo.clothes[2].text
                )
                setTextViewText(R.id.widgetCommentTextView, weatherInfo.comment)

                if (getNowTime().toInt() in Time.AFTERNOON) {
                    setInt(
                        R.id.widgetView,
                        "setBackgroundResource",
                        R.drawable.bg_rounded_gradient_blue_sky
                    )
                } else {
                    setInt(
                        R.id.widgetView,
                        "setBackgroundResource",
                        R.drawable.bg_rounded_gradient_blue_navy
                    )
                }

                val pendingActivity = Intent(context, MainActivity::class.java).let {
                    it.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

                    PendingIntent.getActivity(context, 0, it, 0)
                }
                setOnClickPendingIntent(R.id.widgetContentView, pendingActivity)


                val intent = Intent(context, UpdateWidgetService::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                }

                val pendingService: PendingIntent? =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        PendingIntent.getForegroundService(
                            context, 0, intent, FLAG_UPDATE_CURRENT
                        )
                    } else {
                        PendingIntent.getService(
                            context, 0, intent, FLAG_UPDATE_CURRENT
                        )
                    }

                setOnClickPendingIntent(R.id.widgetRefreshButton, pendingService)
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            stopForeground(true)
        }

        private fun createNotificationChannelIfNeeded() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)
                    ?.createNotificationChannel(
                        NotificationChannel(
                            WIDGET_REFRESH_CHANNEL_ID,
                            WIDGET_REFRESH_CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                    )
            }
        }

        private fun createNotification() =
            NotificationCompat.Builder(this, WIDGET_REFRESH_CHANNEL_ID)
                .setSmallIcon(R.drawable.weather_sun)
                .setContentText(getString(R.string.update_widget))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

        private fun updateWidget(updateViews: RemoteViews) {
            val widgetProvider = ComponentName(this, WeatherWearAppWidgetProvider::class.java)
            AppWidgetManager.getInstance(this).updateAppWidget(widgetProvider, updateViews)
        }

        // 날씨 정보 받아오기
        private suspend fun getWeatherInformation(): WidgetWeatherModel? {
            val location = mapRepository.getLocationDataFromDevice()
            if (location.isEmpty()) return null

            val searchResultEntity = SearchResultEntity(
                name = location.first().name,
                locationLatLng = LocationLatLngEntity(
                    location.first().latitude,
                    location.first().longitude
                )
            )

            val grid = convertGridGPS(TO_GRID, searchResultEntity.locationLatLng)

            val hasWeatherData = weatherRepository.getWeatherItemsFromDevice().filter {
                getTodayDate() == it.baseDate && grid.x.toInt() == it.nx && grid.y.toInt() == it.ny
            }

            val weatherEntityList = hasWeatherData.ifEmpty { getWeatherDataFromAPI(grid) }

            weatherEntityList?.let { list ->
                Items(item = list.map { it.toItem() }).getDateWeatherModel(getTodayDate())
                    ?.let {
                        return WidgetWeatherModel(
                            location = location.first().name,
                            date = setStringToHangeulDateWithDot(it.date) + getNowFullTime(),
                            maxTemperatures = it.TMX.toString(),
                            minTemperatures = it.TMN.toString(),
                            nowTemperatures = it.TMP.toString(),
                            weatherType = it.toWeatherType(),
                            comment = getCommentWeather(it)[0],
                            clothes = pickClothes(it.TMX)
                        )
                    }
            }

            return null
        }

        private suspend fun getWeatherDataFromAPI(grid: LatXLngY): List<WeatherEntity>? {
            val weatherEntityList: List<WeatherEntity>?

            val responseData = weatherRepository.getWeather(
                dataType = "JSON",
                numOfRows = 2000,
                pageNo = 1,
                baseDate = getYesterdayDate(),
                baseTime = "2300",
                nx = grid.x.toInt(),
                ny = grid.y.toInt()
            ) ?: run { return null }

            weatherEntityList = responseData.item?.map { item ->
                item.let { it?.toEntity() } ?: kotlin.run { return null }
            } ?: kotlin.run { return null }

            return weatherEntityList
        }
    }

    data class WidgetWeatherModel(
        val location: String,
        val date: String,
        val maxTemperatures: String,
        val minTemperatures: String,
        val nowTemperatures: String,
        val weatherType: Weather,
        val comment: String,
        val clothes: List<Clothes>
    )

}