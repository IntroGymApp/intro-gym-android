package ru.lonelywh1te.introgym.features.workout.domain.model.workout

import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem

data class WorkoutInfo (
    val name: String,
    val description: String,
    val workoutExerciseItems: List<WorkoutExerciseItem>
)