package com.example.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.domain.model.Achievement
import com.example.domain.model.Destination
import com.example.domain.content.MultiplierRegistry
import com.example.sensor.SensorMode
import com.example.ui.theme.ThemeChoice

/**
 * Scalable localization module for StepTrack.
 * Centralizes string resource resolution for domain models, telemetry, and carrying modes.
 * Ensures zero hardcoded strings across the entire application.
 */
object LocalizationUtil {

    /**
     * Resolves localized title for an Achievement.
     */
    @Composable
    fun getAchievementTitle(achievement: Achievement): String {
        return if (achievement.titleRes != 0) {
            stringResource(achievement.titleRes)
        } else {
            achievement.title
        }
    }

    /**
     * Resolves localized requirement / description for an Achievement.
     */
    @Composable
    fun getAchievementRequirement(achievement: Achievement): String {
        return if (achievement.requirementRes != 0) {
            stringResource(achievement.requirementRes)
        } else {
            achievement.requirement
        }
    }

    /**
     * Resolves localized destination name.
     */
    @Composable
    fun getDestinationName(destination: Destination): String {
        return if (destination.nameRes != 0) {
            stringResource(destination.nameRes)
        } else {
            destination.name
        }
    }

    /**
     * Resolves localized destination description.
     */
    @Composable
    fun getDestinationDescription(destination: Destination): String {
        return if (destination.descriptionRes != 0) {
            stringResource(destination.descriptionRes)
        } else {
            destination.description
        }
    }

    /**
     * Resolves localized streak tier title.
     */
    @Composable
    fun getStreakTierTitle(tier: MultiplierRegistry.MultiplierTier): String {
        return if (tier.titleRes != 0) {
            stringResource(tier.titleRes)
        } else {
            tier.title
        }
    }

    /**
     * Resolves localized theme display name.
     */
    @Composable
    fun getThemeDisplayName(theme: ThemeChoice): String {
        return if (theme.displayNameRes != 0) {
            stringResource(theme.displayNameRes)
        } else {
            theme.displayName
        }
    }

    /**
     * Resolves localized theme description.
     */
    @Composable
    fun getThemeSubtitle(theme: ThemeChoice): String {
        return if (theme.subtitleRes != 0) {
            stringResource(theme.subtitleRes)
        } else {
            theme.subtitle
        }
    }

    /**
     * Resolves localized sensor mode display name.
     */
    @Composable
    fun getSensorModeDisplayName(sensorMode: SensorMode): String {
        return if (sensorMode.displayNameRes != 0) {
            stringResource(sensorMode.displayNameRes)
        } else {
            sensorMode.displayName
        }
    }

    /**
     * Maps carrying mode key to its localized string resource ID.
     */
    @StringRes
    fun getCarryingModeRes(modeKey: String): Int {
        return when (modeKey.lowercase()) {
            "pocket" -> R.string.mode_pocket
            "hand" -> R.string.mode_hand
            "bag" -> R.string.mode_bag
            "backpack" -> R.string.mode_backpack
            "wearable" -> R.string.mode_wearable
            "treadmill" -> R.string.mode_treadmill
            else -> R.string.mode_pocket
        }
    }

    /**
     * Maps confidence level string to localized string resource ID.
     */
    @StringRes
    fun getConfidenceBadgeRes(confidence: String): Int {
        return when (confidence.uppercase()) {
            "HIGH" -> R.string.confidence_badge_high
            "MEDIUM" -> R.string.confidence_badge_reconciled
            else -> R.string.confidence_badge_uncertain
        }
    }
}
