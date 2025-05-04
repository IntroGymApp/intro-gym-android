package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository

class GetWorkoutExerciseUseCase(
    private val repository: WorkoutExerciseRepository,
) {
    operator fun invoke(id: Long): Flow<Result<WorkoutExercise>> {
        return repository.getWorkoutExerciseById(id)
    }
}