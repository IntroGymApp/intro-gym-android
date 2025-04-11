package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise

data class WorkoutExerciseItem(
    val workoutExerciseId: Long,
    val name: String,
    val imgFilename: String,
    val order: Int,
) {
    companion object {
        fun create(exercise: Exercise, workoutExercise: WorkoutExercise): WorkoutExerciseItem {
            return WorkoutExerciseItem(
                workoutExerciseId = workoutExercise.id,
                name = exercise.name,
                imgFilename = exercise.imgFilename,
                order = workoutExercise.order
            )
        }
    }
}
