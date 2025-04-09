package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository

class DeleteWorkoutExerciseUseCase(private val repository: WorkoutExerciseRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.deleteWorkoutExerciseWithReorder(id)
    }
}