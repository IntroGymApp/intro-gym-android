package ru.lonelywh1te.introgym.features.workout.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository
import java.time.LocalDateTime

class WorkoutExercisePlanRepositoryImpl(
    private val workoutExercisePlanDao: WorkoutExercisePlanDao,
): WorkoutExercisePlanRepository {
    override fun getWorkoutExercisePlanById(workoutExerciseId: Long): Flow<WorkoutExercisePlan> {
        return workoutExercisePlanDao.getWorkoutExercisePlanById(workoutExerciseId).map { it.toWorkoutExercisePlan() }
    }

    override suspend fun addWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan): Long {
        val workoutExercisePlanEntity = workoutExercisePlan.copy(
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExercisePlanEntity()

        return workoutExercisePlanDao.createWorkoutExercisePlan(workoutExercisePlanEntity)
    }

    override suspend fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlan) {
        val workoutExercisePlanEntity = workoutExercisePlan.copy(
            lastUpdated = LocalDateTime.now(),
        ).toWorkoutExercisePlanEntity()

        workoutExercisePlanDao.updateWorkoutExercisePlan(workoutExercisePlanEntity)
    }

    override suspend fun deleteWorkoutExercisePlan(id: Long) {
        workoutExercisePlanDao.deleteWorkoutExercisePlan(id)
    }
}