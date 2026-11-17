package com.example

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.sync.StepSyncWorker
import java.util.concurrent.TimeUnit

class StepTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleSyncWorker()
    }

    private fun scheduleSyncWorker() {
        try {
            val syncRequest = PeriodicWorkRequestBuilder<StepSyncWorker>(15, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "StepTrackSyncWork",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        } catch (_: Exception) {
            // Gracefully handles test environments (Robolectric) where WorkManager is not initialized
        }
    }
}
