package ru.lonelywh1te.introgym.data.prefs

import ru.lonelywh1te.introgym.data.prefs.user.UserInfo

interface UserPreferences {
    fun getUserInfo(): UserInfo?
    fun saveUserInfo(userInfo: UserInfo)

    fun clear()
}