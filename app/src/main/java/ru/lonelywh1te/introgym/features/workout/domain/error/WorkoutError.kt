package ru.lonelywh1te.introgym.features.workout.domain.error

import ru.lonelywh1te.introgym.core.result.BaseError

sealed class WorkoutError(
    override val throwable: Throwable?,
): BaseError {

    data class WorkoutEmptyName(
        override val throwable: Throwable? = null
    ) : WorkoutError(throwable)

    data class WorkoutHasNoExercises(
        override val throwable: Throwable? = null
    ) : WorkoutError(throwable)

    data class WorkoutExercisesAndPlansMismatch(
        override val throwable: Throwable? = null
    ) : WorkoutError(throwable)

    data class WorkoutAlreadyStarted(
        override val throwable: Throwable? = null
    ) : WorkoutError(throwable)

}