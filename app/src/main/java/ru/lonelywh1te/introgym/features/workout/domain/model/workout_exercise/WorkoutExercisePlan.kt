package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import java.time.LocalDateTime

data class WorkoutExercisePlan (
    val id: Long = 0L,
    val workoutExerciseId: Long,
    val sets: Int?,
    val reps: Int?,
    val weightKg: Float?,
    val timeInSec: Int?,
    val distanceInMeters: Int?,

    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime,
)