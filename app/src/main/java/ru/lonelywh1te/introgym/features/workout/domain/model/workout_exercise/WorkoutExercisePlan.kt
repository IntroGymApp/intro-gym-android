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
        fun empty() = WorkoutExercisePlan(
            id = -1L,
            workoutExerciseId = -1L,
            sets = 0,
            reps = null,
            weightKg = null,
            timeInSec = null,
            distanceInMeters = null,
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        )
    }
}