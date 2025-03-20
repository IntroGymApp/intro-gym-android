package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

data class WorkoutExercise (
    val id: Long,
    val name: String,
    val description: String,
    val animFilename: String,
    val plan: WorkoutExercisePlan,
)