package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.R
import java.text.NumberFormat
import java.util.Locale

/**
 * Universal metric formatter ensuring all numeric values and units
 * adapt to the user's active locale without hardcoding comma separators,
 * decimal points, or unit abbreviations.
 */
object MetricFormatter {

    fun formatSteps(steps: Int, locale: Locale = Locale.getDefault()): String {
        return NumberFormat.getIntegerInstance(locale).format(steps)
    }

    fun formatDistance(km: Float, locale: Locale = Locale.getDefault(), isMiles: Boolean = false): String {
        val value = if (isMiles) km * 0.621371f else km
        val formatter = NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        return formatter.format(value)
    }

    fun formatCalories(calories: Int, locale: Locale = Locale.getDefault()): String {
        return NumberFormat.getIntegerInstance(locale).format(calories)
    }

    fun formatPercentage(percent: Int, locale: Locale = Locale.getDefault()): String {
        return NumberFormat.getIntegerInstance(locale).format(percent) + "%"
    }

    fun formatDuration(totalSeconds: Long, locale: Locale = Locale.getDefault()): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(locale, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(locale, "%02d:%02d", minutes, seconds)
        }
    }

    @Composable
    fun formatStepsWithUnit(steps: Int): String {
        val formattedNumber = formatSteps(steps)
        val unit = stringResource(R.string.steps_unit)
        return "$formattedNumber $unit"
    }

    @Composable
    fun formatDistanceWithUnit(km: Float, isMiles: Boolean = false): String {
        val formattedNumber = formatDistance(km, isMiles = isMiles)
        val unit = if (isMiles) stringResource(R.string.miles_unit) else stringResource(R.string.km_unit)
        return "$formattedNumber $unit"
    }

    @Composable
    fun formatCaloriesWithUnit(calories: Int): String {
        val formattedNumber = formatCalories(calories)
        val unit = stringResource(R.string.kcal_unit)
        return "$formattedNumber $unit"
    }
}
