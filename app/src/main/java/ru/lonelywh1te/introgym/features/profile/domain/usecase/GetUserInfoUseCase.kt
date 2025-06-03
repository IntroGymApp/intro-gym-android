package ru.lonelywh1te.introgym.features.profile.domain.usecase

import ru.lonelywh1te.introgym.features.profile.domain.UserInfo
import ru.lonelywh1te.introgym.features.profile.domain.repository.UserInfoRepository
import java.time.LocalDate

class GetUserInfoUseCase(
    private val repository: UserInfoRepository,
) {
    // TODO: исправить имя
    suspend operator fun invoke(): UserInfo {
        val userInfo = repository.getUserInfo()

        return userInfo.copy(
            name = userInfo.name ?: "Пользователь",
        )
    }
}