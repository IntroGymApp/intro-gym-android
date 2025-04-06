package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class CreateWorkoutUseCase(
    private val workoutRepository: WorkoutRepository,
) {
    suspend operator fun invoke(
        workout: Workout,
        exercises: List<WorkoutExercise>,
        exercisePlans: List<WorkoutExercisePlan>
    ): Result<Unit> {

        val exercisesWithPlans = exercises.associateWith { workoutExercise ->
            exercisePlans.find { workoutExercise.id == it.workoutExerciseId } ?: return Result.Failure(AppError.UNKNOWN)
        }

        return workoutRepository.createFullWorkout(workout, exercisesWithPlans)
    }
}