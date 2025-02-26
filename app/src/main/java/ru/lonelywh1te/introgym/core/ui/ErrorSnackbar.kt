package ru.lonelywh1te.introgym.core.ui

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import ru.lonelywh1te.introgym.R
import ru.lonelywh1te.introgym.core.result.Error

class ErrorSnackbar (
    private val view: View,
    private val errorMessageProvider: ErrorMessageProvider
){
    fun show(error: Error) {
        Snackbar.make(view, errorMessageProvider.getMessage(error), Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(view.context, R.color.red))
            .setTextColor(ContextCompat.getColor(view.context, R.color.white))
            .show()
    }
}