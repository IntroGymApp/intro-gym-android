package ru.lonelywh1te.introgym.features.workout.presentation.helper

import androidx.annotation.AttrRes
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.ui.utils.DateAndTimeStringFormatUtils
import ru.lonelywh1te.introgym.features.workout.domain.model.Effort
import ru.lonelywh1te.introgym.features.workout.domain.model.workout_exercise.WorkoutExerciseSet
import java.time.LocalTime

object WorkoutExerciseSetHelper {
    fun getStringInfo(workoutExerciseSet: WorkoutExerciseSet): String {
        val reps = workoutExerciseSet.reps
        val weightKg = workoutExerciseSet.weightKg
        val timeInSeconds = workoutExerciseSet.timeInSeconds
        val distanceInMeters = workoutExerciseSet.distanceInMeters

        if (reps == null && weightKg == null && timeInSeconds == null && distanceInMeters == null) {
            return "—"
        }

        val parts = mutableListOf<String>()

        if (reps != null && weightKg != null) {
            parts.add("$reps x ${weightKg.toInt()}кг")
        } else if (reps != null) {
            parts.add("$reps раз")
        } else if (weightKg != null) {
            parts.add("${weightKg.toInt()}кг")
        }

        timeInSeconds?.toLong()?.let {
            parts.add(LocalTime.ofSecondOfDay(it).format(DateAndTimeStringFormatUtils.fullTimeFormatter))
        }

        distanceInMeters?.takeIf { it > 0 }?.let {
            parts.add("${it}м")
        }

        return parts.joinToString(" • ")
    }

    @AttrRes
    fun getEffortColor(workoutExerciseSet: WorkoutExerciseSet): Int {
        return when(workoutExerciseSet.effort) {
            Effort.WARMUP -> R.attr.igWarmUpEffortColor
            Effort.LOW -> R.attr.igLowEffortColor
            Effort.MEDIUM -> R.attr.igMidEffortColor
            Effort.HARD -> R.attr.igHardEffortColor
            Effort.MAX -> R.attr.igMaxEffortColor
            null -> R.attr.igWarmUpEffortColor
        }
    }
}