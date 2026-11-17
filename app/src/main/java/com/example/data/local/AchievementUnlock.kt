package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievement_unlocks")
data class AchievementUnlock(
    @PrimaryKey val achievementId: String,
    val unlockedAt: Long = System.currentTimeMillis(),
    val isSeen: Boolean = true
)
