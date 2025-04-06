package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import kotlinx.coroutines.flow.first
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutRepository

class UpdateWorkoutUseCase(
    private val workoutRepository: WorkoutRepository,
    private val workoutExerciseRepository: WorkoutExerciseRepository,
    private val workoutExercisePlanRepository: WorkoutExercisePlanRepository,
) {
    suspend operator fun invoke(
        workout: Workout,
        exercises: List<WorkoutExercise>,
        exercisePlans: List<WorkoutExercisePlan>
    ): Result<Unit> {
        if (exercises.size != exercisePlans.size) throw Exception("Exercises and exercise plans have different sizes")

        val updateWorkoutResult = workoutRepository.updateWorkout(workout)
        if (updateWorkoutResult is Result.Failure) return updateWorkoutResult

        val getWorkoutExercisesByIdResult = workoutExerciseRepository.getWorkoutExercisesById(workout.id).first()
        if (getWorkoutExercisesByIdResult is Result.Failure) return getWorkoutExercisesByIdResult

        val existingExercises = (getWorkoutExercisesByIdResult as Result.Success).data

        val existingExercisesIds = existingExercises.map { it.id }.toSet()
        val updatedExercisesIds = exercises.map { it.id }.toSet()

        val exercisesToDelete = existingExercises.filter { it.id !in updatedExercisesIds }
        val exercisesToAdd = exercises.filter { it.id !in existingExercisesIds }
        val exercisesToUpdate = exercises.filter { it.id in updatedExercisesIds }

        exercisesToDelete.forEach { exercise ->
            workoutExerciseRepository.deleteWorkoutExercise(exercise.id)
        }

        exercisesToAdd.forEach { exercise ->
            val addWorkoutExerciseResult = workoutExerciseRepository.addWorkoutExercise(exercise.copy(workoutId = workout.id))
            if (addWorkoutExerciseResult is Result.Failure) return addWorkoutExerciseResult

            val workoutExerciseId = (addWorkoutExerciseResult as Result.Success<Long>).data

            val exercisePlan = exercisePlans.find { it.workoutExerciseId == exercise.id }

            if (exercisePlan != null) {
                workoutExercisePlanRepository.addWorkoutExercisePlan(
                    exercisePlan.copy(workoutExerciseId = workoutExerciseId)
                )
            }
        }

        exercisesToUpdate.forEach {
            val updateWorkoutExerciseResult = workoutExerciseRepository.updateWorkoutExercise(it)
            if (updateWorkoutExerciseResult is Result.Failure) return updateWorkoutExerciseResult
        }

        exercisePlans.forEach { plan ->
            val updateWorkoutExercisePlanResult = workoutExercisePlanRepository.updateWorkoutExercisePlan(plan)
            if (updateWorkoutExercisePlanResult is Result.Failure) return updateWorkoutExercisePlanResult
        }

        return Result.Success(Unit)
    }
}