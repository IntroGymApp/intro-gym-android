package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity
import ru.lonelywh1te.introgym.data.db.model.WorkoutExerciseWithExerciseInfo

@Dao
interface WorkoutExerciseDao {

    @Transaction
    @Query("""
        select * 
        from workout_exercise 
        where workout_id = :workoutId
    """)
    fun getWorkoutExercisesWithExerciseInfo(workoutId: Long): Flow<List<WorkoutExerciseWithExerciseInfo>>

    @Query("select * from workout_exercise where id = :id")
    fun getWorkoutExerciseById(id: Long): Flow<WorkoutExerciseEntity>

    @Query("""
        select * 
        from workout_exercise 
        where workout_id = :workoutId 
        order by `order`
    """)
    fun getWorkoutExercisesById(workoutId: Long): Flow<List<WorkoutExerciseEntity>>

    @Query("select * from workout_exercise where `order` > :order")
    suspend fun getWorkoutExercisesWithOrderGreaterThan(order: Int): List<WorkoutExerciseEntity>

    @Insert
    suspend fun addWorkoutExercise(workoutExercise: WorkoutExerciseEntity): Long

    @Update
    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExerciseEntity)

    @Query("delete from workout_exercise where id = :id")
    suspend fun deleteWorkoutExercise(id: Long)
}