package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.UUID

@Parcelize
data class WorkoutExercisePlan (
    val id: UUID = UUID.randomUUID(),
    val workoutExerciseId: UUID,
    val sets: Int? = null,
    val reps: Int? = null,
    val weightKg: Float? = null,
    val timeInSec: Int? = null,
    val distanceInMeters: Int? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
): Parcelable