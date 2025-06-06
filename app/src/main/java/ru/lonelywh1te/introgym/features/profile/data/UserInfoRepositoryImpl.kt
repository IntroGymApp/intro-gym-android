package ru.lonelywh1te.introgym.features.profile.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.network.NetworkError
import ru.lonelywh1te.introgym.data.network.asSafeNetworkFlow
import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import ru.lonelywh1te.introgym.data.prefs.user.UserInfo
import ru.lonelywh1te.introgym.features.profile.domain.repository.UserInfoRepository
import java.time.Instant
import java.time.ZoneId

class UserInfoRepositoryImpl(
    private val userPreferences: UserPreferences,
    private val profileApi: ProfileApi,
): UserInfoRepository {
    override fun getUserInfo(): Flow<Result<UserInfo>> = flow {
        emit(Result.Loading)

        val localUserInfo = userPreferences.getUserInfo()

        if (localUserInfo != null) {
            emit(Result.Success(localUserInfo))
        } else {
            val response = profileApi.getUserInfo()
            val body = response.body()

            val result = when {
                response.isSuccessful && body != null -> {
                    val remoteUserInfo = UserInfo(
                        name = body.username,
                        registerDate = Instant.parse(body.joinedAt).atZone(ZoneId.systemDefault()).toLocalDate(),
                    )

                    userPreferences.saveUserInfo(remoteUserInfo)

                    Result.Success(remoteUserInfo)
                }
                else -> Result.Failure(NetworkError.Unknown(message = response.message()))
            }

            emit(result)
        }
    }.asSafeNetworkFlow()

}