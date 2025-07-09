package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout_exercise_set

import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseSetRepository

class UpdateWorkoutExerciseSetUseCase(
    private val repository: WorkoutExerciseSetRepository
) {
    suspend operator fun invoke(workoutExerciseSet: WorkoutExerciseSet): Result<Unit> {
        return repository.updateWorkoutExerciseSet(workoutExerciseSet)
    }
}