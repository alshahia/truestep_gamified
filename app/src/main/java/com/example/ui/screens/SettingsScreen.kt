package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.screens.settings.components.ThemeSelectionSheet
import com.example.ui.screens.settings.model.SettingsActions
import com.example.ui.screens.settings.model.SettingsUiState
import com.example.ui.screens.settings.sections.AccessibilitySettingsSection
import com.example.ui.screens.settings.sections.AppearanceSettingsSection
import com.example.ui.screens.settings.sections.GoalsSettingsSection
import com.example.ui.screens.settings.sections.NotificationSettingsSection
import com.example.ui.screens.settings.sections.PrivacyDataSettingsSection
import com.example.ui.screens.settings.sections.TrackingSettingsSection
import com.example.ui.screens.settings.sections.TroubleshootingAboutSection
import com.example.ui.theme.DarkModeConfig
import com.example.ui.theme.ThemeChoice
import com.example.ui.viewmodel.StepUiState
import com.example.util.AppLanguage

/**
 * Root modular SettingsScreen orchestrator.
 * Renders categorized Material 3 cards with decoupled low-frequency state.
 */
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    actions: SettingsActions,
    modifier: Modifier = Modifier
) {
    var showThemeSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Appearance & Handcrafted Themes
        AppearanceSettingsSection(
            selectedTheme = uiState.selectedTheme,
            darkModeConfig = uiState.darkModeConfig,
            selectedLanguage = uiState.selectedLanguage,
            onOpenThemeSheet = { showThemeSheet = true },
            onSelectDarkMode = actions.onSelectDarkMode,
            onSelectLanguage = actions.onSelectLanguage
        )

        // Section 2: Sensor Telemetry & Carrying Mode
        TrackingSettingsSection(
            activeSensorMode = uiState.activeSensorMode,
            isSensorAvailable = uiState.isSensorAvailable,
            currentCarryingMode = uiState.userGoalSettings?.carryingMode ?: "Pocket",
            onSelectCarryingMode = actions.onSelectCarryingMode
        )

        // Section 3: Adaptive Goals, Streaks & Rest Days
        GoalsSettingsSection(
            userGoalSettings = uiState.userGoalSettings,
            onToggleAdaptiveGoals = actions.onToggleAdaptiveGoals,
            onToggleLockGoals = actions.onToggleLockGoals,
            onToggleRecoveryMode = actions.onToggleRecoveryMode,
            onRecalculateGoal = actions.onRecalculateGoal
        )

        // Section 4: Live Notifications & Background Tracking
        NotificationSettingsSection(
            isLiveNotificationEnabled = uiState.isLiveNotificationEnabled,
            onToggleLiveNotification = actions.onToggleLiveNotification
        )

        // Section 5: Accessibility & Display
        AccessibilitySettingsSection(
            isHighContrastEnabled = uiState.isHighContrastEnabled,
            onToggleHighContrast = actions.onToggleHighContrast
        )

        // Section 6: Privacy, Data Export & Control
        PrivacyDataSettingsSection(
            onExportData = actions.onExportData,
            onClearAllData = actions.onClearAllData,
            onLoadDemoData = actions.onLoadDemoData,
            onOpenTrustCenter = actions.onOpenTrustCenter
        )

        // Section 7: Diagnostics, Battery Health & About
        TroubleshootingAboutSection(
            isHealthConnectSyncing = uiState.isHealthConnectSyncing,
            onSyncHealthConnect = actions.onSyncHealthConnect,
            onReplayOnboarding = actions.onReplayOnboarding
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showThemeSheet) {
        ThemeSelectionSheet(
            selectedTheme = uiState.selectedTheme,
            onSelectTheme = actions.onSelectTheme,
            onDismissRequest = { showThemeSheet = false }
        )
    }
}

/**
 * Backward-compatible overload accepting StepUiState and individual callback handlers.
 */
@Composable
fun SettingsScreen(
    uiState: StepUiState,
    onSelectTheme: (ThemeChoice) -> Unit,
    onSelectDarkMode: (DarkModeConfig) -> Unit = {},
    onSelectLanguage: (AppLanguage) -> Unit = {},
    onSelectCarryingMode: (String) -> Unit,
    onToggleAdaptiveGoals: (Boolean) -> Unit,
    onToggleLockGoals: (Boolean) -> Unit,
    onToggleRecoveryMode: (Boolean) -> Unit,
    onRecalculateGoal: () -> Unit,
    onSyncHealthConnect: (Context) -> Unit = {},
    onExportData: (Context, Boolean) -> Unit,
    onClearAllData: () -> Unit,
    onLoadDemoData: () -> Unit,
    onToggleLiveNotification: (Boolean) -> Unit = {},
    onToggleHighContrast: (Boolean) -> Unit = {},
    onOpenTrustCenter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val settingsState = SettingsUiState(
        selectedTheme = uiState.selectedTheme,
        darkModeConfig = uiState.darkModeConfig,
        selectedLanguage = uiState.selectedLanguage,
        userGoalSettings = uiState.userGoalSettings,
        activeSensorMode = uiState.activeSensorMode,
        isSensorAvailable = uiState.isSensorAvailable,
        isHealthConnectSyncing = uiState.isHealthConnectSyncing,
        isLiveNotificationEnabled = uiState.isLiveNotificationEnabled,
        isHighContrastEnabled = uiState.isHighContrastEnabled
    )

    val actions = SettingsActions(
        onSelectTheme = onSelectTheme,
        onSelectDarkMode = onSelectDarkMode,
        onSelectLanguage = onSelectLanguage,
        onSelectCarryingMode = onSelectCarryingMode,
        onToggleAdaptiveGoals = onToggleAdaptiveGoals,
        onToggleLockGoals = onToggleLockGoals,
        onToggleRecoveryMode = onToggleRecoveryMode,
        onRecalculateGoal = onRecalculateGoal,
        onSyncHealthConnect = onSyncHealthConnect,
        onExportData = onExportData,
        onClearAllData = onClearAllData,
        onLoadDemoData = onLoadDemoData,
        onToggleLiveNotification = onToggleLiveNotification,
        onToggleHighContrast = onToggleHighContrast,
        onOpenTrustCenter = onOpenTrustCenter
    )

    SettingsScreen(
        uiState = settingsState,
        actions = actions,
        modifier = modifier
    )
}
