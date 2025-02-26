package ru.lonelywh1te.introgym.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.lonelywh1te.introgym.data.db.converters.LocalDateConverter
import ru.lonelywh1te.introgym.data.db.converters.LocalDateTimeConverter
import ru.lonelywh1te.introgym.data.db.converters.UploadStatusConverter
import ru.lonelywh1te.introgym.data.db.dao.ExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.ExerciseSetDao
import ru.lonelywh1te.introgym.data.db.dao.TagDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExerciseDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutExercisePlanDao
import ru.lonelywh1te.introgym.data.db.dao.WorkoutLogDao
import ru.lonelywh1te.introgym.data.db.entity.ExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.ExerciseSetEntity
import ru.lonelywh1te.introgym.data.db.entity.TagEntity
import ru.lonelywh1te.introgym.data.db.entity.TagToExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExerciseEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutExercisePlanEntity
import ru.lonelywh1te.introgym.data.db.entity.WorkoutLogEntity


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
        private const val DB_ASSET_PATH = "db/intro-gym-database"

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