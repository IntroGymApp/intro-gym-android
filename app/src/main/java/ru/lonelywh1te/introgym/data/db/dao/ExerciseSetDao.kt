package ru.lonelywh1te.introgym.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.data.db.entity.ExerciseSetEntity

@Dao
interface ExerciseSetDao {

    @Query("select * from exercise_set where workout_exercise_id = :workoutExerciseId")
    fun getExerciseSets(workoutExerciseId: Long): Flow<List<ExerciseSetEntity>>

}