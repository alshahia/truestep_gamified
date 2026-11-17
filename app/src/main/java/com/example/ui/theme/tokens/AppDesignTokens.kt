package com.example.ui.theme.tokens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocals for design tokens.
 */
val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }
val LocalAppDimensions = staticCompositionLocalOf { AppDimensions() }
val LocalAppShapes = staticCompositionLocalOf { AppShapes() }

/**
 * Idiomatic MaterialTheme accessors for design tokens:
 * - MaterialTheme.spacing.md
 * - MaterialTheme.dimens.buttonHeight
 * - MaterialTheme.appShapes.large
 */
val MaterialTheme.spacing: AppSpacing
    @Composable
    @ReadOnlyComposable
    get() = LocalAppSpacing.current

val MaterialTheme.dimens: AppDimensions
    @Composable
    @ReadOnlyComposable
    get() = LocalAppDimensions.current

val MaterialTheme.appShapes: AppShapes
    @Composable
    @ReadOnlyComposable
    get() = LocalAppShapes.current
