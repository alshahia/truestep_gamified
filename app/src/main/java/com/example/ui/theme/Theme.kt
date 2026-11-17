package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

import com.example.ui.theme.tokens.AppDimensions
import com.example.ui.theme.tokens.AppShapes
import com.example.ui.theme.tokens.AppSpacing
import com.example.ui.theme.tokens.LocalAppDimensions
import com.example.ui.theme.tokens.LocalAppShapes
import com.example.ui.theme.tokens.LocalAppSpacing

@Composable
fun StepTrackTheme(
    themeChoice: ThemeChoice = ThemeChoice.CYBERNETIC_STRIDE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    spacing: AppSpacing = AppSpacing(),
    dimensions: AppDimensions = AppDimensions(),
    shapes: AppShapes = AppShapes(),
    content: @Composable () -> Unit
) {
    val spec = ThemeRegistry.getThemeSpec(themeChoice)
    val colorScheme = if (darkTheme) spec.darkColors else spec.lightColors

    CompositionLocalProvider(
        LocalAppThemeChoice provides themeChoice,
        LocalAppThemeSpec provides spec,
        LocalAppSpacing provides spacing,
        LocalAppDimensions provides dimensions,
        LocalAppShapes provides shapes
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = spec.typography,
            shapes = spec.shapes
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = colorScheme.background,
                contentColor = colorScheme.onBackground
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (darkTheme) {
                        spec.darkBackground?.invoke(this)
                    } else {
                        spec.background?.invoke(this)
                    }
                    content()
                }
            }
        }
    }
}

// Retain alias for any legacy references
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    StepTrackTheme(
        themeChoice = ThemeChoice.CYBERNETIC_STRIDE,
        darkTheme = darkTheme,
        content = content
    )
}
