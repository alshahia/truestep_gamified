package com.example.ui.screens.settings.sections

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.screens.settings.components.SettingsActionRow
import com.example.ui.screens.settings.components.SettingsDangerRow
import com.example.ui.screens.settings.components.SettingsGroupCard

@Composable
fun PrivacyDataSettingsSection(
    onExportData: (Context, Boolean) -> Unit,
    onClearAllData: () -> Unit,
    onLoadDemoData: () -> Unit,
    onOpenTrustCenter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

    SettingsGroupCard(
        title = stringResource(R.string.section_privacy),
        icon = Icons.Default.Security,
        subtitle = stringResource(R.string.privacy_guarantee),
        modifier = modifier
    ) {
        // Trust Center & Transparency
        SettingsActionRow(
            title = stringResource(R.string.trust_center_action_title),
            subtitle = stringResource(R.string.trust_center_action_desc),
            icon = Icons.Default.Shield,
            onClick = onOpenTrustCenter,
            testTag = "open_trust_center_button"
        )

        // Privacy Policy & Data Rights
        SettingsActionRow(
            title = stringResource(R.string.privacy_policy_title),
            subtitle = stringResource(R.string.privacy_policy_sub),
            icon = Icons.Default.Security,
            onClick = { showPrivacyPolicyDialog = true },
            testTag = "privacy_policy_button"
        )

        // Export CSV
        SettingsActionRow(
            title = stringResource(R.string.export_csv_btn),
            subtitle = stringResource(R.string.export_csv_sub),
            icon = Icons.Default.FileDownload,
            onClick = { onExportData(context, true) },
            testTag = "export_csv_button"
        )

        // Export JSON
        SettingsActionRow(
            title = stringResource(R.string.export_json_btn),
            subtitle = stringResource(R.string.export_json_sub),
            icon = Icons.Default.FileDownload,
            onClick = { onExportData(context, false) },
            testTag = "export_json_button"
        )

        // Load Sample Data (Demo)
        SettingsActionRow(
            title = stringResource(R.string.load_demo_data_btn),
            subtitle = stringResource(R.string.load_demo_sub),
            icon = Icons.Default.Science,
            onClick = onLoadDemoData,
            testTag = "load_demo_data_button"
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Delete local data button with confirmation dialog
        SettingsDangerRow(
            title = stringResource(R.string.delete_data),
            onClick = { showDeleteDialog = true },
            testTag = "delete_local_data_button"
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(text = stringResource(R.string.delete_data_confirm_title))
            },
            text = {
                Text(text = stringResource(R.string.delete_data_confirm_desc))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllData()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text(text = stringResource(R.string.confirm_delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    modifier = Modifier.testTag("cancel_delete_button")
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
            modifier = Modifier.testTag("delete_confirm_dialog")
        )
    }

    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            icon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(text = stringResource(R.string.privacy_policy_title))
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(R.string.privacy_policy_body),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showPrivacyPolicyDialog = false },
                    modifier = Modifier.testTag("close_privacy_policy_button")
                ) {
                    Text(text = stringResource(R.string.close))
                }
            },
            modifier = Modifier.testTag("privacy_policy_dialog")
        )
    }
}
