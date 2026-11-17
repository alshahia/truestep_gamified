package com.example.ui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Universal component dimensions, touch targets, and elevations.
 * Zero-hardcoding contract: All UI element heights, icon sizes, and borders
 * MUST consume these tokens rather than raw dp literals.
 */
@Immutable
data class AppDimensions(
    val minTouchTarget: Dp = 48.dp,
    val buttonHeight: Dp = 48.dp,
    val buttonSmallHeight: Dp = 36.dp,
    val iconSmall: Dp = 16.dp,
    val iconMedium: Dp = 20.dp,
    val iconLarge: Dp = 24.dp,
    val iconHero: Dp = 40.dp,
    val cardBorderWidth: Dp = 1.dp,
    val elevationNone: Dp = 0.dp,
    val elevationLow: Dp = 2.dp,
    val elevationMedium: Dp = 6.dp,
    val scrubberTrackHeight: Dp = 72.dp,
    val indicatorBarWidth: Dp = 14.dp,
    val avatarSize: Dp = 40.dp,
    val badgeSize: Dp = 28.dp,
    val dividerThickness: Dp = 1.dp
)
