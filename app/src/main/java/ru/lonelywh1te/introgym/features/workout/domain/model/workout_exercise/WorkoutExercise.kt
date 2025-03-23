package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import java.time.LocalDateTime

data class WorkoutExercise (
    val id: Long = -1,
    val workoutId: Long = -1,
    val exerciseId: Long,
    val order: Int = -1,
    val comment: String,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
)