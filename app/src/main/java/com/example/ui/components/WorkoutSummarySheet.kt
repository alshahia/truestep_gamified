package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.RollerSkating
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.WalkingSession
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing
import com.example.util.MetricFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSummarySheet(
    sheetState: SheetState,
    session: WalkingSession?,
    hunterName: String,
    hunterLevel: Int,
    onClaimRewards: (sparks: Int, cores: Int, shards: Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (session == null) return
    val context = LocalContext.current
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val steps = session.steps
    val durationSeconds = session.durationSeconds
    val distKm = session.distanceKm
    val avgCadence = if (durationSeconds > 0) ((steps.toDouble() / durationSeconds) * 60).roundToInt() else 0
    val caloriesKcal = (steps * 0.04).roundToInt()

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    // Determine performance grade
    val (gradeLetter, gradeColor, gradeTitleRes) = when {
        avgCadence >= 115 || steps >= 4000 -> Triple("S", primaryColor, R.string.raid_grade_s)
        avgCadence >= 95 || steps >= 2000 -> Triple("A", secondaryColor, R.string.raid_grade_a)
        else -> Triple("B", tertiaryColor, R.string.raid_grade_b)
    }

    val minutes = (durationSeconds / 60).coerceAtLeast(1)
    val paceMinPerKm = if (distKm > 0) (durationSeconds / 60.0 / distKm) else 5.2
    val paceMinutes = paceMinPerKm.toInt()
    val paceSeconds = ((paceMinPerKm - paceMinutes) * 60).toInt()
    val paceFormatted = "${paceMinutes}'${paceSeconds.toString().padStart(2, '0')}\""

    // EXP Math
    val strideExp = (steps * 0.08).roundToInt()
    val clearBonus = 250
    val auraBonus = if (avgCadence >= 110) 180 else 80
    val totalExpGained = strideExp + clearBonus + auraBonus

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = MaterialTheme.appShapes.bottomSheet,
        modifier = modifier.testTag("workout_summary_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MaterialTheme.spacing.lg, vertical = MaterialTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            // Victory Crest Hero Card
            Card(
                shape = MaterialTheme.appShapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    // Pulsing status pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.xs)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(MaterialTheme.spacing.xs)
                                .clip(CircleShape)
                                .background(secondaryColor)
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                        Text(
                            text = stringResource(R.string.raid_summary_status),
                            style = MaterialTheme.typography.labelSmall,
                            color = secondaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = stringResource(R.string.raid_summary_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Grade S / A / B Crest
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(gradeColor.copy(alpha = 0.3f), MaterialTheme.colorScheme.surface)
                                )
                            )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = gradeLetter,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold,
                                color = gradeColor,
                                lineHeight = 44.sp
                            )
                            Text(
                                text = "OVERDRIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = secondaryColor,
                                fontSize = 9.sp
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                        Text(
                            text = stringResource(gradeTitleRes),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(MaterialTheme.appShapes.pill)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                        )
                        Text(
                            text = stringResource(R.string.raid_overdrive_master),
                            style = MaterialTheme.typography.labelSmall,
                            color = tertiaryColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(MaterialTheme.appShapes.pill)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                        )
                    }
                }
            }

            // Biometric Telemetry 4-Stat Grid
            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                Text(
                    text = stringResource(R.string.nav_analytics),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    // Distance
                    Card(
                        shape = MaterialTheme.appShapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
                            Text(
                                text = stringResource(R.string.distance_label).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${MetricFormatter.formatDistance(distKm.toFloat())} km",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Active Time
                    Card(
                        shape = MaterialTheme.appShapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
                            Text(
                                text = stringResource(R.string.active_time_label).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = MetricFormatter.formatDuration(durationSeconds),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = secondaryColor
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    // Avg Pace
                    Card(
                        shape = MaterialTheme.appShapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
                            Text(
                                text = "AVG PACE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$paceFormatted /km",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Calories
                    Card(
                        shape = MaterialTheme.appShapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
                            Text(
                                text = stringResource(R.string.calories_burned).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$caloriesKcal kcal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = tertiaryColor
                            )
                        }
                    }
                }
            }

            // Kilometer Splits & Cadence Card
            Card(
                shape = MaterialTheme.appShapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.raid_km_splits_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.raid_overdrive_badge),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    val splitCount = maxOf(1, distKm.toInt()).coerceAtMost(5)
                    for (kmIndex in 1..splitCount) {
                        val splitSpm = (avgCadence + (kmIndex * 3) - 5).coerceIn(80, 160)
                        val isOverdrive = splitSpm >= 115
                        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "KM $kmIndex",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${splitSpm} SPM ${if (isOverdrive) "⚡" else "🔥"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isOverdrive) MaterialTheme.colorScheme.primary else secondaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { (splitSpm / 160f).coerceIn(0.2f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(MaterialTheme.appShapes.pill),
                                color = if (isOverdrive) MaterialTheme.colorScheme.primary else secondaryColor,
                                trackColor = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }
            }

            // Hunter EXP Progression Card
            Card(
                shape = MaterialTheme.appShapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$hunterName (LV. $hunterLevel)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Wind Courier • Speedster",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "+$totalExpGained EXP",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = secondaryColor
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Stride EXP +$strideExp",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Clear Bonus +$clearBonus",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Aura Surge +$auraBonus",
                            style = MaterialTheme.typography.labelSmall,
                            color = tertiaryColor
                        )
                    }
                }
            }

            // Raid Loot Cache Card
            Card(
                shape = MaterialTheme.appShapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    Text(
                        text = stringResource(R.string.raid_loot_cache_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 5x Sparks
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(MaterialTheme.appShapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(MaterialTheme.spacing.sm)
                        ) {
                            Icon(imageVector = Icons.Default.Diamond, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(text = "+5 Sparks", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }

                        // 1x Rare Core
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(MaterialTheme.appShapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(MaterialTheme.spacing.sm)
                        ) {
                            Icon(imageVector = Icons.Default.Memory, contentDescription = null, tint = secondaryColor)
                            Text(text = "+1 Core", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }

                        // 50x Shards
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(MaterialTheme.appShapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .padding(MaterialTheme.spacing.sm)
                        ) {
                            Icon(imageVector = Icons.Default.RollerSkating, contentDescription = null, tint = tertiaryColor)
                            Text(text = "+50 Shards", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Social Share Button
            FilledTonalButton(
                onClick = {
                    val shareText = "⚡ Overdrive Stride Cleared! ${MetricFormatter.formatSteps(steps)} steps in ${minutes}m (${MetricFormatter.formatDistance(distKm.toFloat())} km) at ${avgCadence} SPM! 🚀 Tracked offline with TrueStep."
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share Victory Card"))
                },
                shape = MaterialTheme.appShapes.medium,
                modifier = Modifier.fillMaxWidth().testTag("share_raid_card_button")
            ) {
                Icon(imageVector = Icons.Default.IosShare, contentDescription = null)
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                Text(text = "Share Victory Hologram Card", fontWeight = FontWeight.Bold)
            }

            // Primary Claim & Dismiss CTA
            Button(
                onClick = {
                    onClaimRewards(5, 1, 50)
                    onDismiss()
                },
                shape = MaterialTheme.appShapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.buttonHeight)
                    .testTag("claim_rewards_btn")
            ) {
                Icon(imageVector = Icons.Default.Redeem, contentDescription = null)
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                Text(
                    text = stringResource(R.string.raid_claim_rewards),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.md))
        }
    }
}
