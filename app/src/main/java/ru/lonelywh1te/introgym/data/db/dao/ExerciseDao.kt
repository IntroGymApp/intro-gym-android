package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.ExerciseEntity

@Dao
interface ExerciseDao {

    @Query("select * from exercise where id = :exerciseId")
    fun getExerciseById(exerciseId: Long): Flow<ExerciseEntity>

    @Query("select * from exercise where category_id = :categoryId")
    fun getExerciseEntitiesByCategoryId(categoryId: Long): Flow<List<ExerciseEntity>>

    @Query("select * from exercise where name like '%' || :query || '%' collate nocase")
    fun searchExercicisesByName(query: String): Flow<List<ExerciseEntity>>
}