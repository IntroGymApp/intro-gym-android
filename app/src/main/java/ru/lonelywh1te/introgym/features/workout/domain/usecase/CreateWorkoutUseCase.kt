package ru.lonelywh1te.introgym.features.workout.domain.usecase

import android.util.Log
import kotlinx.coroutines.flow.first
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
        exercisesWithPlans: Map<WorkoutExercise, WorkoutExercisePlan>
    ) {
        val workoutId = workoutRepository.createWorkout(workout.copy(id = resetId))

        exercisesWithPlans.forEach { (workoutExercise, workoutExercisePlan) ->
            val workoutExerciseId = workoutExerciseRepository.addWorkoutExercise(
                workoutExercise.copy(
                    id = resetId,
                    workoutId = workoutId
                )
            )

            workoutExercisePlanRepository.addWorkoutExercisePlan(
                workoutExercisePlan.copy(
                    id = resetId,
                    workoutExerciseId = workoutExerciseId
                )
            )
        }
    }
}