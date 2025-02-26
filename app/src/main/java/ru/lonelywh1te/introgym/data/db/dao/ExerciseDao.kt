package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import ru.lonelywh1te.introgym.data.db.entity.ExerciseEntity

@Dao
interface ExerciseDao {

    @Query("select * from exercise")
    fun getExerciseEntities(): List<ExerciseEntity>

}