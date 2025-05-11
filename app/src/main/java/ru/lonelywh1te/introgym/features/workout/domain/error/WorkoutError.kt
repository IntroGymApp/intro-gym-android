package ru.lonelywh1te.introgym.features.workout.domain.error

import ru.lonelywh1te.introgym.core.result.BaseError

sealed class WorkoutError(
    override val message: String?,
    override val cause: Throwable?,
): BaseError {

    data class WorkoutEmptyName(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : WorkoutError(message, cause)

    data class WorkoutHasNoExercises(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : WorkoutError(message, cause)

    data class WorkoutExercisesAndPlansMismatch(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : WorkoutError(message, cause)

    data class WorkoutAlreadyStarted(
        override val message: String? = null,
        override val cause: Throwable? = null
    ) : WorkoutError(message, cause)

}