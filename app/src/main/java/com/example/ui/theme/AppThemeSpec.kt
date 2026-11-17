package com.example.ui.theme

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush

val LocalAppThemeChoice = compositionLocalOf { ThemeChoice.CYBERNETIC_STRIDE }
val LocalAppThemeSpec = compositionLocalOf { ThemeRegistry.getThemeSpec(ThemeChoice.CYBERNETIC_STRIDE) }

val MaterialTheme.themeSpec: AppThemeSpec
    @Composable
    @ReadOnlyComposable
    get() = LocalAppThemeSpec.current

data class AppThemeSpec(
    val id: ThemeChoice,
    val displayName: String,
    val lightColors: ColorScheme,
    val darkColors: ColorScheme,
    val typography: Typography = Typography,
    val shapes: Shapes,
    val homeProgressGradient: Brush? = null,
    val historyProgressGradient: Brush? = null,
    val background: (@Composable BoxScope.() -> Unit)? = null,
    val darkBackground: (@Composable BoxScope.() -> Unit)? = null
)
