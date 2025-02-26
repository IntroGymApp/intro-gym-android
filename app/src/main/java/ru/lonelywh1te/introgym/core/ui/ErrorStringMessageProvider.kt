package ru.lonelywh1te.introgym.core.ui

import androidx.annotation.StringRes
import ru.lonelywh1te.introgym.core.result.Error

interface ErrorStringResProvider {

    @StringRes
    fun get(error: Error): Int

}