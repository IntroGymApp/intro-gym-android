package ru.lonelywh1te.introgym.data

import org.koin.dsl.module
import ru.lonelywh1te.introgym.data.db.MainDatabase
import ru.lonelywh1te.introgym.data.db.dao.ExerciseCategoryDao

private const val SHARED_PREFERENCES_NAME = "intro_gym_shared_prefs"

val dataModule = module {
    single<MainDatabase> {
        MainDatabase.instance(context = get())
    }

    factory<ExerciseCategoryDao> {
        get<MainDatabase>().exerciseCategoryDao()
    }
}