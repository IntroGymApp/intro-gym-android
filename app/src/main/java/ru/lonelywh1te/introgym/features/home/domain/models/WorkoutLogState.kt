package ru.lonelywh1te.introgym.features.home.domain.models

sealed class WorkoutLogState {
    data object NotStarted : WorkoutLogState()
    data object InProgress : WorkoutLogState()
    data object Finished : WorkoutLogState()

    companion object {
        fun get(workoutLog: WorkoutLog): WorkoutLogState {
            return when {
                workoutLog.startDateTime == null -> NotStarted
                workoutLog.endDateTime == null -> InProgress
                else -> Finished
            }
        }
    }
}