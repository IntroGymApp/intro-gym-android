package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExercisePlanEntity

@Dao
interface WorkoutExercisePlanDao {

    @Insert
    suspend fun createWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlanEntity)

}