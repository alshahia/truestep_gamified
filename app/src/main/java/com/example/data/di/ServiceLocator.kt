package com.example.data.di

import android.content.Context
import com.example.data.local.StepDatabase
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.StepRepositoryImpl
import com.example.domain.repository.StepRepository
import com.example.sensor.StepSensorManager

object ServiceLocator {
    @Volatile
    private var database: StepDatabase? = null

    @Volatile
    private var stepRepository: StepRepository? = null

    @Volatile
    private var stepSensorManager: StepSensorManager? = null

    @Volatile
    private var userPreferencesRepository: UserPreferencesRepository? = null

    fun provideDatabase(context: Context): StepDatabase {
        return database ?: synchronized(this) {
            val db = StepDatabase.getInstance(context.applicationContext)
            database = db
            db
        }
    }

    fun provideStepRepository(context: Context): StepRepository {
        return stepRepository ?: synchronized(this) {
            val db = provideDatabase(context)
            val repo = StepRepositoryImpl(
                stepDao = db.stepDao(),
                hourlyStepDao = db.hourlyStepDao(),
                goalStreakDao = db.goalStreakDao(),
                achievementDao = db.achievementDao(),
                stepSourceRecordDao = db.stepSourceRecordDao(),
                userGoalSettingsDao = db.userGoalSettingsDao(),
                walkingSessionDao = db.walkingSessionDao()
            )
            stepRepository = repo
            repo
        }
    }

    fun provideStepSensorManager(context: Context): StepSensorManager {
        return stepSensorManager ?: synchronized(this) {
            val manager = StepSensorManager(context.applicationContext)
            stepSensorManager = manager
            manager
        }
    }

    fun provideUserPreferencesRepository(context: Context): UserPreferencesRepository {
        return userPreferencesRepository ?: synchronized(this) {
            val repo = UserPreferencesRepository(context.applicationContext)
            userPreferencesRepository = repo
            repo
        }
    }
}
