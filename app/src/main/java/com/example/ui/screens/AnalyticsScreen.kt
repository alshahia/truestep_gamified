package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.content.AchievementRegistry
import com.example.domain.content.DestinationRegistry
import com.example.domain.model.Achievement
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.spacing
import com.example.ui.viewmodel.StepUiState
import com.example.util.LocalizationUtil
import com.example.util.MetricFormatter

@Composable
fun AnalyticsScreen(
    uiState: StepUiState,
    modifier: Modifier = Modifier
) {
    val history = uiState.allHistory
    val totalStepsLifetime = remember(history) { history.sumOf { it.totalSteps } }
    val totalKmLifetime = remember(history) { history.sumOf { it.cumulativeKm } }
    val bestDay = remember(history) { history.maxByOrNull { it.totalSteps } }
    val activeDaysCount = remember(history) { history.size.coerceAtLeast(1) }
    val metGoalCount = remember(history) { history.count { it.totalSteps >= it.minGoal } }
    val consistencyPercent = remember(history) {
        if (history.isEmpty()) 100 else ((metGoalCount.toFloat() / history.size) * 100).toInt()
    }

    val primaryAccent = MaterialTheme.colorScheme.primary
    val secondaryAccent = MaterialTheme.colorScheme.secondary
    val tertiaryAccent = MaterialTheme.colorScheme.tertiary
    val cardBackground = MaterialTheme.colorScheme.surfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    val nextDestination = remember(totalKmLifetime) {
        DestinationRegistry.getNextDestination(totalKmLifetime)
    }
    val completedDestinations = remember(totalKmLifetime) {
        DestinationRegistry.getCompletedDestinations(totalKmLifetime)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TELEMETRY HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.analytics_title).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = onSurfaceColor
                )
                Text(
                    text = "COMBAT BIOMETRICS & DIMENSIONAL TELEMETRY",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = primaryAccent,
                    fontSize = 9.5.sp,
                    letterSpacing = 1.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.appShapes.pill)
                    .background(primaryAccent.copy(alpha = 0.15f))
                    .border(1.dp, primaryAccent, MaterialTheme.appShapes.pill)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "LIVE SENSOR FEED",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = primaryAccent
                )
            }
        }

        // 1. COMBAT CONSISTENCY TELEMETRY HUD
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = BorderStroke(
                1.dp,
                Brush.horizontalGradient(listOf(primaryAccent, tertiaryAccent))
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("consistency_banner")
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(primaryAccent.copy(alpha = 0.15f))
                                .border(1.dp, primaryAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.consistency_score).uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = onSurfaceVariantColor,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "TACTICAL STABILITY QUOTA",
                                fontSize = 8.5.sp,
                                color = primaryAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Rank Pill
                    val adherenceRank = when {
                        consistencyPercent >= 90 -> "S-RANK"
                        consistencyPercent >= 75 -> "A-RANK"
                        consistencyPercent >= 60 -> "B-RANK"
                        else -> "C-RANK"
                    }
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(surfaceColor)
                            .border(1.dp, secondaryAccent, MaterialTheme.appShapes.pill)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = adherenceRank,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = secondaryAccent
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$consistencyPercent%",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = onSurfaceColor
                    )
                    Text(
                        text = stringResource(R.string.consistency_desc, metGoalCount, activeDaysCount),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = onSurfaceVariantColor,
                        fontSize = 11.sp
                    )
                }

                LinearProgressIndicator(
                    progress = { (consistencyPercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(MaterialTheme.appShapes.pill),
                    color = primaryAccent,
                    trackColor = outlineVariantColor
                )
            }
        }

        // 2. DIMENSIONAL GATE PROTOCOL (NEXT DESTINATION LANDMARK)
        if (nextDestination != null) {
            val prevKm = completedDestinations.lastOrNull()?.kmRequired ?: 0.0
            val destProgress = ((totalKmLifetime - prevKm) / (nextDestination.kmRequired - prevKm))
                .coerceIn(0.0, 1.0).toFloat()
            val remainingKm = (nextDestination.kmRequired - totalKmLifetime).coerceAtLeast(0.0)

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                border = BorderStroke(1.dp, tertiaryAccent.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("next_destination_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = tertiaryAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "DIMENSIONAL EXPEDITION GATE",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = tertiaryAccent,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = stringResource(R.string.km_left_format, remainingKm),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = primaryAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(surfaceColor)
                                .border(1.dp, tertiaryAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = nextDestination.emoji, fontSize = 24.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = LocalizationUtil.getDestinationName(nextDestination),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = onSurfaceColor
                            )
                            Text(
                                text = LocalizationUtil.getDestinationDescription(nextDestination),
                                style = MaterialTheme.typography.bodySmall,
                                color = onSurfaceVariantColor
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { destProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(MaterialTheme.appShapes.pill),
                        color = tertiaryAccent,
                        trackColor = outlineVariantColor
                    )
                }
            }
        }

        // 3. PERSONAL RECORDS & PEAK COMBAT FEATS
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.personal_records_title).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = onSurfaceColor
            )
            Text(
                text = "HUNTER CHRONO ARCHIVES & PEAK METRICS",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = secondaryAccent,
                fontSize = 9.sp,
                letterSpacing = 0.8.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CyberRecordTile(
                icon = Icons.Default.Star,
                title = stringResource(R.string.all_time_best_day),
                value = if (bestDay != null) MetricFormatter.formatSteps(bestDay.totalSteps) else "0",
                subtitle = bestDay?.date ?: "—",
                accentColor = secondaryAccent,
                modifier = Modifier.weight(1f)
            )
            CyberRecordTile(
                icon = Icons.Default.EmojiEvents,
                title = stringResource(R.string.longest_streak_record),
                value = "${uiState.bestStreak}",
                subtitle = stringResource(R.string.consecutive_days),
                accentColor = primaryAccent,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CyberRecordTile(
                icon = Icons.Default.Timeline,
                title = stringResource(R.string.total_lifetime_steps),
                value = MetricFormatter.formatSteps(totalStepsLifetime),
                subtitle = stringResource(R.string.all_time_total),
                accentColor = tertiaryAccent,
                modifier = Modifier.weight(1f)
            )
            CyberRecordTile(
                icon = Icons.Default.ElectricBolt,
                title = stringResource(R.string.total_lifetime_km),
                value = "%.1f km".format(totalKmLifetime),
                subtitle = stringResource(R.string.distance_traveled),
                accentColor = secondaryAccent,
                modifier = Modifier.weight(1f)
            )
        }

        // 4. DIMENSIONAL CHRONO BURST (HOURLY RHYTHM RADAR)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = BorderStroke(1.dp, outlineVariantColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.hourly_distribution).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = onSurfaceColor
                    )
                    Text(
                        text = "CHRONO RADAR",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryAccent
                    )
                }

                Text(
                    text = stringResource(R.string.time_of_day_rhythm_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariantColor
                )

                val hoursToDisplay = listOf(6, 7, 8, 9, 11, 12, 14, 15, 17, 18, 20, 22)
                val maxHourlySteps = hoursToDisplay.maxOfOrNull { uiState.hourlySteps.getOrElse(it) { 0 } }?.coerceAtLeast(100) ?: 100

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    hoursToDisplay.forEach { h ->
                        val count = uiState.hourlySteps.getOrElse(h) { 0 }
                        val fraction = (count.toFloat() / maxHourlySteps).coerceIn(0.08f, 1f)
                        val barBrush = if (count > 0) {
                            Brush.verticalGradient(listOf(primaryAccent, tertiaryAccent))
                        } else {
                            Brush.linearGradient(listOf(surfaceColor, surfaceColor))
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((56 * fraction).dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(barBrush)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${h}h",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = if (count > 0) primaryAccent else onSurfaceVariantColor
                            )
                        }
                    }
                }
            }
        }

        // 5. SYSTEM CORES & ACHIEVEMENT MEDALS
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.milestones_title).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = onSurfaceColor
            )
            Text(
                text = "AWAKENED SYSTEM MATRIX CORES",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = secondaryAccent,
                fontSize = 9.sp,
                letterSpacing = 0.8.sp
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AchievementRegistry.allAchievements.forEach { achievement ->
                val isUnlocked = uiState.unlockedAchievementIds.contains(achievement.id)
                CyberAchievementCard(
                    achievement = achievement,
                    isUnlocked = isUnlocked
                )
            }
        }
    }
}

@Composable
private fun CyberAchievementCard(
    achievement: Achievement,
    isUnlocked: Boolean
) {
    val primaryAccent = MaterialTheme.colorScheme.primary
    val secondaryAccent = MaterialTheme.colorScheme.secondary
    val cardBg = if (isUnlocked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    val borderStrokeColor = if (isUnlocked) primaryAccent.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, borderStrokeColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 2.dp else 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("achievement_${achievement.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) primaryAccent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        .border(
                            1.dp,
                            if (isUnlocked) primaryAccent else MaterialTheme.colorScheme.outlineVariant,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = achievement.iconEmoji, fontSize = 20.sp)
                }

                Column {
                    Text(
                        text = LocalizationUtil.getAchievementTitle(achievement),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = LocalizationUtil.getAchievementRequirement(achievement),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.appShapes.pill)
                    .background(
                        if (isUnlocked) secondaryAccent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    )
                    .border(
                        1.dp,
                        if (isUnlocked) secondaryAccent else MaterialTheme.colorScheme.outlineVariant,
                        MaterialTheme.appShapes.pill
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isUnlocked) secondaryAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = stringResource(if (isUnlocked) R.string.unlocked_badge else R.string.locked_badge).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isUnlocked) secondaryAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CyberRecordTile(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
