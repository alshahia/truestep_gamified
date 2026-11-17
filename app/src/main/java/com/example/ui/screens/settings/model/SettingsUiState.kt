package com.example.ui.screens.settings.model

import com.example.data.local.UserGoalSettings
import com.example.sensor.SensorMode
import com.example.ui.theme.DarkModeConfig
import com.example.ui.theme.ThemeChoice
import com.example.util.AppLanguage

/**
 * Isolated, low-frequency state slice specifically for Settings screens.
 * Decoupled from real-time step increments and timestamps to prevent recomposition thrashing.
 */
data class SettingsUiState(
    val selectedTheme: ThemeChoice = ThemeChoice.CYBERNETIC_STRIDE,
    val darkModeConfig: DarkModeConfig = DarkModeConfig.SYSTEM,
    val selectedLanguage: AppLanguage = AppLanguage.SYSTEM,
    val userGoalSettings: UserGoalSettings? = null,
    val activeSensorMode: SensorMode = SensorMode.HARDWARE_STEP_COUNTER,
    val isSensorAvailable: Boolean = true,
    val isHealthConnectSyncing: Boolean = false,
    val isLiveNotificationEnabled: Boolean = true,
    val isHighContrastEnabled: Boolean = false
)
