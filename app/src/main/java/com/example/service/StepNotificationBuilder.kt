package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object StepNotificationBuilder {
    const val CHANNEL_ID = "step_counter_channel"
    const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                setShowBadge(false)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun buildNotification(context: Context, steps: Int, goal: Int, isPaused: Boolean = false): Notification {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseResumeIntent = Intent(context, StepCounterForegroundService::class.java).apply {
            action = if (isPaused) StepCounterForegroundService.ACTION_RESUME else StepCounterForegroundService.ACTION_PAUSE
        }
        val pauseResumePendingIntent = PendingIntent.getService(
            context,
            1,
            pauseResumeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val progressPercent = if (goal > 0) ((steps.toFloat() / goal) * 100).toInt().coerceAtMost(100) else 0
        val percentText = context.getString(R.string.percent_of_goal_format, progressPercent)
        val text = if (isPaused) {
            context.getString(R.string.tracking_paused)
        } else {
            context.getString(
                R.string.notification_text_format,
                steps,
                percentText
            )
        }

        val actionTitle = context.getString(if (isPaused) R.string.resume else R.string.pause)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_notification_steps)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .addAction(0, actionTitle, pauseResumePendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }
}
