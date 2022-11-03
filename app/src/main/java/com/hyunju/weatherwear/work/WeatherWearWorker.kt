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
import com.hyunju.weatherwear.util.weather.getCommentWeather
import com.hyunju.weatherwear.util.weather.getWeatherType
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
        private const val CHANNEL_DESCRIPTION = "지금 바로 오늘의 날씨와 옷차림을 확인해보세요!"
        private const val CHANNEL_ID = "ChannelId"

        private const val NOTIFICATION_ID = 101
    }

    private var notificationTitle = ""
    private var notificationDescription = "지금 바로 오늘의 날씨와 옷차림을 확인해보세요!"

    override suspend fun doWork(): Result = coroutineScope {
        try {
            withContext(ioDispatchers) {
                getWeatherInformation()
            }

            createNotificationChannelIfNeeded()

            NotificationManagerCompat
                .from(context)
                .notify(NOTIFICATION_ID, createNotification())

            Result.success()

        } catch (e: Exception) {
            Result.failure()
        }
    }

    // 날씨 정보 받아오기
    private suspend fun getWeatherInformation() {
        val location = mapRepository.getLocationDataFromDevice()
        if (location.isEmpty()) return

        val date = getTodayDate()
        val searchResultEntity = SearchResultEntity(
            name = location.first().name,
            locationLatLng = LocationLatLngEntity(
                location.first().latitude,
                location.first().longitude
            )
        )

        val grid = convertGridGPS(TO_GRID, searchResultEntity.locationLatLng)

        val hasWeatherData = weatherRepository.getWeatherItemsFromDevice().filter {
            date == it.baseDate && grid.x.toInt() == it.nx && grid.y.toInt() == it.ny
        }

        val weatherEntityList = hasWeatherData.ifEmpty { getWeatherDataFromAPI(grid, date) }

        Items(item = weatherEntityList?.map { it.toItem() }).toEntity(date)?.let {
            notificationTitle = "최고 기온 ${it.TMX}°/ 최저 기온 ${it.TMN}°/ ${getWeatherType(it).text}"
            notificationDescription = getCommentWeather(it) +
                    "\n추천 옷차림 ଘ(੭˃ᴗ˂)━☆ﾟ.*･｡ﾟ ${pickClothes(it.TMX).map { clothes -> clothes.text }}"
        }
    }

    private suspend fun getWeatherDataFromAPI(grid: LatXLngY, date: String): List<WeatherEntity>? {
        val weatherEntityList: List<WeatherEntity>?

        val responseData = weatherRepository.getWeather(
            dataType = "JSON",
            numOfRows = 500,
            pageNo = 1,
            baseDate = date,
            baseTime = "0200",
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
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION

            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
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
            .setSmallIcon(R.drawable.ic_sunny_line)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDescription)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(notificationDescription)
            )
            .build()
    }

}