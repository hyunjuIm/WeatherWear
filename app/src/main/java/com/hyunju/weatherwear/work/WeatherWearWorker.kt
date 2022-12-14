package com.hyunju.weatherwear.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.LifecycleService
import androidx.work.*
import com.hyunju.weatherwear.R
import com.hyunju.weatherwear.data.entity.LocationLatLngEntity
import com.hyunju.weatherwear.data.entity.SearchResultEntity
import com.hyunju.weatherwear.data.entity.WeatherEntity
import com.hyunju.weatherwear.data.repository.map.MapRepository
import com.hyunju.weatherwear.data.repository.weather.WeatherRepository
import com.hyunju.weatherwear.data.response.weather.Items
import com.hyunju.weatherwear.screen.main.MainActivity
import com.hyunju.weatherwear.util.clothes.pickClothes
import com.hyunju.weatherwear.util.conventer.LatXLngY
import com.hyunju.weatherwear.util.conventer.TO_GRID
import com.hyunju.weatherwear.util.conventer.convertGridGPS
import com.hyunju.weatherwear.util.date.getTodayDate
import com.hyunju.weatherwear.util.date.getYesterdayDate
import com.hyunju.weatherwear.util.weather.getCommentWeather
import com.hyunju.weatherwear.widget.WeatherWearAppWidgetProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*

@HiltWorker
class WeatherWearWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val workerParams: WorkerParameters,
    private val mapRepository: MapRepository,
    private val weatherRepository: WeatherRepository
) : CoroutineWorker(context, workerParams) {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }
    private val ioDispatchers = Dispatchers.IO + coroutineExceptionHandler

    companion object {
        private const val CHANNEL_NAME = "Daily WeatherWear Updates"
        private const val CHANNEL_ID = "ChannelId"

        const val NOTIFICATION = "notification"
        private const val NOTIFICATION_ID = 101

        const val PUSH_ALERT = "pushAlert"
        const val YET = "yet"
        const val ON = "on"
        const val OFF = "off"

        const val WORK_NAME = "weather_wear_worker"
    }

    private var notificationTitle = context.getString(R.string.app_full_name)
    private var notificationContent = "???(??????????)?????????.*????????? \n?????? ?????? ????????? ????????? ???????????? ??????????????????!"

    override suspend fun doWork(): Result = coroutineScope {
        try {
            when (inputData.getString(PUSH_ALERT) ?: "") {
                ON -> {
                    withContext(ioDispatchers) {
                        getWeatherInformation()
                    }

                    createNotificationChannelIfNeeded()
                    NotificationManagerCompat
                        .from(context)
                        .notify(NOTIFICATION_ID, createNotification())
                }
                else -> Unit
            }

            Result.success()

        } catch (e: Exception) {
            Result.failure()
        }
    }

    // ?????? ?????? ????????????
    private suspend fun getWeatherInformation() {
        val location = mapRepository.getLocationDataFromDevice()
        if (location.isEmpty()) return

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
                notificationTitle = "?????? ?????? ${it.TMX}??/ ?????? ?????? ${it.TMN}??/ ${it.toWeatherType().text}"
                notificationContent = getCommentWeather(it)[0] +
                        "\n?????? ????????? ???(??????????)?????????.*????????? ${pickClothes(it.TMX).map { clothes -> clothes.text }}"
            }
        }
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

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.weather_sun)
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(notificationContent)
            )
            .build()
    }

}