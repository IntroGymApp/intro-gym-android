package ru.lonelywh1te.introgym.core.ui

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import ru.lonelywh1te.introgym.R

class ErrorSnackbar (
    private val view: View,
){
    fun show(message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(view.context, R.color.red_color))
            .setTextColor(ContextCompat.getColor(view.context, R.color.background_color))
            .show()
    }
}