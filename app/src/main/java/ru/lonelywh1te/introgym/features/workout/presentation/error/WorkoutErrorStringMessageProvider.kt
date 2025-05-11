package ru.lonelywh1te.introgym.features.workout.presentation.error

import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.AppError
import ru.lonelywh1te.introgym.core.result.BaseError
import ru.lonelywh1te.introgym.core.result.asStringRes
import ru.lonelywh1te.introgym.core.ui.ErrorStringResProvider
import ru.lonelywh1te.introgym.features.workout.domain.error.WorkoutError

object WorkoutErrorStringMessageProvider: ErrorStringResProvider {
    override fun get(error: BaseError): Int {
        return when (error) {
            is WorkoutError -> error.getStringRes()
            is AppError -> error.asStringRes()
            else -> throw IllegalArgumentException("Unknown Error type: $this")
        }
    }
}

fun WorkoutError.getStringRes(): Int {
    return when (this) {
        is WorkoutError.WorkoutEmptyName -> R.string.workout_error_empty_name
        is WorkoutError.WorkoutHasNoExercises -> R.string.workout_error_has_no_exercises
        is WorkoutError.WorkoutExercisesAndPlansMismatch -> R.string.workout_error_exercise_and_plans_mismatch
        is WorkoutError.WorkoutAlreadyStarted -> R.string.label_workout_error_already_started
    }
}