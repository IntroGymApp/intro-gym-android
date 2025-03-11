package ru.lonelywh1te.introgym.data

import org.koin.dsl.module
import ru.lonelywh1te.introgym.data.db.MainDatabase
import ru.lonelywh1te.introgym.data.db.dao.ExerciseCategoryDao
import ru.lonelywh1te.introgym.data.db.dao.ExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.TagDao
import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences
import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import ru.lonelywh1te.introgym.data.prefs.settings.SettingsPreferencesImpl
import ru.lonelywh1te.introgym.data.prefs.user.UserPreferencesImpl

val dataModule = module {
    single<MainDatabase> {
        MainDatabase.instance(context = get())
    }

    factory<ExerciseCategoryDao> {
        get<MainDatabase>().exerciseCategoryDao()
    }

    factory<ExerciseDao> {
        get<MainDatabase>().exerciseDao()
    }

    factory<TagDao> {
        get<MainDatabase>().tagDao()
    }

    single<UserPreferences> {
        UserPreferencesImpl(context = get())
    }

    single<SettingsPreferences> {
        SettingsPreferencesImpl(context = get())
    }
}