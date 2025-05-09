package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.ExerciseSetEntity
import ru.lonelywh1te.introgym.data.db.model.ExerciseSetWithWorkoutLogDate
import java.time.LocalDate

@Dao
interface ExerciseSetDao {

    @Query("select * from exercise_set where workout_exercise_id = :workoutExerciseId")
    fun getExerciseSets(workoutExerciseId: Long): Flow<List<ExerciseSetEntity>>

    @Query("select * from exercise_set where workout_exercise_id in (:workoutExerciseIds)")
    fun getExerciseSetsByIds(workoutExerciseIds: List<Long>): Flow<List<ExerciseSetEntity>>

    @Transaction
    @Query("""
        select es.*, 
               wl.date as workout_log_date
        from exercise_set es
        inner join workout_exercise we ON es.workout_exercise_id = we.id
        inner join workout_log wl ON we.workout_id = wl.workout_id
        where workout_log_date between :startDate and :endDate
    """)
    fun getExerciseSetsWithWorkoutLogDateAtPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<ExerciseSetWithWorkoutLogDate>>

    @Insert
    suspend fun addExerciseSet(exerciseSet: ExerciseSetEntity): Long

    @Update
    suspend fun updateExerciseSet(exerciseSet: ExerciseSetEntity)

    @Query("delete from exercise_set where id = :id")
    suspend fun deleteExerciseSet(id: Long)

}