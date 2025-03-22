package ru.lonelywh1te.introgym.features.workout.domain.model.workout

import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import java.time.LocalDateTime

data class Workout (
    val id: Long = 0,
    val name: String,
    val description: String,
    val isTemplate: Boolean,
    val order: Int,
    val workoutExercises: List<WorkoutExercise>,

    val createdAt: LocalDateTime,
    val lastUpdated: LocalDateTime,
)