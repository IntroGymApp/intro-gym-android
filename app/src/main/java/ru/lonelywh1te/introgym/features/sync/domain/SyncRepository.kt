package ru.lonelywh1te.introgym.features.sync.domain

import ru.lonelywh1te.introgym.core.result.Result

interface SyncRepository {
    suspend fun sync(): Result<Unit>
}