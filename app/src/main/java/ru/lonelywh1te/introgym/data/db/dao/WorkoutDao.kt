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
    """)
    fun getWorkouts(): Flow<List<WorkoutEntity>>

    @Query("""
        select *,
               count(we.workout_id) as countOfExercises
        from workout w
        left join workout_exercise we on w.id = we.workout_id
        where is_template = 1
        group by w.id
    """)
    fun getWorkoutWithCountOfExercises(): Flow<List<WorkoutEntityWithCountOfExercises>>

    @Insert
    suspend fun createWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity): Long

    @Query("delete from workout where id = :id")
    suspend fun deleteWorkout(id: Long)

}