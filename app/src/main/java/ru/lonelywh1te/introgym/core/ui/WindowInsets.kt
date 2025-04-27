package ru.lonelywh1te.introgym.core.ui

import android.view.View
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object WindowInsets {
    lateinit var systemBars: Insets

    fun setInsets(view: View, left: Int? = null, top: Int? = null, end: Int? = null, bottom: Int? = null) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                left ?: systemBars.left,
                top ?: systemBars.top,
                end ?: systemBars.right,
                bottom ?: systemBars.bottom
            )
            insets
        }
    }
}