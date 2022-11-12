package com.hyunju.weatherwear.widget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.WeatherWearApplication.Companion.appContext
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
import com.hyunju.weatherwear.util.date.getNowTime
import com.hyunju.weatherwear.util.date.getTodayDate
import com.hyunju.weatherwear.util.date.getYesterdayDate
import com.hyunju.weatherwear.util.date.setStringToHangeulDateWithDot
import com.hyunju.weatherwear.util.weather.Time
import com.hyunju.weatherwear.util.weather.Weather
import com.hyunju.weatherwear.util.weather.getCommentWeather
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.lang.Exception
import javax.inject.Inject

class WeatherWearAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val pendingIntent: PendingIntent =
                Intent(context, MainActivity::class.java).let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }

            val views: RemoteViews =
                RemoteViews(context.packageName, R.layout.widget_provider_layout).apply {
                    setOnClickPendingIntent(R.id.widgetView, pendingIntent)
                }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        val intent = Intent(context, UpdateWidgetService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }

    @AndroidEntryPoint
    class UpdateWidgetService : LifecycleService() {

        private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }
        private val ioDispatchers = Dispatchers.IO + coroutineExceptionHandler
        private val mainDispatchers = Dispatchers.IO + coroutineExceptionHandler

        companion object {
            private const val NOTIFICATION_ID = 12345
            private const val WIDGET_REFRESH_CHANNEL_ID = "WIDGET_REFRESH"
            private const val WIDGET_REFRESH_CHANNEL_NAME = "위젯 갱신 채널"
        }

        @Inject
        lateinit var mapRepository: MapRepository

        @Inject
        lateinit var weatherRepository: WeatherRepository

        override fun onCreate() {
            super.onCreate()

            createChannelIfNeeded()
            startForeground(NOTIFICATION_ID, createNotification())
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            lifecycleScope.launch {
                try {
                    var widgetWeatherModel: WidgetWeatherModel?
                    withContext(ioDispatchers) {
                        widgetWeatherModel = getWeatherInformation()
                    }
                    withContext(mainDispatchers) {
                        widgetWeatherModel?.let {
                            val updateViews =
                                RemoteViews(packageName, R.layout.widget_provider_layout).apply {
                                    setTextViewText(R.id.widgetLocationTextView, it.location)
                                    setTextViewText(R.id.widgetDateTextView, it.date)
                                    setTextViewText(
                                        R.id.widgetTemperatureTextView,
                                        "최저 ${it.minTemperatures}° / 최고 ${it.maxTemperatures}°"
                                    )
                                    setImageViewResource(
                                        R.id.widgetWeatherImageView,
                                        it.weatherType.image
                                    )
                                    setTextViewText(
                                        R.id.widgetNowTemperatureTextView,
                                        "${it.nowTemperatures}°"
                                    )

                                    setImageViewResource(
                                        R.id.widgetFirstClothesIcon, it.clothes[0].image
                                    )
                                    setTextViewText(
                                        R.id.widgetFirstClothesTextView, it.clothes[0].text
                                    )
                                    setImageViewResource(
                                        R.id.widgetSecondClothesIcon, it.clothes[1].image
                                    )
                                    setTextViewText(
                                        R.id.widgetSecondClothesTextView, it.clothes[1].text
                                    )
                                    setImageViewResource(
                                        R.id.widgetThirdClothesIcon, it.clothes[2].image
                                    )
                                    setTextViewText(
                                        R.id.widgetThirdClothesTextView, it.clothes[2].text
                                    )

                                    setTextViewText(R.id.widgetCommentTextView, it.comment)


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
                                }

                            updateWidget(updateViews)
                        }
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                } finally {
                    stopSelf()
                }
            }

            return super.onStartCommand(intent, flags, startId)
        }

        override fun onDestroy() {
            super.onDestroy()
            stopForeground(true)
        }

        private fun createChannelIfNeeded() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)
                    ?.createNotificationChannel(
                        NotificationChannel(
                            WIDGET_REFRESH_CHANNEL_ID,
                            WIDGET_REFRESH_CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_LOW
                        )
                    )
            }
        }

        private fun createNotification() =
            NotificationCompat.Builder(appContext!!, WIDGET_REFRESH_CHANNEL_ID)
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
                Items(item = list.map { it.toItem() }).getDateWeatherModel(getTodayDate())?.let {
                    return WidgetWeatherModel(
                        location = location.first().name,
                        date = setStringToHangeulDateWithDot(it.date),
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