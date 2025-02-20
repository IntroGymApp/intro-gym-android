package ru.lonelywh1te.introgym.core.db.dao

import androidx.room.Dao
import androidx.room.Query
import ru.lonelywh1te.introgym.core.db.entity.ExerciseEntity

@Dao
interface ExerciseDao {

    @Query("select * from exercise")
    fun getExerciseEntities(): List<ExerciseEntity>

}