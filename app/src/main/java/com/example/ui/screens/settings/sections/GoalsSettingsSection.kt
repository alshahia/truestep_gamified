package com.example.ui.screens.settings.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.local.UserGoalSettings
import com.example.ui.screens.settings.components.SettingsActionRow
import com.example.ui.screens.settings.components.SettingsGroupCard
import com.example.ui.screens.settings.components.SettingsSwitchRow

@Composable
fun GoalsSettingsSection(
    userGoalSettings: UserGoalSettings?,
    onToggleAdaptiveGoals: (Boolean) -> Unit,
    onToggleLockGoals: (Boolean) -> Unit,
    onToggleRecoveryMode: (Boolean) -> Unit,
    onRecalculateGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings = userGoalSettings ?: UserGoalSettings()

    SettingsGroupCard(
        title = stringResource(R.string.goals_title),
        icon = Icons.Default.Tune,
        modifier = modifier
    ) {
        // Adaptive goals switch
        SettingsSwitchRow(
            title = stringResource(R.string.adaptive_goals),
            subtitle = stringResource(R.string.adaptive_goals_desc),
            icon = Icons.Default.AutoAwesome,
            checked = settings.adaptiveGoalsEnabled,
            onCheckedChange = onToggleAdaptiveGoals,
            testTag = "adaptive_goals_switch",
            switchTestTag = "adaptive_goals_switch_control"
        )

        // Lock goals switch
        SettingsSwitchRow(
            title = stringResource(R.string.lock_goals),
            subtitle = stringResource(R.string.lock_goals_desc),
            icon = Icons.Default.Lock,
            checked = settings.lockedGoalEnabled,
            onCheckedChange = onToggleLockGoals,
            testTag = "lock_goals_switch",
            switchTestTag = "lock_goals_switch_control"
        )

        // Recovery mode switch
        SettingsSwitchRow(
            title = stringResource(R.string.recovery_mode),
            subtitle = stringResource(R.string.recovery_mode_desc),
            icon = Icons.Default.Spa,
            checked = settings.recoveryModeEnabled,
            onCheckedChange = onToggleRecoveryMode,
            testTag = "recovery_mode_switch",
            switchTestTag = "recovery_mode_switch_control"
        )

        // Recalculate goal action
        SettingsActionRow(
            title = stringResource(R.string.recalculate_adaptive_goal_btn),
            subtitle = stringResource(R.string.recalculate_adaptive_sub),
            icon = Icons.Default.Refresh,
            onClick = onRecalculateGoal,
            testTag = "recalculate_goal_button"
        )
    }
}
