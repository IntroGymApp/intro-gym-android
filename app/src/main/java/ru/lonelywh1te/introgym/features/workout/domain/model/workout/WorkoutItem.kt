package ru.lonelywh1te.introgym.features.workout.domain.model.workout

data class WorkoutItem (
    val workoutId: Long,
    val name: String,
    val countOfExercises: Int,
    val order: Int,
)