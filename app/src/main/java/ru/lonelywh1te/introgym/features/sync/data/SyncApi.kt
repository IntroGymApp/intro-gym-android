package ru.lonelywh1te.introgym.features.sync.data

import retrofit2.Response
import retrofit2.http.Body

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.lonelywh1te.introgym.features.sync.data.dto.SyncDataDto

interface SyncApi {

    @GET("sync")
    suspend fun fetchServerChanges(
        @Query("lastSynchronization") lastSyncDateTime: String
    ): Response<SyncDataDto>

    @POST("sync")
    suspend fun syncClientData(
        @Body clientData: SyncDataDto,
    ): Response<Unit>

}