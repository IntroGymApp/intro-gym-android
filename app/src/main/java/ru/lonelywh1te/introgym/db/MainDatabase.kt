package ru.lonelywh1te.introgym.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.lonelywh1te.introgym.db.converters.LocalDateConverter
import ru.lonelywh1te.introgym.db.converters.LocalDateTimeConverter
import ru.lonelywh1te.introgym.db.converters.UploadStatusConverter
import ru.lonelywh1te.introgym.db.dao.ExerciseDao
import ru.lonelywh1te.introgym.db.dao.ExerciseSetDao
import ru.lonelywh1te.introgym.db.dao.TagDao
import ru.lonelywh1te.introgym.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.db.dao.WorkoutLogDao
import ru.lonelywh1te.introgym.db.entity.ExerciseEntity
import ru.lonelywh1te.introgym.db.entity.ExerciseSetEntity
import ru.lonelywh1te.introgym.db.entity.TagEntity
import ru.lonelywh1te.introgym.db.entity.TagToExerciseEntity
import ru.lonelywh1te.introgym.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.db.entity.WorkoutExerciseEntity
import ru.lonelywh1te.introgym.db.entity.WorkoutExercisePlanEntity
import ru.lonelywh1te.introgym.db.entity.WorkoutLogEntity

private const val DB_ASSET_PATH = "db/intro-gym-database"

@Database(
    entities = [WorkoutEntity::class, WorkoutLogEntity::class, ExerciseEntity::class, WorkoutExerciseEntity::class, WorkoutExercisePlanEntity::class, ExerciseSetEntity::class, TagEntity::class, TagToExerciseEntity::class],
    version = 1
)
@TypeConverters(LocalDateConverter::class, UploadStatusConverter::class, LocalDateTimeConverter::class)
abstract class MainDatabase: RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao
    abstract fun workoutExercisePlanDao(): WorkoutExercisePlanDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseSetDao(): ExerciseSetDao
    abstract fun tagDao(): TagDao

    companion object {
        private var db: MainDatabase? = null

        fun instance(context: Context): MainDatabase {
            if (db == null) {
                synchronized(MainDatabase::class) {
                    db = Room
                        .databaseBuilder(context, MainDatabase::class.java, "intro-gym-database")
                        .createFromAsset(DB_ASSET_PATH)
                        .build()
                }
            }

            return db!!
        }
    }
}