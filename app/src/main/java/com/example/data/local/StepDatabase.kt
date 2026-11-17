package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        DailySteps::class,
        HourlyStepRecord::class,
        GoalStreakCounter::class,
        AchievementUnlock::class,
        StepSourceRecord::class,
        UserGoalSettings::class,
        WalkingSession::class
    ],
    version = 3,
    exportSchema = false
)
abstract class StepDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao
    abstract fun hourlyStepDao(): HourlyStepDao
    abstract fun goalStreakDao(): GoalStreakDao
    abstract fun achievementDao(): AchievementDao
    abstract fun stepSourceRecordDao(): StepSourceRecordDao
    abstract fun userGoalSettingsDao(): UserGoalSettingsDao
    abstract fun walkingSessionDao(): WalkingSessionDao

    companion object {
        @Volatile
        private var INSTANCE: StepDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `hourly_steps` (
                        `date` TEXT NOT NULL,
                        `hourOfDay` INTEGER NOT NULL,
                        `stepsCount` INTEGER NOT NULL,
                        `lastUpdated` INTEGER NOT NULL,
                        PRIMARY KEY(`date`, `hourOfDay`)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `walking_sessions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `sessionType` TEXT NOT NULL,
                        `startTime` INTEGER NOT NULL,
                        `endTime` INTEGER NOT NULL,
                        `durationSeconds` INTEGER NOT NULL,
                        `steps` INTEGER NOT NULL,
                        `distanceKm` REAL NOT NULL,
                        `notes` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                """.trimIndent())
                db.execSQL("ALTER TABLE user_goal_settings ADD COLUMN weekendAdjustmentEnabled INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE user_goal_settings ADD COLUMN weekendTargetGoal INTEGER NOT NULL DEFAULT 8000")
            }
        }

        fun getInstance(context: Context): StepDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepDatabase::class.java,
                    "steptrack_database.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration(false)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
