package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import ru.lonelywh1te.introgym.data.db.entity.ExerciseCategoryEntity

@Dao
interface ExerciseCategoryDao {

    @Query("select * from exercise_category")
    fun getExerciseCategories(): List<ExerciseCategoryEntity>

}