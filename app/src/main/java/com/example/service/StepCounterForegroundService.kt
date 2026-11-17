package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.ServiceCompat
import com.example.data.di.ServiceLocator
import com.example.domain.repository.StepRepository
import com.example.sensor.StepSensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate

class StepCounterForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var sensorManager: StepSensorManager
    private lateinit var repository: StepRepository
    private lateinit var userPreferencesRepository: com.example.data.preferences.UserPreferencesRepository
    private var wakeLock: PowerManager.WakeLock? = null

    private val stepChannel = Channel<Pair<Int, Int>>(Channel.UNLIMITED)
    private var lastRecordedDate: LocalDate = LocalDate.now()
    private var cachedTodaySteps: Int = 0
    private var cachedGoal: Int = 10000

    private val stepDeltaCallback: (Int, Int) -> Unit = { delta, rawValue ->
        if (delta > 0 && !isTrackingPaused) {
            stepChannel.trySend(Pair(delta, rawValue))
        }
    }

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        StepNotificationBuilder.createNotificationChannel(this)

        val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
        wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StepTrack::ForegroundServiceLock")

        sensorManager = ServiceLocator.provideStepSensorManager(this)
        repository = ServiceLocator.provideStepRepository(this)
        userPreferencesRepository = ServiceLocator.provideUserPreferencesRepository(this)

        startForegroundWithNotification()

        serviceScope.launch {
            val isPaused = userPreferencesRepository.isTrackingPausedManually.firstOrNull() ?: false
            isTrackingPaused = isPaused
            if (isPaused) {
                sensorManager.pause()
            }
            val todayRecord = repository.getTodaySteps().firstOrNull()
            cachedTodaySteps = todayRecord?.totalSteps ?: 0
            cachedGoal = todayRecord?.goalSteps ?: 10000
            todayRecord?.lastSensorValue?.let {
                if (it > 0) sensorManager.setBaseline(it)
            }
            updateNotification()
        }

        // Sequential consumer: processes step events instantly in real-time
        serviceScope.launch {
            for ((delta, raw) in stepChannel) {
                // Hold partial wake lock briefly so CPU does not suspend during database flush
                try {
                    wakeLock?.acquire(3_000L)
                } catch (_: Exception) {}

                try {
                    var totalDelta = delta
                    var latestRaw = raw
                    // Drain any items queued during rapid succession (burst walk/run)
                    while (true) {
                        val next = stepChannel.tryReceive().getOrNull() ?: break
                        totalDelta += next.first
                        latestRaw = next.second
                    }
                    cachedTodaySteps += totalDelta
                    val source = sensorManager.getActiveSourceDescription()
                    repository.recordStepsDelta(totalDelta, latestRaw, source)
                    updateNotification()
                } finally {
                    try {
                        if (wakeLock?.isHeld == true) {
                            wakeLock?.release()
                        }
                    } catch (_: Exception) {}
                }
            }
        }

        sensorManager.addStepDeltaListener(stepDeltaCallback)
        sensorManager.startListening()

        // Periodic safety loop: midnight rollover and periodic notification refresh
        serviceScope.launch {
            while (isActive) {
                delay(15_000L)
                checkMidnightRollover()
            }
        }
    }

    private var isTrackingPaused: Boolean = false

    private fun startForegroundWithNotification() {
        val notification = StepNotificationBuilder.buildNotification(this, cachedTodaySteps, cachedGoal, isTrackingPaused)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceCompat.startForeground(
                    this,
                    StepNotificationBuilder.NOTIFICATION_ID,
                    notification,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                    } else {
                        0
                    }
                )
            } else {
                startForeground(StepNotificationBuilder.NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            // If foreground service cannot start due to permissions, stop service cleanly
            stopSelf()
        }
    }

    private fun checkMidnightRollover() {
        val today = LocalDate.now()
        if (today != lastRecordedDate) {
            lastRecordedDate = today
            cachedTodaySteps = 0
            sensorManager.resetSession()
            updateNotification()
        }
    }

    private fun updateNotification() {
        val notification = StepNotificationBuilder.buildNotification(this, cachedTodaySteps, cachedGoal, isTrackingPaused)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.notify(StepNotificationBuilder.NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_PAUSE -> {
                isTrackingPaused = true
                sensorManager.pause()
                updateNotification()
                serviceScope.launch {
                    userPreferencesRepository.setTrackingPausedManually(true)
                }
            }
            ACTION_RESUME -> {
                isTrackingPaused = false
                sensorManager.resume()
                updateNotification()
                serviceScope.launch {
                    userPreferencesRepository.setTrackingPausedManually(false)
                }
            }
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // If swiped away from Recents by user/system, schedule immediate resurrection alarm
        if (!isTrackingPaused) {
            try {
                val restartServiceIntent = Intent(applicationContext, StepCounterForegroundService::class.java)
                val restartPendingIntent = PendingIntent.getService(
                    applicationContext,
                    999,
                    restartServiceIntent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                alarmManager?.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 1500L,
                    restartPendingIntent
                )
            } catch (_: Exception) {}
        }
    }

    override fun onDestroy() {
        isServiceRunning = false
        sensorManager.removeStepDeltaListener(stepDeltaCallback)
        stepChannel.close()
        sensorManager.stopListening()
        serviceScope.cancel()
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (_: Exception) {}
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_STOP_SERVICE = "com.example.steptrack.action.STOP_SERVICE"
        const val ACTION_PAUSE = "com.example.steptrack.action.PAUSE"
        const val ACTION_RESUME = "com.example.steptrack.action.RESUME"

        @Volatile
        var isServiceRunning: Boolean = false
            private set

        fun start(context: Context) {
            try {
                val intent = Intent(context, StepCounterForegroundService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {
                // Guard against ForegroundServiceStartNotAllowedException or SecurityException
            }
        }

        fun pause(context: Context) {
            try {
                val intent = Intent(context, StepCounterForegroundService::class.java).apply {
                    action = ACTION_PAUSE
                }
                context.startService(intent)
            } catch (_: Exception) {}
        }

        fun resume(context: Context) {
            try {
                val intent = Intent(context, StepCounterForegroundService::class.java).apply {
                    action = ACTION_RESUME
                }
                context.startService(intent)
            } catch (_: Exception) {}
        }

        fun stop(context: Context) {
            val intent = Intent(context, StepCounterForegroundService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            context.startService(intent)
        }
    }
}
