package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class WorkoutExercisePlan (
    val id: Long = 0L,
    val workoutExerciseId: Long,
    val sets: Int? = null,
    val reps: Int? = null,
    val weightKg: Float? = null,
    val timeInSec: Int? = null,
    val distanceInMeters: Int? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
): Parcelable