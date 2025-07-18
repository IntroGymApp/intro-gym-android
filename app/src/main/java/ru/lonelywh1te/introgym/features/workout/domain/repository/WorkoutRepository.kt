package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.Workout
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutItem
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import java.util.UUID

interface WorkoutRepository {

    fun getWorkoutItems(): Flow<Result<List<WorkoutItem>>>

    fun getWorkouts(): Result<List<Workout>>

    fun getWorkoutById(workoutId: UUID): Flow<Result<Workout>>

    suspend fun createFullWorkout(
        workout: Workout,
        exercisesWithPlans: Map<WorkoutExercise, WorkoutExercisePlan>
    ): Result<Unit>

    suspend fun updateWorkout(workout: Workout): Result<Unit>

    suspend fun deleteWorkoutWithReorder(id: UUID): Result<Unit>

    suspend fun getCountOfWorkouts(): Result<Int>

}