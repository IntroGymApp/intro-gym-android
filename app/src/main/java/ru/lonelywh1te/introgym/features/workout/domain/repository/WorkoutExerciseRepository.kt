package ru.lonelywh1te.introgym.features.workout.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercise
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseItem

interface WorkoutExerciseRepository {

    fun getWorkoutExerciseItems(workoutId: Long): Flow<List<WorkoutExerciseItem>>

    fun getWorkoutExercisesById(workoutId: Long): Flow<List<WorkoutExercise>>

    suspend fun addWorkoutExercise(workoutExercise: WorkoutExercise): Long

    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise)

    suspend fun deleteWorkoutExercise(id: Long)

}