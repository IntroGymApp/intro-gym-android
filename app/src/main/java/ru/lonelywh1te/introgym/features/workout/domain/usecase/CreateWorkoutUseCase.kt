package ru.lonelywh1te.introgym.features.workout.domain.usecase

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
    suspend operator fun invoke(
        workout: Workout,
        exercisesWithPlans: Map<WorkoutExercise, WorkoutExercisePlan>
    ) {
        val workoutId = workoutRepository.createWorkout(workout)

        exercisesWithPlans.forEach { (workoutExercise, workoutExercisePlan) ->
            val workoutExerciseId = workoutExerciseRepository.addWorkoutExercise(
                workoutExercise.copy(workoutId = workoutId)
            )

            workoutExercisePlanRepository.addWorkoutExercisePlan(
                workoutExercisePlan.copy(workoutExerciseId = workoutExerciseId)
            )
        }
    }
}