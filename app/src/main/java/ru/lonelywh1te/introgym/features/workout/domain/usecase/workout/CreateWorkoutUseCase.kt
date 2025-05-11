package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.WorkoutValidator
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class CreateWorkoutUseCase(
    private val workoutRepository: WorkoutRepository,
    private val validator: WorkoutValidator,
) {
    suspend operator fun invoke(
        workout: Workout,
        exercises: List<WorkoutExercise>,
        exercisePlans: List<WorkoutExercisePlan>
    ): Result<Unit> {

        validator.validate(workout, exercises, exercisePlans).let { result ->
            if (result is Result.Failure) return result
        }

        val exercisesWithPlans = exercises.associateWith { workoutExercise ->
            exercisePlans.find { workoutExercise.id == it.workoutExerciseId } ?: throw NoSuchElementException("No plan found for exercise with ID ${workoutExercise.id}")
        }

        return workoutRepository.createFullWorkout(workout, exercisesWithPlans)
    }
}