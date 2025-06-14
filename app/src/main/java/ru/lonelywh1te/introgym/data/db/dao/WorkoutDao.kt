package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.model.WorkoutEntityWithCountOfExercises
import java.util.UUID

@Dao
interface WorkoutDao {

    @Query("select * from workout where id = :id")
    fun getWorkoutById(id: UUID): Flow<WorkoutEntity?>

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
        where w.id = :id
        group by w.id
    """)
    fun getWorkoutWithCountOfExercises(id: UUID): WorkoutEntityWithCountOfExercises

    @Query("""
        select w.*,
               count(we.workout_id) as count_of_exercises
        from workout w
        left join workout_exercise we on w.id = we.workout_id
        where is_template = 1
        group by w.id
    """)
    fun getWorkoutListWithCountOfExercises(): Flow<List<WorkoutEntityWithCountOfExercises>>

    @Query("select * from workout where is_synchronized = 0")
    suspend fun getUnsynchronizedWorkouts(): List<WorkoutEntity>

    @Query("select * from workout where `order` > :order")
    suspend fun getWorkoutsWithOrderGreaterThan(order: Int): List<WorkoutEntity>

    @Query("select count(*) from workout where is_template = 1")
    suspend fun getCountOfWorkouts(): Int

    @Query("""
        select count(*)
        from workout w
        inner join workout_log wl on w.id = wl.workout_id
        where wl.end_datetime is not null
    """)
    suspend fun getCountOfFinishedWorkouts(): Int

    @Insert
    suspend fun createWorkout(workout: WorkoutEntity)

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Query("delete from workout where id = :id")
    suspend fun deleteWorkout(id: UUID)

}