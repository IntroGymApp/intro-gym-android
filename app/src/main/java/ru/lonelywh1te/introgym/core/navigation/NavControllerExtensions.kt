package ru.lonelywh1te.introgym.core.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.safeNavigate(direction: NavDirections) {
    if (currentDestination?.getAction(direction.actionId) == null) {
        Log.w("NavController", "Navigation action ${direction.actionId} not found from current destination")
        return
    }

    try {
        navigate(direction)
    } catch (e: Exception) {
        Log.e("NavController", "NAV_CONTROLLER_ERROR: $e")
    }
}