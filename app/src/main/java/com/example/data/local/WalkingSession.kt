package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "walking_sessions")
data class WalkingSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionType: String = "INDOOR", // "INDOOR", "OUTDOOR_GPS", "OUTDOOR_NO_GPS", "TREADMILL", "MANUAL"
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis(),
    val durationSeconds: Long = 0L,
    val steps: Int = 0,
    val distanceKm: Double = 0.0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
