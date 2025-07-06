package ru.lonelywh1te.introgym.features.workout.presentation.workoutTrackingService.impl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutLogRepository
import ru.lonelywh1te.introgym.features.workout.presentation.workoutTrackingService.WorkoutServiceController
import java.time.LocalDateTime

class WorkoutServiceControllerImpl(
    private val context: Context,
    private val workoutLogRepository: WorkoutLogRepository,
): WorkoutServiceController {

    private var serviceConnection: ServiceConnection? = null

    override fun start(startDateTime: LocalDateTime?) {
        context.startService(
            Intent(context, WorkoutService::class.java).apply {
                action = WorkoutService.ACTION_START
                startDateTime?.let { putExtra(WorkoutService.START_LOCAL_DATE_TIME_EXTRA, startDateTime.toString()) }
            }
        )
    }

    override fun stop() {
        context.startService(
            Intent(context, WorkoutService::class.java).apply {
                action = WorkoutService.ACTION_STOP
            }
        )

        unbind()
    }

    override fun bind(
        onServiceConnectedListener: ((name: ComponentName?, service: IBinder?) -> Unit)?,
        onServiceDisconnectedListener: ((name: ComponentName?) -> Unit)?
    ) {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                onServiceConnectedListener?.invoke(p0, p1)
            }
            override fun onServiceDisconnected(p0: ComponentName?){
                onServiceDisconnectedListener?.invoke(p0)
            }
        }

        context.bindService(
            Intent(context, WorkoutService::class.java), serviceConnection!!, Context.BIND_AUTO_CREATE
        )
    }

    override fun unbind() {
        serviceConnection?.let { context.unbindService(it) }
        serviceConnection = null
    }

    override suspend fun restore() {
        workoutLogRepository.getWorkoutLogWithStartDateTime()
            .onSuccess {
                if (it != null) start(it.startDateTime)
            }
    }
}