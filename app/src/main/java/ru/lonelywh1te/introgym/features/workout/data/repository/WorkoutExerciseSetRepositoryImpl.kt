package ru.lonelywh1te.introgym.features.workout.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.db.asSafeSQLiteFlow
import ru.lonelywh1te.introgym.data.db.dao.ExerciseSetDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.entity.ExerciseSetEntity
import ru.lonelywh1te.introgym.data.db.sqliteTryCatching
import ru.lonelywh1te.introgym.features.workout.data.toExerciseSetEntity
import ru.lonelywh1te.introgym.features.workout.data.toWorkoutExerciseSet
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseSetRepository
import java.time.LocalDateTime
import java.util.UUID

class WorkoutExerciseSetRepositoryImpl(
    private val exerciseSetDao: ExerciseSetDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
): WorkoutExerciseSetRepository {
    override fun getWorkoutSets(workoutId: UUID): Flow<Result<List<WorkoutExerciseSet>>> {
        return workoutExerciseDao.getWorkoutExercisesById(workoutId)
            .flatMapLatest { workoutExercises ->
                val ids = workoutExercises.map { it.id }

                exerciseSetDao.getExerciseSetsByIds(ids)
                    .map<List<ExerciseSetEntity>, Result<List<WorkoutExerciseSet>>> { list ->
                        Result.Success(list.map { it.toWorkoutExerciseSet() })
                    }
            }
            .asSafeSQLiteFlow()
    }

    override fun getWorkoutExerciseSets(workoutExerciseId: UUID): Flow<Result<List<WorkoutExerciseSet>>> {
        return exerciseSetDao.getExerciseSets(workoutExerciseId)
            .map<List<ExerciseSetEntity>, Result<List<WorkoutExerciseSet>>> {
                    list -> Result.Success(list.map { it.toWorkoutExerciseSet() })
            }
            .asSafeSQLiteFlow()
    }

    override suspend fun addWorkoutExerciseSet(workoutExerciseSet: WorkoutExerciseSet): Result<Unit> {
        val exerciseSetEntity = workoutExerciseSet.copy(
            createdAt = LocalDateTime.now(),
            lastUpdated = LocalDateTime.now(),
        ).toExerciseSetEntity()

        return sqliteTryCatching {
            exerciseSetDao.addExerciseSet(exerciseSetEntity)
        }
    }

    override suspend fun deleteWorkoutExerciseSetById(id: UUID): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateWorkoutExerciseSet(workoutExerciseSet: WorkoutExerciseSet): Result<Unit> {
        TODO("Not yet implemented")
    }

}