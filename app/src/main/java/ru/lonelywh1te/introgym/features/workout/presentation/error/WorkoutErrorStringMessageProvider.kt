package ru.lonelywh1te.introgym.features.workout.presentation.error

import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.Error
import ru.lonelywh1te.introgym.core.result.asStringRes
import ru.lonelywh1te.introgym.core.ui.ErrorStringResProvider
import ru.lonelywh1te.introgym.features.workout.domain.error.WorkoutError

object WorkoutErrorStringMessageProvider: ErrorStringResProvider {
    override fun get(error: Error): Int {
        return when (error) {
            is WorkoutError -> error.getStringRes()
            is AppError -> error.asStringRes()
            else -> throw IllegalArgumentException("Unknown Error type: $this")
        }
    }
}

fun WorkoutError.getStringRes(): Int {
    return when (this) {
        WorkoutError.WORKOUT_EMPTY_NAME -> R.string.workout_error_empty_name
        WorkoutError.WORKOUT_HAS_NO_EXERCISES -> R.string.workout_error_has_no_exercises
        WorkoutError.EMPTY_EXERCISE_PLAN -> R.string.workout_error_empty_exercise_plan
        WorkoutError.WORKOUT_EXERCISES_AND_PLANS_MISMATCH -> R.string.workout_error_exercise_and_plans_mismatch
    }
}