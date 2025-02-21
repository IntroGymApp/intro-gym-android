package ru.lonelywh1te.introgym.core.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.dsl.module
import ru.lonelywh1te.introgym.core.db.MainDatabase

private const val SHARED_PREFERENCES_NAME = "intro_gym_shared_prefs"

val coreModule = module {

    single<MainDatabase> {
        MainDatabase.instance(context = get())
    }

    single<SharedPreferences> {
        get<Context>().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

}