package com.example.ui.theme.tokens

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Universal corner radius and surface contour tokens.
 * Zero-hardcoding contract: All surface shapes, dialogs, cards, chips, and sheets
 * MUST consume these tokens rather than raw RoundedCornerShape literals.
 */
@Immutable
data class AppShapes(
    val none: Shape = RoundedCornerShape(0.dp),
    val extraSmall: Shape = RoundedCornerShape(4.dp),
    val small: Shape = RoundedCornerShape(8.dp),
    val medium: Shape = RoundedCornerShape(12.dp),
    val large: Shape = RoundedCornerShape(16.dp),
    val extraLarge: Shape = RoundedCornerShape(24.dp),
    val bottomSheet: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    val pill: Shape = CircleShape
)
