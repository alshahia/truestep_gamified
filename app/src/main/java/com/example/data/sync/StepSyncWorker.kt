package com.example.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.di.ServiceLocator
import kotlinx.coroutines.flow.firstOrNull

class StepSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val repository = ServiceLocator.provideStepRepository(applicationContext)

            // 1. Prune high-frequency source records older than 14 days to keep database fast & small
            val fourteenDaysMillis = 14L * 24L * 60L * 60L * 1000L
            repository.pruneOldSourceRecords(fourteenDaysMillis)

            // 2. Real Health Connect synchronization if permission is active
            repository.syncHealthConnectSteps(applicationContext)

            // 3. Evaluate streak continuity
            repository.evaluateStreakContinuity()

            // 4. Adaptive goals periodic check
            val today = repository.getTodaySteps().firstOrNull()
            if (today != null) {
                val settings = repository.getUserGoalSettings().firstOrNull()
                if (settings?.adaptiveGoalsEnabled == true && !settings.lockedGoalEnabled) {
                    repository.recalculateAdaptiveGoal()
                }
            }

            // 5. Watchdog: resurrect step counting service if killed by low memory or OS freeze
            try {
                val userPrefs = ServiceLocator.provideUserPreferencesRepository(applicationContext)
                val hasCompletedOnboarding = userPrefs.hasCompletedOnboarding.firstOrNull() ?: false
                val isTrackingPaused = userPrefs.isTrackingPausedManually.firstOrNull() ?: false
                val hasPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    androidx.core.content.ContextCompat.checkSelfPermission(
                        applicationContext,
                        android.Manifest.permission.ACTIVITY_RECOGNITION
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                } else true

                if (hasCompletedOnboarding && !isTrackingPaused && hasPermission && !com.example.service.StepCounterForegroundService.isServiceRunning) {
                    com.example.service.StepCounterForegroundService.start(applicationContext)
                }
            } catch (_: Exception) {
                // Ignore if foreground service start is restricted by background rules
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
