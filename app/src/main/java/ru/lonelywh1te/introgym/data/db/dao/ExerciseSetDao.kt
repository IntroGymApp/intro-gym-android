package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.ExerciseSetEntity
import ru.lonelywh1te.introgym.data.db.model.ExerciseSetWithExerciseCategoryId
import ru.lonelywh1te.introgym.data.db.model.ExerciseSetWithWorkoutLogDate
import java.time.LocalDate
import java.util.UUID

@Dao
interface ExerciseSetDao {

    @Query("select * from exercise_set where workout_exercise_id = :workoutExerciseId")
    fun getExerciseSets(workoutExerciseId: UUID): Flow<List<ExerciseSetEntity>>

    @Query("select * from exercise_set where workout_exercise_id in (:workoutExerciseIds)")
    fun getExerciseSetsByIds(workoutExerciseIds: List<UUID>): Flow<List<ExerciseSetEntity>>

    @Transaction
    @Query("""
        select es.*, 
               wl.date as workout_log_date
        from exercise_set es
        inner join workout_exercise we on es.workout_exercise_id = we.id
        inner join workout_log wl on we.workout_id = wl.workout_id
        where workout_log_date between :startDate and :endDate
    """)
    fun getExerciseSetsWithWorkoutLogDateAtPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<ExerciseSetWithWorkoutLogDate>>

    @Query("""
        select es.*,
               ec.name as exercise_category_name
        from exercise_set es
        inner join workout_exercise we ON es.workout_exercise_id = we.id
        inner join workout_log wl on we.workout_id = wl.workout_id
        inner join exercise e on we.exercise_id = e.id
        inner join exercise_category ec on e.category_id = ec.id
        where wl.date between :startDate and :endDate
    """)
    fun getExerciseSetsWithExerciseCategoryIdAtPeriod(startDate: LocalDate, endDate: LocalDate): Flow<List<ExerciseSetWithExerciseCategoryId>>

    @Query("select * from exercise_set where is_synchronized = 0")
    suspend fun getUnsychronizedExerciseSets(): List<ExerciseSetEntity>

    @Query("select * from exercise_set where id = :id")
    suspend fun getExerciseSet(id: UUID): ExerciseSetEntity

    @Insert
    suspend fun addExerciseSet(exerciseSet: ExerciseSetEntity)

    @Update
    suspend fun updateExerciseSet(exerciseSet: ExerciseSetEntity)

    @Query("delete from exercise_set where id = :id")
    suspend fun deleteExerciseSet(id: UUID)

}