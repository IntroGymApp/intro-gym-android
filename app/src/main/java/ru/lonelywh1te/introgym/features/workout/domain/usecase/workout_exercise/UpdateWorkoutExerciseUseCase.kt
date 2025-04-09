package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository

class UpdateWorkoutExerciseUseCase(
    private val repository: WorkoutExerciseRepository
) {
    suspend operator fun invoke(workoutExercise: WorkoutExercise): Result<Unit> {
        return repository.updateWorkoutExercise(workoutExercise)
    }
}