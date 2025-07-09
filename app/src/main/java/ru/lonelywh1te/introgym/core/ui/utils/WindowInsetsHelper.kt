package ru.lonelywh1te.introgym.core.ui.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

object WindowInsetsHelper {
    fun setInsets(view: View, left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())

            v.updatePadding( left ?: systemBars.left, top ?: systemBars.top, right ?: systemBars.right, bottom ?: systemBars.bottom)

            insets
        }
    }
}