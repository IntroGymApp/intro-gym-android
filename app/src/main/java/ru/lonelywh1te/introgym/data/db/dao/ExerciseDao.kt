package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.ExerciseEntity
import ru.lonelywh1te.introgym.data.db.model.ExerciseEntityShort
import ru.lonelywh1te.introgym.data.db.model.ExerciseEntityWithTags

@Dao
interface ExerciseDao {

    @Query("select * from exercise where id = :exerciseId")
    fun getExerciseById(exerciseId: Long): Flow<ExerciseEntity>

    @Query("select * from exercise where id in (:exerciseIds)")
    suspend fun getExercisesShortByIds(exerciseIds: List<Long>): List<ExerciseEntityShort>

    @Transaction
    @Query("select * from exercise")
    fun getExercisesWithTags(): Flow<List<ExerciseEntityWithTags>>
}