package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_goal_settings")
data class UserGoalSettings(
    @PrimaryKey val id: Int = 1,
    val minimumGoal: Int = 6000,
    val targetGoal: Int = 10000,
    val stretchGoal: Int = 12000,
    val adaptiveGoalsEnabled: Boolean = true,
    val lockedGoalEnabled: Boolean = false,
    val restDays: String = "Sunday", // Comma-separated day names e.g. "Sunday"
    val recoveryModeEnabled: Boolean = false,
    val recoveryEndDate: String? = null,
    val weekendAdjustmentEnabled: Boolean = false,
    val weekendTargetGoal: Int = 8000,
    val carryingMode: String = "Pocket",
    val primaryDataSource: String = "Auto (Health Connect + Sensor)"
)
