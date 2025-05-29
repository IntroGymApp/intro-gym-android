package ru.lonelywh1te.introgym.features.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class NotificationChannelManager(private val context: Context) {

    fun initChannels() {
        val channels = listOf(
            NotificationChannel(GENERAL_CHANNEL_ID, "Общие уведомления", NotificationManager.IMPORTANCE_DEFAULT),
            NotificationChannel(WORKOUT_TRACKING_CHANNEL_ID, "Таймер тренировки", NotificationManager.IMPORTANCE_HIGH),
            NotificationChannel(REMINDERS_CHANNEL_ID, "Напоминания", NotificationManager.IMPORTANCE_HIGH),
        )

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        channels.forEach { notificationManager.createNotificationChannel(it) }
    }

    companion object {
        const val GENERAL_CHANNEL_ID = "general"
        const val REMINDERS_CHANNEL_ID = "reminders"
        const val WORKOUT_TRACKING_CHANNEL_ID = "workout_tracking"
    }
}