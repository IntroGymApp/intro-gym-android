package ru.lonelywh1te.introgym.features.workout.presentation.workoutTrackingService

import android.content.ComponentName
import android.os.IBinder
import java.time.LocalDateTime

interface WorkoutServiceController {

    fun start(startDateTime: LocalDateTime? = null)

    fun stop()

    fun bind(
        onServiceConnectedListener: ((name: ComponentName?, service: IBinder?) -> Unit)? = null,
        onServiceDisconnectedListener: ((name: ComponentName?) -> Unit)? = null,
    )

    fun unbind()

    suspend fun restore()
}