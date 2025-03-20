package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity

@Dao
interface WorkoutExerciseDao {

    @Query("""
        select * 
        from workout_exercise 
        where workout_id = :workoutId
        order by `order` asc
    """)
    fun getWorkoutExercises(workoutId: Long)

    @Insert
    fun addWorkoutExercise(workoutExercise: WorkoutExerciseEntity)

    @Update
    fun updateWorkoutExercise(workoutExercise: WorkoutExerciseEntity)

    @Query("delete from workout_exercise where id = :id")
    fun deleteWorkoutExercise(id: Int)
}