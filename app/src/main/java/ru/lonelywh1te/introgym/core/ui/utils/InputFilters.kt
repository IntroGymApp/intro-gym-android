package ru.lonelywh1te.introgym.core.ui.utils

import android.text.InputFilter
import android.text.Spanned

object InputFilters {
    class DecimalDigitsInputFilter: InputFilter {
        private val pattern = Regex("^\\d{0,4}(\\.\\d{0,2})?\$")

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            val newText = dest.replaceRange(dstart, dend, source.subSequence(start, end))
            return if (pattern.matches(newText)) null else ""
        }
    }
}