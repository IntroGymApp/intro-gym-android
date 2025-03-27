package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

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
    ) {
        if (exercises.size != exercisePlans.size) throw Exception("Exercises and exercise plans have different sizes")

        val workoutId = workoutRepository.createWorkout(
            workout.copy(
                id = resetId,
                order = workoutRepository.getCountOfWorkouts()
            )
        )

        exercises.forEach { workoutExercise ->
            val workoutExerciseId = workoutExerciseRepository.addWorkoutExercise(
                workoutExercise.copy(
                    id = resetId,
                    workoutId = workoutId
                )
            )

            val workoutExercisePlan = exercisePlans.find { it.workoutExerciseId == workoutExercise.id }

            if (workoutExercisePlan != null) {
                workoutExercisePlanRepository.addWorkoutExercisePlan(
                    workoutExercisePlan.copy(
                        id = resetId,
                        workoutExerciseId = workoutExerciseId
                    )
                )
            }
        }
    }
}