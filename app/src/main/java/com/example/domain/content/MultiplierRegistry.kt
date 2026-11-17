package com.example.domain.content

import androidx.annotation.StringRes
import com.example.R

object MultiplierRegistry {
    data class MultiplierTier(
        val daysRequired: Int,
        val multiplierLabel: String,
        val title: String,
        val emoji: String,
        @StringRes val titleRes: Int = 0
    )

    val tiers = listOf(
        MultiplierTier(1, "1x", "First Spark", "🌱", titleRes = R.string.streak_tier_1_title),
        MultiplierTier(3, "3x", "Three-Day Momentum", "🔥", titleRes = R.string.streak_tier_3_title),
        MultiplierTier(7, "7x", "Weekly Habit", "⚡", titleRes = R.string.streak_tier_7_title),
        MultiplierTier(13, "13x", "Unstoppable", "⭐", titleRes = R.string.streak_tier_13_title),
        MultiplierTier(21, "21x", "Habit Mastered", "🏆", titleRes = R.string.streak_tier_21_title),
        MultiplierTier(25, "25x", "Quarter Century", "💎", titleRes = R.string.streak_tier_25_title),
        MultiplierTier(36, "36x", "Dedicated Pacer", "🎯", titleRes = R.string.streak_tier_36_title),
        MultiplierTier(50, "50x", "Gold Standard", "🥇", titleRes = R.string.streak_tier_50_title),
        MultiplierTier(60, "60x", "Two-Month Dynamo", "🚀", titleRes = R.string.streak_tier_60_title),
        MultiplierTier(80, "80x", "Iron Stride", "🛡️", titleRes = R.string.streak_tier_80_title),
        MultiplierTier(100, "100x", "Centurion Century", "👑", titleRes = R.string.streak_tier_100_title),
        MultiplierTier(120, "120x", "Living Legend", "🌟", titleRes = R.string.streak_tier_120_title)
    )
}

