package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import java.util.UUID

class GetWorkoutExerciseUseCase(
    private val repository: WorkoutExerciseRepository,
) {
    operator fun invoke(id: UUID): Flow<Result<WorkoutExercise>> {
        return repository.getWorkoutExerciseById(id)
    }
}