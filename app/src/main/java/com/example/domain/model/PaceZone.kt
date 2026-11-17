package com.example.domain.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.R

enum class PaceZone(
    @StringRes val titleRes: Int,
    val minSpm: Int,
    val maxSpm: Int,
    val color: Color
) {
    STROLL(
        titleRes = R.string.zone_stroll,
        minSpm = 0,
        maxSpm = 79,
        color = Color(0xFF0288D1) // Light Blue / Sky
    ),
    MODERATE(
        titleRes = R.string.zone_moderate,
        minSpm = 80,
        maxSpm = 99,
        color = Color(0xFF00897B) // Teal
    ),
    BRISK(
        titleRes = R.string.zone_brisk,
        minSpm = 100,
        maxSpm = 119,
        color = Color(0xFF2E7D32) // Emerald Green (Aerobic zone)
    ),
    POWER(
        titleRes = R.string.zone_power,
        minSpm = 120,
        maxSpm = 999,
        color = Color(0xFFE65100) // Deep Orange / Amber
    );

    companion object {
        fun fromSpm(spm: Int): PaceZone {
            return when {
                spm >= 120 -> POWER
                spm >= 100 -> BRISK
                spm >= 80 -> MODERATE
                else -> STROLL
            }
        }
    }
}
