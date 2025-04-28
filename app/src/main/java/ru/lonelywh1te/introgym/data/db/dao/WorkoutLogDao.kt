package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.WorkoutLogEntity
import java.time.LocalDate

@Dao
interface WorkoutLogDao {

    @Query("select * from workout_log where date = :date")
    fun getWorkoutLogListByDate(date: LocalDate): Flow<List<WorkoutLogEntity>>

    @Query("select count(*) from workout_log where date = :date")
    suspend fun getCountOfWorkoutLogAtDate(date: LocalDate): Int

    @Query("select distinct date from workout_log")
    suspend fun getWorkoutLogDates(): List<LocalDate>

    @Update
    suspend fun updateWorkoutLog(workoutLog: WorkoutLogEntity)

    @Insert
    suspend fun addWorkoutLog(workoutLog: WorkoutLogEntity)

}