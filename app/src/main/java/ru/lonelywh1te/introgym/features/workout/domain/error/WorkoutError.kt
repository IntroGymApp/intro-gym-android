package ru.lonelywh1te.introgym.features.workout.domain.error

import ru.lonelywh1te.introgym.core.result.Error

enum class WorkoutError: Error {
    WORKOUT_EMPTY_NAME,
    WORKOUT_HAS_NO_EXERCISES,
    WORKOUT_EXERCISES_AND_PLANS_MISMATCH,
}