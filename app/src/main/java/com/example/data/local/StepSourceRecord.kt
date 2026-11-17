package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_source_records")
data class StepSourceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val sourcePackage: String = "android.hardware.sensor",
    val sourceName: String = "Hardware Step Counter",
    val sourceType: String = "SENSOR", // "HEALTH_CONNECT", "SENSOR", "MANUAL"
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val stepCount: Int = 0,
    val isOverlapping: Boolean = false,
    val isIncluded: Boolean = true,
    val exclusionReason: String? = null,
    val synchronizedAt: Long = System.currentTimeMillis()
)
