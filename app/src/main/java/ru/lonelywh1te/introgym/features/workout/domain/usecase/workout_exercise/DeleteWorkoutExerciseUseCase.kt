package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import java.util.UUID

class DeleteWorkoutExerciseUseCase(private val repository: WorkoutExerciseRepository) {
    suspend operator fun invoke(id: UUID): Result<Unit> {
        return repository.deleteWorkoutExerciseWithReorder(id)
    }
}