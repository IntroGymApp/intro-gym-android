package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class CreateWorkoutUseCase(
    private val workoutRepository: WorkoutRepository,
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val workoutExercisePlanRepository: WorkoutExercisePlanRepository,
) {
    private val resetId = 0L

    suspend operator fun invoke(
        workout: Workout,
        exercises: List<WorkoutExercise>,
        exercisePlans: List<WorkoutExercisePlan>
    ): Result<Unit> {
        if (exercises.size != exercisePlans.size) return Result.Failure(AppError.UNKNOWN)

        val countOfWorkoutResult = workoutRepository.getCountOfWorkouts()
        if (countOfWorkoutResult is Result.Failure) return countOfWorkoutResult

        val newWorkoutOrder = (countOfWorkoutResult as Result.Success<Int>).data

        val saveWorkoutResult = saveWorkout(workout, newWorkoutOrder)
        if (saveWorkoutResult is Result.Failure) return saveWorkoutResult

        val workoutId = (saveWorkoutResult as Result.Success<Long>).data

        return saveExercises(workoutId, exercises, exercisePlans)
    }

    private suspend fun saveWorkout(workout: Workout, order: Int): Result<Long> {
        val createWorkoutResult = workoutRepository.createWorkout(
            workout.copy(id = resetId, order = order)
        )

        return createWorkoutResult
    }

    private suspend fun saveExercises(
        workoutId: Long,
        exercises: List<WorkoutExercise>,
        exercisePlans: List<WorkoutExercisePlan>
    ): Result<Unit> {
        exercises.forEach { workoutExercise ->
            val addWorkoutExerciseResult = workoutExerciseRepository.addWorkoutExercise(
                workoutExercise.copy(
                    id = resetId,
                    workoutId = workoutId
                )
            )
            if (addWorkoutExerciseResult is Result.Failure) return addWorkoutExerciseResult

            val workoutExerciseId = (addWorkoutExerciseResult as Result.Success<Long>).data

            val workoutExercisePlan = exercisePlans.find { it.workoutExerciseId == workoutExercise.id }

            if (workoutExercisePlan != null) {
                val addWorkoutExercisePlanResult = workoutExercisePlanRepository.addWorkoutExercisePlan(
                    workoutExercisePlan.copy(
                        id = resetId,
                        workoutExerciseId = workoutExerciseId
                    )
                )

                if (addWorkoutExercisePlanResult is Result.Failure) return addWorkoutExercisePlanResult
            }
        }

        return Result.Success(Unit)
    }
}