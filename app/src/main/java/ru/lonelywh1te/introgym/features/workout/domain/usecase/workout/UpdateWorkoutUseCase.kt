package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
    ) {
        if (exercises.size != exercisePlans.size) throw Exception("Exercises and exercise plans have different sizes")

        workoutRepository.updateWorkout(workout)

        val existingExercises = workoutExerciseRepository.getWorkoutExercisesById(workout.id).first()
        val existingExercisesIds = existingExercises.map { it.id }.toSet()
        val updatedExercisesIds = exercises.map { it.id }.toSet()

        val exercisesToDelete = existingExercises.filter { it.id !in existingExercisesIds }
        val exercisesToAdd = exercises.filter { it.id !in existingExercisesIds }
        val exercisesToUpdate = exercises.filter { it.id in updatedExercisesIds }

        exercisesToDelete.forEach { exercise ->
            workoutExerciseRepository.deleteWorkoutExercise(exercise.id)
        }

        exercisesToAdd.forEach { exercise ->
            val workoutExerciseId = workoutExerciseRepository.addWorkoutExercise(exercise.copy(workoutId = workout.id))
            val exercisePlan = exercisePlans.find { it.workoutExerciseId == exercise.id }

            if (exercisePlan != null) {
                workoutExercisePlanRepository.addWorkoutExercisePlan(
                    exercisePlan.copy(workoutExerciseId = workoutExerciseId)
                )
            }
        }

        exercisesToUpdate.forEach {
            workoutExerciseRepository.updateWorkoutExercise(it)
        }

        exercisePlans.forEach { plan ->
            workoutExercisePlanRepository.updateWorkoutExercisePlan(plan)
        }

    }
}