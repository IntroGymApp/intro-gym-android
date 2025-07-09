package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_set

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseSetRepository
import java.util.UUID

class DeleteWorkoutExerciseSetUseCase(
    private val repository: WorkoutExerciseSetRepository
) {
    suspend operator fun invoke(id: UUID): Result<Unit> {
        return repository.deleteWorkoutExerciseSetById(id)
    }
}