package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.ExerciseEntity
import ru.lonelywh1te.introgym.data.db.model.ExerciseEntityWithTags

@Dao
interface ExerciseDao {

    @Query("select * from exercise where id = :exerciseId")
    fun getExerciseById(exerciseId: Long): Flow<ExerciseEntity>
//
//    @Query("select * from exercise where category_id = :categoryId")
//    fun getExerciseEntitiesByCategoryId(categoryId: Long): Flow<List<ExerciseEntity>>
//
//    @Query("select * from exercise where name like '%' || :query || '%' collate nocase")
//    fun searchExercisesByName(query: String): Flow<List<ExerciseEntity>>
//
//    @Query("""
//        select distinct exercise.*
//        from exercise
//        inner join tag_to_exercise on exercise.id = tag_to_exercise.exercise_id
//        where tag_to_exercise.tag_id in (:tagsIds)
//        group by exercise.id
//        having count(distinct tag_to_exercise.tag_id) = :tagCount
//    """)
//    fun getExerciseWithSelectedTagsIds(tagsIds: List<Int>, tagCount: Int = tagsIds.size): Flow<List<ExerciseEntity>>

    @Transaction
    @Query("select * from exercise")
    fun getExercisesWithTags(): Flow<List<ExerciseEntityWithTags>>
}