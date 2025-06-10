package ru.lonelywh1te.introgym.features.workout.domain.model.workout

import java.util.UUID

data class WorkoutItem (
    val workoutId: UUID,
    val name: String,
    val countOfExercises: Int,
    val order: Int,
)