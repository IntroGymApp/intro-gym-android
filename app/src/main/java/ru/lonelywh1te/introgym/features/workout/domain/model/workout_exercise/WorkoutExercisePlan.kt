package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

data class WorkoutExercisePlan (
    val id: Long,
    val sets: Int?,
    val reps: Int?,
    val weightKg: Float?,
    val timeInSec: Int?,
    val distanceInMeters: Int?
)