package com.example.domain.content

import com.example.R
import com.example.domain.model.Achievement
import com.example.domain.model.AchievementCategory

object AchievementRegistry {
    val allAchievements: List<Achievement> = listOf(
        // Daily goals
        Achievement("goal_4k", "Gentle Stride", "Reach 4,000 steps in a day", AchievementCategory.DAILY_GOAL, "👟", isUnlocked = false, titleRes = R.string.achievement_goal_4k_title, requirementRes = R.string.achievement_goal_4k_desc),
        Achievement("goal_6k", "Active Flow", "Reach 6,000 steps in a day", AchievementCategory.DAILY_GOAL, "🚶", isUnlocked = false, titleRes = R.string.achievement_goal_6k_title, requirementRes = R.string.achievement_goal_6k_desc),
        Achievement("goal_8k", "Health Pioneer", "Reach 8,000 steps in a day", AchievementCategory.DAILY_GOAL, "🌿", isUnlocked = false, titleRes = R.string.achievement_goal_8k_title, requirementRes = R.string.achievement_goal_8k_desc),
        Achievement("goal_10k", "Classic Ten", "Reach 10,000 steps in a day", AchievementCategory.DAILY_GOAL, "🎯", isUnlocked = false, titleRes = R.string.achievement_goal_10k_title, requirementRes = R.string.achievement_goal_10k_desc),
        Achievement("goal_12k", "Stretch Reached", "Reach 12,000 steps in a day", AchievementCategory.DAILY_GOAL, "🔥", isUnlocked = false, titleRes = R.string.achievement_goal_12k_title, requirementRes = R.string.achievement_goal_12k_desc),
        Achievement("goal_15k", "Peak Walker", "Reach 15,000 steps in a day", AchievementCategory.DAILY_GOAL, "🏔️", isUnlocked = false, titleRes = R.string.achievement_goal_15k_title, requirementRes = R.string.achievement_goal_15k_desc),
        Achievement("goal_20k", "Ultra Stride", "Reach 20,000 steps in a day", AchievementCategory.DAILY_GOAL, "⚡", isUnlocked = false, titleRes = R.string.achievement_goal_20k_title, requirementRes = R.string.achievement_goal_20k_desc),
        Achievement("goal_25k", "Marathon Legend", "Reach 25,000 steps in a day", AchievementCategory.DAILY_GOAL, "👑", isUnlocked = false, titleRes = R.string.achievement_goal_25k_title, requirementRes = R.string.achievement_goal_25k_desc),

        // Active Days
        Achievement("days_1", "Journey Begins", "Complete your first day", AchievementCategory.ACTIVE_DAYS, "🌱", isUnlocked = false, titleRes = R.string.achievement_days_1_title, requirementRes = R.string.achievement_days_1_desc),
        Achievement("days_3", "Building Rhythm", "Record activity for 3 days", AchievementCategory.ACTIVE_DAYS, "📅", isUnlocked = false, titleRes = R.string.achievement_days_3_title, requirementRes = R.string.achievement_days_3_desc),
        Achievement("days_7", "Full Week", "Record activity for 7 days", AchievementCategory.ACTIVE_DAYS, "🗓️", isUnlocked = false, titleRes = R.string.achievement_days_7_title, requirementRes = R.string.achievement_days_7_desc),
        Achievement("days_14", "Fortnight Focus", "Record activity for 14 days", AchievementCategory.ACTIVE_DAYS, "✨", isUnlocked = false, titleRes = R.string.achievement_days_14_title, requirementRes = R.string.achievement_days_14_desc),
        Achievement("days_30", "Month of Movement", "Record activity for 30 days", AchievementCategory.ACTIVE_DAYS, "🌟", isUnlocked = false, titleRes = R.string.achievement_days_30_title, requirementRes = R.string.achievement_days_30_desc),
        Achievement("days_60", "Two-Month Dedication", "Record activity for 60 days", AchievementCategory.ACTIVE_DAYS, "🏅", isUnlocked = false, titleRes = R.string.achievement_days_60_title, requirementRes = R.string.achievement_days_60_desc),
        Achievement("days_90", "Quarter Year Pioneer", "Record activity for 90 days", AchievementCategory.ACTIVE_DAYS, "💎", isUnlocked = false, titleRes = R.string.achievement_days_90_title, requirementRes = R.string.achievement_days_90_desc),
        Achievement("days_180", "Half Year Hero", "Record activity for 180 days", AchievementCategory.ACTIVE_DAYS, "🏆", isUnlocked = false, titleRes = R.string.achievement_days_180_title, requirementRes = R.string.achievement_days_180_desc)
    )

    fun getAchievementById(id: String): Achievement? {
        return allAchievements.find { it.id == id }
    }
}

