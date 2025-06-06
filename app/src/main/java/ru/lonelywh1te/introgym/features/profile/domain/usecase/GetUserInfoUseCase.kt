package ru.lonelywh1te.introgym.features.profile.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.lonelywh1te.introgym.core.result.Result
import ru.lonelywh1te.introgym.data.prefs.user.UserInfo
import ru.lonelywh1te.introgym.features.profile.domain.repository.UserInfoRepository

class GetUserInfoUseCase(
    private val repository: UserInfoRepository,
) {
    operator fun invoke(): Flow<Result<UserInfo>> {
        return repository.getUserInfo()
    }
}