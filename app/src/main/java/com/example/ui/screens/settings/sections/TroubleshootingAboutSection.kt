package com.example.ui.screens.settings.sections

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.screens.settings.components.SettingsActionRow
import com.example.ui.screens.settings.components.SettingsGroupCard
import com.example.util.OemBatteryHelper

@Composable
fun TroubleshootingAboutSection(
    isHealthConnectSyncing: Boolean,
    onSyncHealthConnect: (Context) -> Unit,
    modifier: Modifier = Modifier,
    onReplayOnboarding: () -> Unit = {}
) {
    val context = LocalContext.current
    var showBatteryGuideDialog by remember { mutableStateOf(false) }

    SettingsGroupCard(
        title = stringResource(R.string.section_about),
        icon = Icons.Default.Info,
        modifier = modifier
    ) {
        // Replay setup guide walkthrough
        SettingsActionRow(
            title = stringResource(R.string.settings_replay_onboarding_title),
            subtitle = stringResource(R.string.settings_replay_onboarding_sub),
            icon = Icons.AutoMirrored.Filled.DirectionsWalk,
            onClick = onReplayOnboarding,
            testTag = "replay_onboarding_button"
        )
        // Health Connect sync button
        SettingsActionRow(
            title = stringResource(R.string.sync_health_connect_btn),
            subtitle = if (isHealthConnectSyncing) stringResource(R.string.status_syncing) else stringResource(R.string.sync_health_connect_sub),
            icon = Icons.Default.Sync,
            onClick = { onSyncHealthConnect(context) },
            enabled = !isHealthConnectSyncing,
            trailingContent = if (isHealthConnectSyncing) {
                {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else null,
            testTag = "sync_health_connect_button"
        )

        // Battery optimization exemption button
        val oemName = remember { OemBatteryHelper.getOemName() }
        SettingsActionRow(
            title = stringResource(R.string.battery_optimization_title),
            subtitle = stringResource(R.string.oem_battery_guide_sub, oemName),
            icon = Icons.Default.BatteryChargingFull,
            onClick = {
                showBatteryGuideDialog = true
            },
            testTag = "battery_optimization_button"
        )

        // Rate TrueStep on Google Play Store
        SettingsActionRow(
            title = stringResource(R.string.rate_app_title),
            subtitle = stringResource(R.string.rate_app_sub),
            icon = Icons.Default.Star,
            onClick = {
                try {
                    val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(marketIntent)
                } catch (e: Exception) {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(webIntent)
                }
            },
            testTag = "rate_app_button"
        )

        // Version & offline pledge
        SettingsActionRow(
            title = stringResource(R.string.app_version_label),
            subtitle = stringResource(R.string.offline_pledge_sub),
            icon = Icons.Default.Info,
            onClick = {},
            showChevron = false
        )
    }

    if (showBatteryGuideDialog) {
        val oemName = remember { OemBatteryHelper.getOemName() }
        val instructions = remember { OemBatteryHelper.getOemInstructions() }

        AlertDialog(
            onDismissRequest = { showBatteryGuideDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.oem_battery_guide_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.oem_detected_prefix, oemName),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.oem_explanation, oemName),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Recommended Steps:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    instructions.forEach { step ->
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        OemBatteryHelper.openOemSettings(context)
                        showBatteryGuideDialog = false
                    }
                ) {
                    Text(stringResource(R.string.open_device_settings_btn))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showBatteryGuideDialog = false }
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }
}
