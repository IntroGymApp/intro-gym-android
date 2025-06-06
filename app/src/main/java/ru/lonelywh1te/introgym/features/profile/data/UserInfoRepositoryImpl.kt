package ru.lonelywh1te.introgym.features.profile.data

import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import ru.lonelywh1te.introgym.features.profile.domain.UserInfo
import ru.lonelywh1te.introgym.features.profile.domain.repository.UserInfoRepository

class UserInfoRepositoryImpl(
    private val userPreferences: UserPreferences,
    private val workoutDao: WorkoutDao,
): UserInfoRepository {

    // TODO: register date будет приходить с сервера
    override suspend fun getUserInfo(): UserInfo {
        return UserInfo(
            name = userPreferences.username,
            registerDate = null,
            countOfWorkouts = workoutDao.getCountOfWorkouts(),
        )
    }

}