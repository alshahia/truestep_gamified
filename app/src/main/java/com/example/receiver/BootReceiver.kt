package com.example.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.UserManager
import androidx.core.content.ContextCompat
import com.example.data.di.ServiceLocator
import com.example.service.StepCounterForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_USER_UNLOCKED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON" -> {
                // Safeguard for Direct Boot: CE storage is locked until user unlocks device
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val userManager = context.getSystemService(Context.USER_SERVICE) as? UserManager
                    if (userManager != null && !userManager.isUserUnlocked) {
                        return
                    }
                }

                val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    true
                }

                if (hasPermission) {
                    val pendingResult = goAsync()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val userPrefs = ServiceLocator.provideUserPreferencesRepository(context)
                            val hasCompletedOnboarding = userPrefs.hasCompletedOnboarding.firstOrNull() ?: false
                            val isTrackingPaused = userPrefs.isTrackingPausedManually.firstOrNull() ?: false
                            if (hasCompletedOnboarding && !isTrackingPaused) {
                                StepCounterForegroundService.start(context)
                            }
                        } catch (e: Exception) {
                            // Catch ForegroundServiceStartNotAllowedException or storage access issues
                        } finally {
                            try {
                                pendingResult.finish()
                            } catch (_: Exception) {}
                        }
                    }
                }
            }
        }
    }
}
