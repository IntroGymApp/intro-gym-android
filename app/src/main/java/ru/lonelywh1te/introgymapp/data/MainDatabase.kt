package ru.lonelywh1te.introgymapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.lonelywh1te.introgymapp.data.dao.ExerciseDao
import ru.lonelywh1te.introgymapp.data.dao.WorkoutDao
import ru.lonelywh1te.introgymapp.domain.AssetsPath
import ru.lonelywh1te.introgymapp.domain.model.Exercise
import ru.lonelywh1te.introgymapp.domain.model.ExerciseGroup
import ru.lonelywh1te.introgymapp.domain.model.ExerciseHistory
import ru.lonelywh1te.introgymapp.domain.model.ExerciseInfo
import ru.lonelywh1te.introgymapp.domain.model.Workout

@Database(entities = [ExerciseInfo::class, Workout::class, Exercise::class, ExerciseHistory::class, ExerciseGroup::class], version = 3)
abstract class MainDatabase: RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        private var db: MainDatabase? = null

        fun getInstance(context: Context): MainDatabase {
            if (db == null) {
                synchronized(MainDatabase::class.java) {
                    db = Room.databaseBuilder(context, MainDatabase::class.java, "app_db")
                        .createFromAsset("${AssetsPath.DATABASE}/app_db")
                        .build()
                }
            }

            return db!!
        }
    }
}