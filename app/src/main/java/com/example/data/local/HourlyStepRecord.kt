package com.example.data.local

import androidx.room.Entity

@Entity(
    tableName = "hourly_steps",
    primaryKeys = ["date", "hourOfDay"]
)
data class HourlyStepRecord(
    val date: String, // "YYYY-MM-DD"
    val hourOfDay: Int, // 0..23
    val stepsCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
