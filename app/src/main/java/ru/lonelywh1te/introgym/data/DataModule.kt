package ru.lonelywh1te.introgym.data

import androidx.room.RoomDatabase
import org.koin.dsl.module
import ru.lonelywh1te.introgym.data.db.MainDatabase
import ru.lonelywh1te.introgym.data.db.dao.ExerciseCategoryDao
import ru.lonelywh1te.introgym.data.db.dao.ExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.ExerciseSetDao
import ru.lonelywh1te.introgym.data.db.dao.TagDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutLogDao
import ru.lonelywh1te.introgym.data.prefs.LaunchPreferences
import ru.lonelywh1te.introgym.data.prefs.SettingsPreferences
import ru.lonelywh1te.introgym.data.prefs.UserPreferences
import ru.lonelywh1te.introgym.data.prefs.launch.LaunchPreferencesImpl
import ru.lonelywh1te.introgym.data.prefs.settings.SettingsPreferencesImpl
import ru.lonelywh1te.introgym.data.prefs.user.UserPreferencesImpl

val dataModule = module {
    single<RoomDatabase> {
        get<MainDatabase>()
    }

    single<MainDatabase> {
        MainDatabase.instance(context = get())
    }



    factory<ExerciseCategoryDao> {
        get<MainDatabase>().exerciseCategoryDao()
    }

    factory<ExerciseDao> {
        get<MainDatabase>().exerciseDao()
    }

    factory<ExerciseSetDao> {
        get<MainDatabase>().exerciseSetDao()
    }

    factory<TagDao> {
        get<MainDatabase>().tagDao()
    }

    factory<WorkoutDao> {
        get<MainDatabase>().workoutDao()
    }

    factory<WorkoutExerciseDao> {
        get<MainDatabase>().workoutExerciseDao()
    }

    factory<WorkoutExercisePlanDao> {
        get<MainDatabase>().workoutExercisePlanDao()
    }

    factory<WorkoutLogDao> {
        get<MainDatabase>().workoutLogDao()
    }



    single<UserPreferences> {
        UserPreferencesImpl(context = get())
    }

    single<SettingsPreferences> {
        SettingsPreferencesImpl(context = get())
    }

    single<LaunchPreferences> {
        LaunchPreferencesImpl(context = get())
    }
}