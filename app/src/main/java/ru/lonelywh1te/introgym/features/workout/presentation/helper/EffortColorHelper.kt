package ru.lonelywh1te.introgym.features.workout.presentation.helper

import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.features.workout.domain.model.Effort

object EffortColorHelper {
    fun getColor(effort: Effort): Int {
        return when(effort) {
            Effort.WARMUP -> R.attr.igWarmUpEffortColor
            Effort.LOW -> R.attr.igLowEffortColor
            Effort.MEDIUM -> R.attr.igMidEffortColor
            Effort.HARD -> R.attr.igHardEffortColor
            Effort.MAX -> R.attr.igMaxEffortColor
        }
    }
}