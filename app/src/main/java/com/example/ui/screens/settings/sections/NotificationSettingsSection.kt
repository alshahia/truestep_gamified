package com.example.ui.screens.settings.sections

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.ui.screens.settings.components.SettingsGroupCard
import com.example.ui.screens.settings.components.SettingsSwitchRow

@Composable
fun NotificationSettingsSection(
    isLiveNotificationEnabled: Boolean,
    onToggleLiveNotification: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsGroupCard(
        title = stringResource(R.string.section_notifications),
        icon = Icons.Default.Notifications,
        modifier = modifier
    ) {
        SettingsSwitchRow(
            title = stringResource(R.string.live_notification_mode),
            subtitle = if (isLiveNotificationEnabled) {
                stringResource(R.string.live_notification_sub_active)
            } else {
                stringResource(R.string.live_notification_sub_minimal)
            },
            icon = if (isLiveNotificationEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
            checked = isLiveNotificationEnabled,
            onCheckedChange = onToggleLiveNotification,
            testTag = "live_notification_switch",
            switchTestTag = "live_notification_switch_control"
        )
    }
}
