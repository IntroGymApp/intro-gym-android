package ru.lonelywh1te.introgym.features.profile.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.prefs.user.UserInfo

interface UserInfoRepository {
    fun getUserInfo(): Flow<Result<UserInfo>>
}