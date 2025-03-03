package ru.lonelywh1te.introgym.core.ui

import android.net.Uri

enum class AssetType(val folder: String, val extension: String) {
    CATEGORY_IMAGE("images/category", "webp"),
    EXERCISE_PREVIEW_IMAGE("images/exercise/preview", "webp"),
    EXERCISE_ANIMATION("images/exercise/anim", "webp"),
}

object AssetPath {
    fun get(type: AssetType, filename: String): Uri {
        return Uri.parse("file:///android_asset/${type.folder}/$filename.${type.extension}")
    }
}