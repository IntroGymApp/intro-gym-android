package ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise

import ru.lonelywh1te.introgym.features.guide.domain.model.Exercise

sealed class WorkoutExerciseItem {
    abstract val workoutExerciseId: Long
    abstract val name: String
    abstract val imgFilename: String
    abstract val order: Int

    data class Default(
        override val workoutExerciseId: Long,
        override val name: String,
        override val imgFilename: String,
        override val order: Int,
    ): WorkoutExerciseItem() {
        companion object {
            fun create(exercise: Exercise, workoutExercise: WorkoutExercise): Default {
                return Default(
                    workoutExerciseId = workoutExercise.id,
                    name = exercise.name,
                    imgFilename = exercise.imgFilename,
                    order = workoutExercise.order
                )
            }
        }
    }

    data class WithProgress(
        override val workoutExerciseId: Long,
        override val name: String,
        override val imgFilename: String,
        override val order: Int,
        val plannedSets: Int,
        val completedSets: Int
    ): WorkoutExerciseItem()


}
