package ru.lonelywh1te.introgym.core.ui.utils

import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

object WindowInsetsHelper {
    lateinit var systemBars: Insets

    fun setInsets(view: View, left: Int? = null, top: Int? = null, end: Int? = null, bottom: Int? = null) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.updatePadding(
                left = left ?: systemBars.left,
                top = top ?: systemBars.top,
                right = end ?: systemBars.right,
                bottom = bottom ?: systemBars.bottom
            )

            WindowInsetsCompat.CONSUMED
        }
    }
}