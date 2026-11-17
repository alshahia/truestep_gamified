package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.sync.HealthConnectHelper
import com.example.ui.theme.tokens.appShapes

@Composable
fun OnboardingScreen(
    onFinish: (startingGoal: Int, carryingMode: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var step by remember { mutableIntStateOf(0) }
    var chosenGoal by remember { mutableIntStateOf(10000) }
    var customGoalText by remember { mutableStateOf("8000") }
    var isCustomGoalSelected by remember { mutableStateOf(false) }
    var chosenCarryingMode by remember { mutableStateOf("Pocket") }

    val primaryAccent = MaterialTheme.colorScheme.primary
    val secondaryAccent = MaterialTheme.colorScheme.secondary
    val tertiaryAccent = MaterialTheme.colorScheme.tertiary

    // Permissions state tracking
    var hasHealthConnectPerm by remember { mutableStateOf(false) }
    var hasActivityRecognitionPerm by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }
    var hasNotificationPerm by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val registryOwner = LocalActivityResultRegistryOwner.current

    val healthConnectLauncher = if (registryOwner != null) {
        rememberLauncherForActivityResult(
            contract = androidx.health.connect.client.PermissionController.createRequestPermissionResultContract()
        ) { grantedPermissions ->
            hasHealthConnectPerm = grantedPermissions.containsAll(HealthConnectHelper.REQUIRED_PERMISSIONS)
        }
    } else null

    val activityRecognitionLauncher = if (registryOwner != null) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasActivityRecognitionPerm = granted
        }
    } else null

    val notificationLauncher = if (registryOwner != null) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasNotificationPerm = granted
        }
    } else null

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with System Status & Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(primaryAccent)
                    )
                    Text(
                        text = "SYSTEM INITIALIZATION",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryAccent,
                        letterSpacing = 1.sp
                    )
                }

                TextButton(
                    onClick = {
                        val finalGoal = if (isCustomGoalSelected) {
                            customGoalText.toIntOrNull()?.coerceIn(1000, 50000) ?: chosenGoal
                        } else chosenGoal
                        onFinish(finalGoal, chosenCarryingMode)
                    },
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_skip).uppercase(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-Step Content
            when (step) {
                0 -> {
                    // Step 0: Welcome & Product explanation - Hunter Awakening
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.linearGradient(listOf(primaryAccent, tertiaryAccent)),
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_app_logo_winged),
                                contentDescription = "Hunter Awakening Crest",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(26.dp))
                            )
                        }

                        Text(
                            text = stringResource(R.string.onboarding_welcome_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = stringResource(R.string.onboarding_welcome_desc),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = stringResource(R.string.onboarding_trust_badge).uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = primaryAccent,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "◆ " + stringResource(R.string.onboarding_trust_bullet_1),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "◆ " + stringResource(R.string.onboarding_trust_bullet_2),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "◆ " + stringResource(R.string.onboarding_trust_bullet_3),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // Step 1: Privacy explanation & Optional Permissions
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(secondaryAccent.copy(alpha = 0.15f))
                                .border(1.5.dp, secondaryAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = secondaryAccent,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Text(
                            text = stringResource(R.string.onboarding_privacy_title),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = stringResource(R.string.onboarding_privacy_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        // Health Connect Permission Card
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(imageVector = Icons.Default.HealthAndSafety, contentDescription = null, tint = primaryAccent)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = stringResource(R.string.onboarding_hc_title),
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = stringResource(R.string.onboarding_hc_desc),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (hasHealthConnectPerm) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = stringResource(R.string.status_granted), tint = secondaryAccent)
                                } else {
                                    FilledTonalButton(
                                        onClick = {
                                            healthConnectLauncher?.launch(HealthConnectHelper.REQUIRED_PERMISSIONS)
                                        },
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = primaryAccent
                                        ),
                                        border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.5f))
                                    ) {
                                        Text(stringResource(R.string.onboarding_hc_btn), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Activity Recognition (Step Sensor) Permission Card
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth().testTag("onboarding_sensor_perm_card")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                                            contentDescription = null,
                                            tint = primaryAccent
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = stringResource(R.string.onboarding_sensor_perm_title),
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = stringResource(R.string.onboarding_sensor_perm_desc),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    if (hasActivityRecognitionPerm) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = stringResource(R.string.status_granted),
                                            tint = secondaryAccent
                                        )
                                    } else {
                                        FilledTonalButton(
                                            onClick = {
                                                activityRecognitionLauncher?.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                                            },
                                            colors = ButtonDefaults.filledTonalButtonColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                contentColor = primaryAccent
                                            ),
                                            border = BorderStroke(1.dp, primaryAccent),
                                            modifier = Modifier.testTag("onboarding_sensor_perm_button")
                                        ) {
                                            Text(stringResource(R.string.onboarding_allow_btn), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                }
                            }
                        }

                        // Notification Permission Card
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = tertiaryAccent)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = stringResource(R.string.onboarding_notif_title),
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = stringResource(R.string.onboarding_notif_desc),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (hasNotificationPerm) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = stringResource(R.string.status_granted), tint = secondaryAccent)
                                } else {
                                    FilledTonalButton(
                                        onClick = {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                notificationLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
                                            }
                                        },
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            contentColor = tertiaryAccent
                                        ),
                                        border = BorderStroke(1.dp, tertiaryAccent.copy(alpha = 0.5f))
                                    ) {
                                        Text(stringResource(R.string.onboarding_notif_btn), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Step 2: Goal preference (Beginner, Existing walker, Custom)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(primaryAccent.copy(alpha = 0.15f))
                                .border(1.5.dp, primaryAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Text(
                            text = stringResource(R.string.onboarding_goal_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CyberGoalChoiceCard(
                                title = stringResource(R.string.goal_profile_beginner),
                                steps = 4000,
                                isSelected = !isCustomGoalSelected && chosenGoal == 4000,
                                onClick = {
                                    isCustomGoalSelected = false
                                    chosenGoal = 4000
                                }
                            )
                            CyberGoalChoiceCard(
                                title = stringResource(R.string.goal_profile_active),
                                steps = 7500,
                                isSelected = !isCustomGoalSelected && chosenGoal == 7500,
                                onClick = {
                                    isCustomGoalSelected = false
                                    chosenGoal = 7500
                                }
                            )
                            CyberGoalChoiceCard(
                                title = stringResource(R.string.goal_profile_stretch),
                                steps = 10000,
                                isSelected = !isCustomGoalSelected && chosenGoal == 10000,
                                onClick = {
                                    isCustomGoalSelected = false
                                    chosenGoal = 10000
                                }
                            )

                            // Custom Goal Card
                            Card(
                                onClick = { isCustomGoalSelected = true },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCustomGoalSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isCustomGoalSelected) primaryAccent else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = stringResource(R.string.onboarding_custom_goal),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isCustomGoalSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCustomGoalSelected) primaryAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    AnimatedVisibility(visible = isCustomGoalSelected) {
                                        OutlinedTextField(
                                            value = customGoalText,
                                            onValueChange = { input ->
                                                if (input.all { it.isDigit() } && input.length <= 6) {
                                                    customGoalText = input
                                                }
                                            },
                                            label = { Text(stringResource(R.string.onboarding_custom_goal_label), color = primaryAccent) },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = primaryAccent,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Step 3: Carrying Mode Selection
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(tertiaryAccent.copy(alpha = 0.15f))
                                .border(1.5.dp, tertiaryAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Smartphone,
                                contentDescription = null,
                                tint = tertiaryAccent,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Text(
                            text = stringResource(R.string.onboarding_carrying_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = stringResource(R.string.onboarding_carrying_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        val modes = listOf(
                            Triple("Pocket", stringResource(R.string.mode_pocket), Icons.Default.Smartphone),
                            Triple("Hand", stringResource(R.string.mode_hand), Icons.AutoMirrored.Filled.DirectionsWalk),
                            Triple("Bag", stringResource(R.string.mode_bag), Icons.Default.ShoppingBag),
                            Triple("Backpack", stringResource(R.string.mode_backpack), Icons.Default.ShoppingBag),
                            Triple("Wearable", stringResource(R.string.mode_wearable), Icons.Default.Watch),
                            Triple("Treadmill", stringResource(R.string.mode_treadmill), Icons.Default.FitnessCenter)
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            modes.forEach { (modeKey, modeDesc, icon) ->
                                val isSelected = chosenCarryingMode == modeKey
                                CyberCarryingModeChoiceCard(
                                    title = modeDesc,
                                    icon = icon,
                                    isSelected = isSelected,
                                    onClick = { chosenCarryingMode = modeKey }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer buttons & step dots
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Notches (4 steps total: 0, 1, 2, 3)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(4) { i ->
                        Box(
                            modifier = Modifier
                                .size(if (step == i) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (step == i) primaryAccent else MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            val finalGoal = if (isCustomGoalSelected) {
                                customGoalText.toIntOrNull()?.coerceIn(1000, 50000) ?: chosenGoal
                            } else chosenGoal
                            onFinish(finalGoal, chosenCarryingMode)
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryAccent,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_primary_button")
                ) {
                    Text(
                        text = (if (step == 3) stringResource(R.string.onboarding_start_button) else stringResource(R.string.onboarding_next)).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CyberGoalChoiceCard(
    title: String,
    steps: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryAccent = MaterialTheme.colorScheme.primary

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) primaryAccent else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "%,d".format(steps),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) primaryAccent else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CyberCarryingModeChoiceCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tertiaryAccent = MaterialTheme.colorScheme.tertiary

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) tertiaryAccent else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) tertiaryAccent else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
