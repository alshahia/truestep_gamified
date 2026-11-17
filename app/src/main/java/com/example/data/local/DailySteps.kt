package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_steps")
data class DailySteps(
    @PrimaryKey val date: String, // "YYYY-MM-DD"
    val totalSteps: Int = 0,
    val goalSteps: Int = 10000,
    val minGoal: Int = 6000,
    val stretchGoal: Int = 12000,
    val lastSensorValue: Int = 0,
    val cumulativeKm: Double = 0.0,
    val activeMinutes: Int = 0,
    val caloriesKcal: Int = 0,
    val isRestDay: Boolean = false,
    val isRecoveryDay: Boolean = false,
    val dataSource: String = "Phone Hardware Sensor",
    val confidence: String = "HIGH", // HIGH, MEDIUM, LOW
    val notes: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
