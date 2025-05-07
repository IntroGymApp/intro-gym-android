package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.ExerciseSetEntity

@Dao
interface ExerciseSetDao {

    @Query("select * from exercise_set where workout_exercise_id = :workoutExerciseId")
    fun getExerciseSets(workoutExerciseId: Long): Flow<List<ExerciseSetEntity>>

    @Query("select * from exercise_set where workout_exercise_id in (:workoutExerciseIds)")
    fun getExerciseSetsByIds(workoutExerciseIds: List<Long>): Flow<List<ExerciseSetEntity>>

    @Insert
    suspend fun addExerciseSet(exerciseSet: ExerciseSetEntity): Long

    @Update
    suspend fun updateExerciseSet(exerciseSet: ExerciseSetEntity)

    @Query("delete from exercise_set where id = :id")
    suspend fun deleteExerciseSet(id: Long)

}