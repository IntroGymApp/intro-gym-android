package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.model.WorkoutEntityWithCountOfExercises

@Dao
interface WorkoutDao {

    @Query("select * from workout where id = :id")
    fun getWorkoutById(id: Long): Flow<WorkoutEntity>

    @Query("""
        select *
        from workout
        where is_template = 1
        order by `order`
    """)
    fun getWorkouts(): List<WorkoutEntity>

    @Query("""
        select w.*,
               count(we.workout_id) as count_of_exercises
        from workout w
        left join workout_exercise we on w.id = we.workout_id
        where is_template = 1
        group by w.id
    """)
    fun getWorkoutWithCountOfExercises(): Flow<List<WorkoutEntityWithCountOfExercises>>

    @Query("select count(*) from workout where is_template = 1")
    suspend fun getCountOfWorkouts(): Int

    @Insert
    suspend fun createWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Query("delete from workout where id = :id")
    suspend fun deleteWorkout(id: Long)

}