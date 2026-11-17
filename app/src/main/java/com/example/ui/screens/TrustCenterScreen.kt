package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ui.theme.tokens.appShapes
import com.example.ui.viewmodel.StepUiState
import com.example.util.LocalizationUtil
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TrustCenterScreen(
    uiState: StepUiState,
    showHeader: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

    val cyanAccent = MaterialTheme.colorScheme.primary
    val mintAccent = MaterialTheme.colorScheme.secondary
    val violetAccent = MaterialTheme.colorScheme.tertiary
    val obsidianBackdrop = MaterialTheme.colorScheme.surface

    var hasActivityPerm by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val registryOwner = LocalActivityResultRegistryOwner.current
    val activityRecognitionLauncher = if (registryOwner != null) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasActivityPerm = granted
        }
    } else null

    val timeFormatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a").withZone(ZoneId.systemDefault()) }
    val lastSyncFormatted = remember(uiState.lastUpdated) {
        timeFormatter.format(Instant.ofEpochMilli(uiState.lastUpdated))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showHeader) {
            // CYBER SECURITY MATRIX TITLE
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.trust_center_title).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "NEURAL SECURITY MATRIX • ZERO-CLOUD SANDBOX PROTOCOL",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = mintAccent,
                    fontSize = 9.5.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        // 1. DATA SOURCE TRANSPARENCY CARD (ACTIVE HARDWARE FEEDS)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = obsidianBackdrop),
            border = BorderStroke(
                1.dp,
                Brush.horizontalGradient(listOf(cyanAccent, mintAccent))
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth().testTag("trust_data_sources_card")
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(cyanAccent.copy(alpha = 0.15f))
                                .border(1.dp, cyanAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Devices, contentDescription = null, tint = cyanAccent, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = stringResource(R.string.active_data_feeds).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(mintAccent.copy(alpha = 0.15f))
                            .border(1.dp, mintAccent, MaterialTheme.appShapes.pill)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SECURE",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = mintAccent
                        )
                    }
                }

                val sensorStatusText = LocalizationUtil.getSensorModeDisplayName(uiState.activeSensorMode)

                CyberStatusRow(
                    label = stringResource(R.string.phone_sensor_status),
                    status = sensorStatusText,
                    isActive = uiState.isSensorAvailable && hasActivityPerm
                )

                if (!hasActivityPerm && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    FilledTonalButton(
                        onClick = {
                            activityRecognitionLauncher?.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                        },
                        shape = MaterialTheme.appShapes.pill,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = cyanAccent
                        ),
                        border = BorderStroke(1.dp, cyanAccent.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth().testTag("trust_request_sensor_perm_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.request_sensor_perm).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                if (uiState.activeSensorMode == com.example.sensor.SensorMode.ACCELEROMETER_FALLBACK) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                            .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.accelerometer_fallback_notice),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                CyberStatusRow(
                    label = stringResource(R.string.health_connect_status),
                    status = stringResource(R.string.health_connect_connected),
                    isActive = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.last_sync_label).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.sp
                    )
                    Text(
                        text = lastSyncFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = cyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 2. RECONCILIATION & CONFIDENCE RADAR
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = obsidianBackdrop),
            border = BorderStroke(1.dp, violetAccent.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth().testTag("trust_overlap_card")
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(violetAccent.copy(alpha = 0.15f))
                            .border(1.dp, violetAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Layers, contentDescription = null, tint = violetAccent, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = stringResource(R.string.duplicate_detection_title).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = stringResource(R.string.deduplication_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, cyanAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    val confidenceText = stringResource(LocalizationUtil.getConfidenceBadgeRes(uiState.confidence))
                    Text(
                        text = stringResource(R.string.confidence_status_format, confidenceText, uiState.dataSource),
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        color = cyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3. ZERO-CLOUD LOCAL INTEGRITY GUARANTEE
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = obsidianBackdrop),
            border = BorderStroke(1.dp, mintAccent.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(mintAccent.copy(alpha = 0.15f))
                            .border(1.dp, mintAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = mintAccent, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = stringResource(R.string.privacy_guarantees_title).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                listOf(
                    stringResource(R.string.privacy_bullet_1),
                    stringResource(R.string.privacy_bullet_2),
                    stringResource(R.string.privacy_bullet_3),
                    stringResource(R.string.privacy_bullet_4)
                ).forEach { bullet ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "◆", color = mintAccent, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                        Text(text = bullet, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                OutlinedButton(
                    onClick = { showPrivacyPolicyDialog = true },
                    shape = MaterialTheme.appShapes.pill,
                    border = BorderStroke(1.dp, cyanAccent.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = cyanAccent),
                    modifier = Modifier.fillMaxWidth().testTag("trust_view_privacy_policy_btn")
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.read_privacy_policy).uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // 4. POWER PROTOCOL & BATTERY OPTIMIZATION
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = obsidianBackdrop),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(violetAccent.copy(alpha = 0.15f))
                            .border(1.dp, violetAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.BatteryChargingFull, contentDescription = null, tint = violetAccent, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = stringResource(R.string.battery_opt_title).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = stringResource(R.string.battery_opt_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FilledTonalButton(
                    onClick = {
                        com.example.util.OemBatteryHelper.openOemSettings(context)
                    },
                    shape = MaterialTheme.appShapes.pill,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = violetAccent
                    ),
                    border = BorderStroke(1.dp, violetAccent.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("battery_settings_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.open_battery_settings).uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }

    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = cyanAccent
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.privacy_policy_title),
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
                    modifier = Modifier.testTag("close_trust_privacy_dialog_button")
                ) {
                    Text(text = stringResource(R.string.close).uppercase(), color = cyanAccent, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .border(1.dp, cyanAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("trust_privacy_policy_dialog")
        )
    }
}

/**
 * Interactive Modal Bottom Sheet allowing on-demand inspection of the Trust Center
 * from Settings or the Home screen status strip.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrustCenterSheet(
    uiState: StepUiState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("trust_center_sheet_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.trust_center_title).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.testTag("close_trust_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TrustCenterScreen(
                uiState = uiState,
                showHeader = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CyberStatusRow(
    label: String,
    status: String,
    isActive: Boolean
) {
    val mintAccent = MaterialTheme.colorScheme.secondary
    val amberAccent = MaterialTheme.colorScheme.tertiary

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .clip(MaterialTheme.appShapes.pill)
                .background(
                    if (isActive) mintAccent.copy(alpha = 0.15f) else amberAccent.copy(alpha = 0.15f)
                )
                .border(
                    1.dp,
                    if (isActive) mintAccent else amberAccent,
                    MaterialTheme.appShapes.pill
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isActive) mintAccent else amberAccent)
                )
                Text(
                    text = if (isActive) "ACTIVE" else "STANDBY",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isActive) mintAccent else amberAccent
                )
            }
        }
    }
}
