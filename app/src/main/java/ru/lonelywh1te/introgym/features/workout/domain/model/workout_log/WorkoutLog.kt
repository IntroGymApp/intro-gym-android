package ru.lonelywh1te.introgym.features.workout.domain.model.workout_log

import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLogState.Finished
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLogState.InProgress
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_log.WorkoutLogState.NotStarted
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class WorkoutLog (
    val id: UUID? = null,
    val workoutId: UUID,
    val date: LocalDate,
    val startDateTime: LocalDateTime? = null,
    val endDateTime: LocalDateTime? = null,
    val order: Int = -1,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdatedAt: LocalDateTime = LocalDateTime.now(),
    val isSynchronized: Boolean = false,
) {
    val state: WorkoutLogState get() = when {
        this.startDateTime == null -> NotStarted
        this.endDateTime == null -> InProgress
        else -> Finished
    }
}
