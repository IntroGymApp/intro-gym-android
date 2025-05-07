package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExercisePlanEntity

@Dao
interface WorkoutExercisePlanDao {

    @Insert
    suspend fun createWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlanEntity): Long

    @Query("select * from workout_exercise_plan where workout_exercise_id = :workoutExerciseId")
    fun getWorkoutExercisePlanById(workoutExerciseId: Long): Flow<WorkoutExercisePlanEntity>

    @Query("select * from workout_exercise_plan where workout_exercise_id in (:workoutExerciseIds)")
    fun getWorkoutExercisePlans(workoutExerciseIds: List<Long>): Flow<List<WorkoutExercisePlanEntity>>

    @Update
    suspend fun updateWorkoutExercisePlan(workoutExercisePlan: WorkoutExercisePlanEntity)

    @Query("delete from workout_exercise_plan where id = :id")
    suspend fun deleteWorkoutExercisePlan(id: Long)

}