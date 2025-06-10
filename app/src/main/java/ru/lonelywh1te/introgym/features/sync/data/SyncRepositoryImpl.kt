package ru.lonelywh1te.introgym.features.sync.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.onFailure
import ru.lonelywh1te.introgym.core.result.onSuccess
import ru.lonelywh1te.introgym.data.db.dao.ExerciseSetDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutLogDao
import ru.lonelywh1te.introgym.data.db.sqliteTryCatching
import ru.lonelywh1te.introgym.data.network.NetworkError
import ru.lonelywh1te.introgym.data.network.safeNetworkCall
import ru.lonelywh1te.introgym.features.sync.data.dto.SyncDataDto
import ru.lonelywh1te.introgym.features.sync.domain.SyncRepository
import java.time.LocalDate

class SyncRepositoryImpl(
    private val syncApi: SyncApi,

    private val workoutLogDao: WorkoutLogDao,
    private val workoutDao: WorkoutDao,
    private val workoutExerciseDao: WorkoutExerciseDao,
    private val workoutExercisePlanDao: WorkoutExercisePlanDao,
    private val exerciseSetDao: ExerciseSetDao,
): SyncRepository {
    override suspend fun sync(): Result<Unit> {
        val localDataResult = getLocalData()

        localDataResult
            .onSuccess { syncDataDto ->
                Log.d("SyncRepository", GsonBuilder().serializeNulls().create().toJson(syncDataDto))

                pushLocalChanges(syncDataDto)
                    .onSuccess {
                        Log.d("SyncRepository", "Local data pushed!\nWorkoutLogs: ${syncDataDto.workoutLogs.size}\nWorkouts: ${syncDataDto.workouts.size}\nWorkoutExercises: ${syncDataDto.workoutExercises.size}\nWorkoutExercisePlans: ${syncDataDto.workoutExercisePlans.size}\nExerciseSets: ${syncDataDto.exerciseSets.size}")
                    }
                    .onFailure { return Result.Failure(it) }
            }
            .onFailure { return Result.Failure(it)}

        return Result.Success(Unit)
    }

    private suspend fun getLocalData(): Result<SyncDataDto> = sqliteTryCatching {
        SyncDataDto(
            workoutLogs = workoutLogDao.getUnsynchronizedWorkoutLogs().map { it.toSyncWorkoutLogDto() },
            workouts = workoutDao.getUnsynchronizedWorkouts().map { it.toSyncWorkoutDto() },
            workoutExercises = workoutExerciseDao.getUnsynchronizedWorkoutExercises().map { it.toSyncWorkoutExerciseDto() },
            workoutExercisePlans = workoutExercisePlanDao.getUnsyncedWorkoutExercisePlans().map { it.toSyncWorkoutExercisePlanDto() },
            exerciseSets = exerciseSetDao.getUnsychronizedExerciseSets().map { it.toSyncExerciseSetDto() },
        )
    }

    private suspend fun getRemoteData(): Result<SyncDataDto> = safeNetworkCall {
        val response = syncApi.getRemoteSyncData(null)
        val body = response.body()

        when {
            response.isSuccessful && body != null -> body
            response.code() in 500..599 -> return Result.Failure(NetworkError.ServerError(response.code(), response.errorBody()?.string()))
            else -> return Result.Failure(NetworkError.Unknown(message = response.errorBody()?.string()))
        }
    }

    private suspend fun pushLocalChanges(syncDataDto: SyncDataDto): Result<Unit> = safeNetworkCall {
        val response = syncApi.pushSyncData(syncDataDto)
        val body = response.body()

        when {
            response.isSuccessful && body != null -> Unit
            response.code() in 500..599 -> return Result.Failure(NetworkError.ServerError(response.code(), response.errorBody()?.string()))
            else -> return Result.Failure(NetworkError.Unknown(message = response.errorBody()?.string()))
        }
    }
}