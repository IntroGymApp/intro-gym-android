package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository

class GetWorkoutExercisesUseCase(private val repository: WorkoutExerciseRepository) {
    operator fun invoke(workoutId: Long): Flow<List<WorkoutExercise>> {
        return repository.getWorkoutExercisesById(workoutId)
    }
}