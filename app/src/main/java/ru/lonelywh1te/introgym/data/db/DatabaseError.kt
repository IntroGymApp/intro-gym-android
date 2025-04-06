package ru.lonelywh1te.introgym.data.db

import ru.lonelywh1te.introgym.core.result.Error

// TODO: Уточнить список ошибок базы данных

enum class DatabaseError: Error {
    SQLITE_ERROR,
}

fun DatabaseError.toStringRes() = when(this) {
    DatabaseError.SQLITE_ERROR -> "Произошла ошибка базы данных приложения."
}