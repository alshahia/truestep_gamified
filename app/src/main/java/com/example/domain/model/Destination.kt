package com.example.domain.model

import androidx.annotation.StringRes

data class Destination(
    val id: String,
    val name: String,
    val kmRequired: Double,
    val emoji: String,
    val description: String,
    @StringRes val nameRes: Int = 0,
    @StringRes val descriptionRes: Int = 0,
    val rankGrade: String = "B-RANK",
    val chamberTitle: String = "Chamber 1/4: The Depths",
    val primaryLootEmoji: String = "⚙️",
    val primaryLootName: String = "Legendary Core (S)",
    val secondaryLootName: String = "Arcane Data Chip",
    val arcadeScoreBonus: Int = 785300
)

data class Achievement(
    val id: String,
    val title: String,
    val requirement: String,
    val category: AchievementCategory,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    @StringRes val titleRes: Int = 0,
    @StringRes val requirementRes: Int = 0
)

enum class AchievementCategory {
    DAILY_GOAL,
    STREAK,
    ACTIVE_DAYS,
    DESTINATION
}
