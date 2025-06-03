package ru.lonelywh1te.introgym.features.profile.domain.repository

import ru.lonelywh1te.introgym.features.profile.domain.UserInfo

interface UserInfoRepository {
    suspend fun getUserInfo(): UserInfo
}