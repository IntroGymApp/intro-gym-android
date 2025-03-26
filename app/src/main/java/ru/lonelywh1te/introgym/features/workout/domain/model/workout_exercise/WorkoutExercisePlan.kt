package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class WorkoutExercisePlan (
    val id: Long,
    val workoutExerciseId: Long,
    val sets: Int?,
    val reps: Int?,
    val weightKg: Float?,
    val timeInSec: Int?,
    val distanceInMeters: Int?,

    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime,
): Parcelable {
    companion object {
        fun empty(workoutExerciseId: Long = 0L) = WorkoutExercisePlan(
            id = 0L,
            workoutExerciseId = workoutExerciseId,
            sets = null,
            reps = null,
            weightKg = null,
            timeInSec = null,
            distanceInMeters = null,
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        )
    }
}