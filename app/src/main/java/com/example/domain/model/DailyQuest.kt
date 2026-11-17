package com.example.domain.model

import androidx.annotation.StringRes

data class DailyQuest(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    val iconEmoji: String,
    val targetValue: Int,
    val currentValue: Int,
    val rewardXp: Int,
    val isCompleted: Boolean
) {
    val progressPercent: Float
        get() = if (targetValue > 0) (currentValue.toFloat() / targetValue).coerceIn(0f, 1f) else 0f
}
