package com.example.ui.screens.settings.model

import android.content.Context
import com.example.ui.theme.DarkModeConfig
import com.example.ui.theme.ThemeChoice
import com.example.util.AppLanguage

/**
 * Unified action callback container for all settings operations.
 * Eliminates constructor parameter drilling while preserving individual lambda handlers.
 */
data class SettingsActions(
    val onSelectTheme: (ThemeChoice) -> Unit = {},
    val onSelectDarkMode: (DarkModeConfig) -> Unit = {},
    val onSelectLanguage: (AppLanguage) -> Unit = {},
    val onSelectCarryingMode: (String) -> Unit = {},
    val onToggleAdaptiveGoals: (Boolean) -> Unit = {},
    val onToggleLockGoals: (Boolean) -> Unit = {},
    val onToggleRecoveryMode: (Boolean) -> Unit = {},
    val onRecalculateGoal: () -> Unit = {},
    val onToggleLiveNotification: (Boolean) -> Unit = {},
    val onToggleHighContrast: (Boolean) -> Unit = {},
    val onSyncHealthConnect: (Context) -> Unit = {},
    val onExportData: (Context, Boolean) -> Unit = { _, _ -> },
    val onClearAllData: () -> Unit = {},
    val onLoadDemoData: () -> Unit = {},
    val onOpenTrustCenter: () -> Unit = {},
    val onReplayOnboarding: () -> Unit = {}
)
