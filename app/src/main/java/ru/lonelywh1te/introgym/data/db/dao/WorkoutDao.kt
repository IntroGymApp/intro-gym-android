package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.model.WorkoutEntityWithExercises

@Dao
interface WorkoutDao {

    @Transaction
    @Query("""
        select *
        from workout
        where is_template = 1
        order by `order` asc
    """)
    fun getWorkoutsWithExercises(): Flow<List<WorkoutEntityWithExercises>>

    @Transaction
    @Query("select * from workout where id = :id")
    fun getWorkoutWithExercisesById(id: Long): Flow<WorkoutEntityWithExercises>

    @Insert
    fun createWorkout(workout: WorkoutEntity)

    @Query("delete from workout where id = :id")
    fun deleteWorkout(id: Long)

    @Update
    fun updateWorkout(workout: WorkoutEntity)
}