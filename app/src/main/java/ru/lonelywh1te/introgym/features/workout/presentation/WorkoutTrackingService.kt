package ru.lonelywh1te.introgym.features.workout.presentation

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.utils.DateAndTimeStringFormatUtils
import ru.lonelywh1te.introgym.features.notifications.NotificationChannelManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

class WorkoutTrackingService: Service() {
    private val job = SupervisorJob()
    private val serviceScope = CoroutineScope(job + Dispatchers.IO)
    private val binder = LocalBinder()

    private var onTimeChangedListener: ((LocalTime) -> Unit)? = null
    private var time: LocalTime = LocalTime.of(0, 0, 0)
        set(value) {
            field = value
            _timeStateFlow.value = value

            onTimeChangedListener?.invoke(value)
        }

    private val _timeStateFlow = MutableStateFlow(time)
    val timeStateFlow = _timeStateFlow.asStateFlow()

    private var timerJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getStringExtra(START_LOCAL_DATE_TIME_EXTRA)?.let {
            restoreStartTime(LocalDateTime.parse(it))
        }

        intent?.action.let {
            when(it) {
                ACTION_START -> start()
                ACTION_STOP -> stop()
                else -> throw IllegalArgumentException("Unknown WorkoutTrackingService action. Use ACTION_START or ACTION_STOP instead")
            }
        }

        return START_REDELIVER_INTENT
    }

    fun setOnTimeChangedListener(listener: ((LocalTime) -> Unit)?) {
        onTimeChangedListener = listener
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun start() {
        if (timerJob != null) return

        startForeground(SERVICE_ID, createNotification())
        timerJob = serviceScope.launch {
            while (true) {
                delay(1000)
                time = time.plusSeconds(1)

                updateNotification()
            }
        }

        Log.d("WorkoutTrackingService", "SERVICE STARTED")
    }

    private fun stop() {
        Log.d("WorkoutTrackingService", "SERVICE STOPPED")

        timerJob?.cancel().also { timerJob = null }
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun restoreStartTime(startDateTime: LocalDateTime) {
        val duration = Duration.between(LocalDateTime.now(), startDateTime).abs()

        val seconds = duration.seconds
        val hours = (seconds / 3600).toInt()
        val minutes = ((seconds % 3600) / 60).toInt()
        val secs = (seconds % 60).toInt()

        time = LocalTime.of(hours, minutes, secs)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d("WorkoutTrackingService", "SERVICE DESTROYED")
    }

    private fun createNotification(): Notification {
        return NotificationCompat
            .Builder(this, NotificationChannelManager.WORKOUT_TRACKING_CHANNEL_ID)
            .setContentTitle("Активная тренировка")
            .setContentText("Общее время: " + time.format(DateAndTimeStringFormatUtils.fullTimeFormatter))
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(SERVICE_ID, notification)
    }

    inner class LocalBinder: Binder() {
        fun getService(): WorkoutTrackingService = this@WorkoutTrackingService
    }

    companion object {
        private const val SERVICE_ID = 1

        const val ACTION_START = "start"
        const val ACTION_STOP = "stop"
        const val START_LOCAL_DATE_TIME_EXTRA = "start_time"
    }
}