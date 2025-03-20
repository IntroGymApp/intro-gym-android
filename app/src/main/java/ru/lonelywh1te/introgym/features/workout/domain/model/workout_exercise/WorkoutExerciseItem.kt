package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

data class WorkoutExerciseItem(
    val id: Long,
    val name: String,
    val imgFilename: String,
    val order: Int,
)
