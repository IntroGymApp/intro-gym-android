package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import java.time.LocalDateTime

data class WorkoutExercisePlan (
    val id: Long = -1,
    val workoutExerciseId: Long = -1,
    val sets: Int?,
    val reps: Int?,
    val weightKg: Float?,
    val timeInSec: Int?,
    val distanceInMeters: Int?,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
)