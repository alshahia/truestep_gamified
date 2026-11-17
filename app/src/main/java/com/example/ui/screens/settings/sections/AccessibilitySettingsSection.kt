package com.example.ui.screens.settings.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.ui.screens.settings.components.SettingsActionRow
import com.example.ui.screens.settings.components.SettingsGroupCard
import com.example.ui.screens.settings.components.SettingsSwitchRow

@Composable
fun AccessibilitySettingsSection(
    isHighContrastEnabled: Boolean,
    onToggleHighContrast: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsGroupCard(
        title = stringResource(R.string.accessibility_options),
        icon = Icons.Default.AccessibilityNew,
        modifier = modifier
    ) {
        // High contrast toggle
        SettingsSwitchRow(
            title = stringResource(R.string.high_contrast_toggle),
            subtitle = stringResource(R.string.accessibility_high_contrast_sub),
            icon = Icons.Default.Contrast,
            checked = isHighContrastEnabled,
            onCheckedChange = onToggleHighContrast,
            testTag = "high_contrast_switch",
            switchTestTag = "high_contrast_switch_control"
        )

        // Accessibility standard information
        SettingsActionRow(
            title = stringResource(R.string.accessibility_scaling_title),
            subtitle = stringResource(R.string.accessibility_scaling_sub),
            icon = Icons.Default.TextFields,
            onClick = {},
            showChevron = false
        )
    }
}
