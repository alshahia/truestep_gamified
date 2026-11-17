package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goal_streaks")
data class GoalStreakCounter(
    @PrimaryKey val id: Int = 1,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastAchievedDate: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
