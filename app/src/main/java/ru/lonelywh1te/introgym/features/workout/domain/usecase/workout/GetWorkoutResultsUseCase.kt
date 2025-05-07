package ru.lonelywh1te.introgym.features.workout.domain.usecase.workout

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.core.result.getOrNull
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLog
import ru.lonelywh1te.introgym.features.home.domain.models.WorkoutLogState
import ru.lonelywh1te.introgym.features.home.domain.repository.WorkoutLogRepository
import ru.lonelywh1te.introgym.features.workout.domain.model.workout.WorkoutResult
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExercisePlan
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExercisePlanRepository
import ru.lonelywh1te.introgym.features.workout.domain.repository.WorkoutExerciseSetRepository
import java.time.LocalTime

class GetWorkoutResultsUseCase(
    private val workoutLogRepository: WorkoutLogRepository,
    private val workoutExerciseSetRepository: WorkoutExerciseSetRepository,
    private val workoutExercisePlanRepository: WorkoutExercisePlanRepository,
) {
    operator fun invoke(workoutId: Long): Flow<Result<WorkoutResult>> {
        return workoutLogRepository.getWorkoutLogByWorkoutId(workoutId).map { result ->
            when (result) {
                is Result.Success -> {
                    val workoutLog = result.data
                    val completedSets = workoutExerciseSetRepository.getWorkoutSets(workoutId).first().getOrNull()
                    val plans = workoutExercisePlanRepository.getWorkoutExercisePlans(workoutId).first().getOrNull()

                    if (workoutLog != null && completedSets != null && plans != null) {
                        val workoutResult = getWorkoutResult(workoutLog, completedSets, plans)

                        if (workoutResult != null) {
                            Result.Success(workoutResult)
                        } else {
                            Result.Failure(AppError.UNKNOWN)
                        }

                    } else {
                        Result.Failure(AppError.UNKNOWN)
                    }
                }
                is Result.Failure -> result
                is Result.Loading -> result
            }
        }
    }

    private fun getWorkoutResult(
        workoutLog: WorkoutLog,
        completedSets: List<WorkoutExerciseSet>,
        plans: List<WorkoutExercisePlan>
    ): WorkoutResult? {
        if (workoutLog.startDateTime == null || workoutLog.endDateTime == null) return null

        val duration = java.time.Duration.between(workoutLog.startDateTime, workoutLog.endDateTime).abs()
        val hours = duration.toHours().toInt()
        val minutes = (duration.toMinutes() % 60).toInt()
        val seconds = (duration.seconds % 60).toInt()

        val totalCompleted = completedSets.size.takeIf { it > 0 } ?: 1 // защита от деления на 0
        val progress = if (completedSets.isEmpty()) 0 else(totalCompleted / plans.sumOf { it.sets?.toDouble() ?: 1.0 } * 100).toInt()

        return WorkoutResult(
            totalTime = LocalTime.of(hours, minutes, seconds),
            progress = progress,
            totalWeight = completedSets.sumOf { (it.weightKg ?: 0.0f).toDouble() }.toFloat(),
            totalEffort = completedSets.sumOf { it.effort?.toPercent() ?: 0 } / totalCompleted
        )
    }
}