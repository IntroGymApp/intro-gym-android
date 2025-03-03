package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.model.ExerciseCategoryWithCount

@Dao
interface ExerciseCategoryDao {

    @Transaction
    @Query("""
        select ec.id, 
               ec.name,
               count(e.id) as countOfExercises,
               ec.img_filename as imgFilename
        from exercise_category as ec
        left join exercise as e on ec.id = e.category_id
        group by ec.id
    """)
    fun getCategoriesWithExerciseCount(): Flow<List<ExerciseCategoryWithCount>>

}