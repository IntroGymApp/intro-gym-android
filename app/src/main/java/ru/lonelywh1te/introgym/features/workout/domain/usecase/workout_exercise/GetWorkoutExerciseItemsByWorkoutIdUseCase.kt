package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository

class GetWorkoutExerciseItemsByWorkoutIdUseCase(
    private val repository: WorkoutExerciseRepository,
) {
    operator fun invoke(workoutId: Long): Flow<List<WorkoutExerciseItem>> {
        return repository.getWorkoutExerciseItems(workoutId)
    }
}