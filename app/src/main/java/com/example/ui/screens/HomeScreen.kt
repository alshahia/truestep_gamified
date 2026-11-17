package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SystemHologramDialog
import com.example.ui.components.WorkoutSummarySheet
import com.example.domain.model.DailyQuest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import com.example.R
import com.example.data.local.WalkingSession
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.HeroStepRing
import com.example.ui.components.HistoryPreviewChart
import com.example.ui.components.HomeKpiGrid
import com.example.ui.components.HourlyScrubRibbon
import com.example.ui.components.PausedBanner
import com.example.ui.components.RatingReviewDialog
import com.example.ui.components.WalkingSessionSheet
import com.example.ui.model.HistoryChartRange
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing
import com.example.ui.viewmodel.StepUiState
import com.example.util.MetricFormatter
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: StepUiState,
    onTogglePause: () -> Unit,
    onUpdateGoal: (Int) -> Unit,
    onScrubHour: (Int?) -> Unit,
    onSelectRange: (HistoryChartRange) -> Unit = {},
    onNavigateToDestination: () -> Unit = {},
    onNavigateToJourney: () -> Unit = {},
    onNavigateToGoals: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onSaveWalkingSession: (WalkingSession) -> Unit = {},
    onDeleteWalkingSession: (Long) -> Unit = {},
    onOpenTrustCenter: () -> Unit = {},
    onDismissCelebration: () -> Unit = {},
    onShareCelebration: (android.content.Context) -> Unit = {},
    onDismissReviewPrompt: (Boolean) -> Unit = {},
    onRateOnPlayStore: (android.content.Context) -> Unit = {},
    onSendFeedback: (android.content.Context) -> Unit = {},
    onRateApp: (android.content.Context) -> Unit = {},
    onClaimRaidRewards: (Int, Int, Int) -> Unit = { _, _, _ -> },
    onDismissWorkoutSummary: () -> Unit = {},
    onDismissSystemNotification: () -> Unit = {},
    onOpenQuestSystemPopup: (DailyQuest) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    var showWalkingSessionSheet by remember { mutableStateOf(false) }
    val walkingSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val timeFormatter = remember { DateTimeFormatter.ofPattern("h:mm a").withZone(ZoneId.systemDefault()) }
    val isStale = remember(uiState.lastUpdated) {
        val ageMillis = System.currentTimeMillis() - uiState.lastUpdated
        ageMillis > 3_600_000L && uiState.currentSteps > 0
    }
    val syncTimeFormatted = remember(uiState.lastUpdated) {
        timeFormatter.format(Instant.ofEpochMilli(uiState.lastUpdated))
    }

    val remainingToTarget = maxOf(0, uiState.goalSteps - uiState.currentSteps)
    val supportiveMessage = when {
        uiState.isRestDay -> stringResource(R.string.supportive_rest)
        uiState.currentSteps >= uiState.stretchGoal -> stringResource(R.string.stretch_reached)
        uiState.currentSteps >= uiState.goalSteps -> stringResource(R.string.goal_reached)
        uiState.currentSteps >= uiState.minGoal -> stringResource(R.string.min_goal_completed_inline, remainingToTarget)
        remainingToTarget > 0 -> stringResource(R.string.steps_to_target_inline, remainingToTarget)
        else -> stringResource(R.string.supportive_habit)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.lg, vertical = MaterialTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        // Prominently Placed Paused Notification Banner (Visible immediately at the top)
        if (uiState.isPaused) {
            PausedBanner(modifier = Modifier.fillMaxWidth())
        }

        // Scrub Mode Exit Banner (Visible immediately at the top when scrub hour is selected)
        if (uiState.isScrubbing && uiState.selectedScrubHour != null) {
            val formattedHour = String.format(java.util.Locale.getDefault(), "%02d:00", uiState.selectedScrubHour)
            Card(
                shape = MaterialTheme.appShapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("scrub_exit_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.scrub_viewing_hour_format,
                            formattedHour,
                            MetricFormatter.formatSteps(uiState.hourSteps)
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    TextButton(
                        onClick = { onScrubHour(null) },
                        modifier = Modifier.testTag("scrub_exit_button")
                    ) {
                        Text(
                            text = stringResource(R.string.return_to_full_day),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Holographic Urgent Quest System Beacon (Solo Leveling / Cyberpunk Directive Alert)
        val activeQuest = uiState.dailyQuests.firstOrNull { !it.isCompleted } ?: uiState.dailyQuests.firstOrNull()
        if (activeQuest != null) {
            Card(
                onClick = { onOpenQuestSystemPopup(activeQuest) },
                shape = MaterialTheme.appShapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_urgent_quest_beacon")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = activeQuest.iconEmoji,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = stringResource(R.string.home_urgent_quest_beacon),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.8.sp),
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(activeQuest.titleRes) + " • " + MetricFormatter.formatSteps(activeQuest.currentValue) + "/" + MetricFormatter.formatSteps(activeQuest.targetValue),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = stringResource(R.string.home_summon_quest_hud),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }

        // Hunter RPG Profile & Combat Power Status Card (Approved Gamified Athletic HUD)
        Card(
            onClick = onNavigateToJourney,
            shape = MaterialTheme.appShapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_hunter_header_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Dossier Terminal Label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_hunter_dossier),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.home_open_gear_vault),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        // High-Res Illustrated Cyber Runner Avatar with Glowing Rim
                        Box(
                            modifier = Modifier.size(50.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(MaterialTheme.appShapes.medium)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary), MaterialTheme.appShapes.medium)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.proto_cyber_runner_1791613481498),
                                    contentDescription = stringResource(R.string.hero_title),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Glowing Level Tag Overlaid on Avatar
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.appShapes.pill)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "LV.${uiState.hunterProfile.level}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1
                                )
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = uiState.hunterProfile.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(MaterialTheme.appShapes.pill)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .border(BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary), MaterialTheme.appShapes.pill)
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = uiState.hunterProfile.rankTitle.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                            Text(
                                text = uiState.hunterProfile.classTitle,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Combat Power Core Reactor Chip
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.secondary), MaterialTheme.appShapes.pill)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("hunter_combat_power_chip")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${MetricFormatter.formatSteps(uiState.hunterProfile.combatPower)} CP",
                                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Dynamic Segmented EXP Gauge
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXP: ${MetricFormatter.formatSteps(uiState.hunterProfile.currentExp)} / ${MetricFormatter.formatSteps(uiState.hunterProfile.expToNextLevel)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(
                                R.string.exp_gauge_format,
                                ((uiState.hunterProfile.currentExp.toFloat() / maxOf(1, uiState.hunterProfile.expToNextLevel)) * 100).toInt(),
                                uiState.hunterProfile.level + 1
                            ),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    LinearProgressIndicator(
                        progress = {
                            (uiState.hunterProfile.currentExp.toFloat() / maxOf(1, uiState.hunterProfile.expToNextLevel)).coerceIn(0f, 1f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(MaterialTheme.appShapes.pill),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                // Inventory Currency Bar with Glowing Resource Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.appShapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant), MaterialTheme.appShapes.small)
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.hunterProfile.sparks} 💎",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.appShapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant), MaterialTheme.appShapes.small)
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.hunterProfile.cores} ⚙️",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.appShapes.small)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant), MaterialTheme.appShapes.small)
                            .padding(vertical = 4.dp, horizontal = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.hunterProfile.shards} 💠",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Mana Overdrive / Aura Multiplier Banner
        Card(
            shape = MaterialTheme.appShapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_aura_overdrive_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = stringResource(R.string.home_mana_overdrive_engaged),
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(R.string.home_aura_overdrive_active),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.appShapes.pill)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(BorderStroke(1.dp, MaterialTheme.colorScheme.primary), MaterialTheme.appShapes.pill)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.boost_badge_format, uiState.hunterProfile.auraMultiplier.toString()),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }
        }

        // Hero Circular Step Ring with Safe Dedicated Toggle
        HeroStepRing(
            steps = uiState.displaySteps,
            goal = uiState.goalSteps,
            percent = uiState.displayPercent,
            isPaused = uiState.isPaused,
            onTogglePause = onTogglePause,
            modifier = Modifier.fillMaxWidth()
        )

        // Elevated Above-the-Fold 2x2 Metric KPI Grid (Steps, Distance, Calories, Active Minutes)
        HomeKpiGrid(
            stepsInHour = uiState.displaySteps,
            distanceKm = uiState.displayKm,
            caloriesKcal = uiState.displayCalories,
            shareOfDayPercent = if (uiState.isScrubbing) uiState.hourSharePercent else uiState.displayPercent,
            isScrubbing = uiState.isScrubbing,
            isPaused = uiState.isPaused,
            stepsDeltaInHour = if (uiState.isScrubbing) uiState.hourSteps else 0,
            activeMinutes = uiState.activeMinutes,
            modifier = Modifier.fillMaxWidth()
        )

        // Trust & Transparency Status Strip (Clickable to open full Trust Center)
        Card(
            onClick = onOpenTrustCenter,
            shape = MaterialTheme.appShapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                if (isStale) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("today_trust_status_card")
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isStale) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isStale) stringResource(R.string.telemetry_stale) else stringResource(R.string.telemetry_hardware),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 0.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = if (isStale) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = uiState.dataSource.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.appShapes.pill)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (uiState.confidence == "HIGH") stringResource(R.string.confidence_badge_high).uppercase() else stringResource(R.string.confidence_badge_reconciled).uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Freshness & Supportive message
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = supportiveMessage,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isStale) stringResource(R.string.data_stale) else syncTimeFormatted,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (isStale) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }

        // Dedicated Goal Tiers Overview Card (Decoupled from Trust Card to prevent nested clickables)
        Card(
            shape = MaterialTheme.appShapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_goal_tiers_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToGoals)
                    .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm)
                    .testTag("home_goal_tiers_row"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = stringResource(R.string.goal_min_inline_format, uiState.minGoal),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Text(
                        text = stringResource(R.string.goal_target_inline_format, uiState.goalSteps),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                    Text(
                        text = stringResource(R.string.goal_stretch_inline_format, uiState.stretchGoal),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = stringResource(R.string.goals_title),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Action Buttons: Stacked Full-Width Hierarchy (Initiate Stride Raid + Edit Goal)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
        ) {
            Button(
                onClick = { showWalkingSessionSheet = true },
                shape = MaterialTheme.appShapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_walk_session_cta")
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                Text(
                    text = stringResource(R.string.home_initiate_stride_raid),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            FilledTonalButton(
                onClick = { showGoalDialog = true },
                shape = MaterialTheme.appShapes.medium,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("edit_goal_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.dimens.iconSmall),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                Text(
                    text = stringResource(R.string.edit_goal).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Daily Habit Briefing & Quests Glance Card (Active Bounties Terminal)
        val currentHour = remember { LocalTime.now().hour }
        val briefingText = when {
            currentHour in 5..11 -> stringResource(R.string.briefing_morning)
            currentHour in 12..17 -> stringResource(R.string.briefing_afternoon)
            else -> stringResource(R.string.briefing_evening)
        }

        Card(
            shape = MaterialTheme.appShapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_daily_briefing_card")
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
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = stringResource(R.string.home_active_contracts),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, letterSpacing = 1.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = briefingText,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    FilledTonalButton(
                        onClick = onNavigateToJourney,
                        shape = MaterialTheme.appShapes.pill,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("home_view_journey_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.dimens.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                        Text(
                            text = stringResource(R.string.home_loot_vault),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (uiState.dailyQuests.isNotEmpty()) {
                    // Display top active daily bounties directly on Home screen
                    val topQuests = uiState.dailyQuests.take(2)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        topQuests.forEach { quest ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.appShapes.medium)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)), MaterialTheme.appShapes.medium)
                                    .clickable { onOpenQuestSystemPopup(quest) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (quest.isCompleted) Icons.Default.CheckCircle else Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = if (quest.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(quest.titleRes),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (quest.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(MaterialTheme.appShapes.pill)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "+${quest.rewardXp} XP",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(
                                R.string.home_quests_glance_format,
                                uiState.completedQuestsCount,
                                uiState.totalQuestsCount
                            ).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val questProgress = if (uiState.totalQuestsCount > 0) {
                        (uiState.completedQuestsCount.toFloat() / uiState.totalQuestsCount).coerceIn(0f, 1f)
                    } else 0f

                    LinearProgressIndicator(
                        progress = { questProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.spacing.xs)
                            .clip(MaterialTheme.appShapes.pill),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }


        // Day Shape Hourly Scrub Ribbon (Full 24-hour timeline 00:00 to 23:00)
        HourlyScrubRibbon(
            hourlySteps = uiState.hourlySteps,
            selectedHour = uiState.selectedScrubHour,
            onScrubHour = onScrubHour,
            modifier = Modifier.fillMaxWidth()
        )

        // Cadence Peak Insight Callout
        Card(
            shape = MaterialTheme.appShapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_cadence_insight_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
                Text(
                    text = stringResource(R.string.home_cadence_insight),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Embedded Multi-Range History Chart (Daily 14d | Weekly 12w | Monthly 12m) with Link to Full History
        HistoryPreviewChart(
            chartData = uiState.chartRangeData,
            selectedRange = uiState.chartRange,
            onSelectRange = onSelectRange,
            onNavigateToHistory = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))
    }

    // Walking Session Bottom Sheet
    if (showWalkingSessionSheet) {
        WalkingSessionSheet(
            sheetState = walkingSheetState,
            walkingSessions = uiState.walkingSessions,
            currentDaySteps = uiState.currentSteps,
            onSaveSession = onSaveWalkingSession,
            onDeleteSession = onDeleteWalkingSession,
            onDismiss = { showWalkingSessionSheet = false }
        )
    }

    // Goal Edit Dialog
    if (showGoalDialog) {
        var goalInput by remember { mutableStateOf(uiState.goalSteps.toString()) }
        val presets = listOf(4000, 6000, 8000, 10000, 12000)

        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.edit_goal),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)) {
                    Text(
                        text = stringResource(R.string.goals_title),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() } && input.length <= 6) {
                                goalInput = input
                            }
                        },
                        label = { Text(stringResource(R.string.target_goal_label)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("goal_dialog_input")
                    )

                    Text(
                        text = stringResource(R.string.quick_presets),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                    ) {
                        presets.forEach { preset ->
                            FilterChip(
                                selected = goalInput == preset.toString(),
                                onClick = { goalInput = preset.toString() },
                                label = {
                                    Text(
                                        text = "${preset / 1000}k",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val parsed = goalInput.toIntOrNull()
                        if (parsed != null && parsed in 1000..100000) {
                            onUpdateGoal(parsed)
                        }
                        showGoalDialog = false
                    },
                    modifier = Modifier.testTag("goal_dialog_confirm")
                ) {
                    Text(stringResource(R.string.save), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (uiState.showCelebrationDialog) {
        val context = LocalContext.current
        CelebrationDialog(
            steps = uiState.currentSteps,
            distanceKm = uiState.cumulativeKm,
            streak = uiState.streak,
            onShare = { onShareCelebration(context) },
            onDismiss = onDismissCelebration,
            onRateApp = { onRateApp(context) }
        )
    }

    if (uiState.showReviewPromptDialog) {
        val context = LocalContext.current
        RatingReviewDialog(
            onRateOnStore = { onRateOnPlayStore(context) },
            onSendFeedback = { onSendFeedback(context) },
            onDismiss = { onDismissReviewPrompt(false) },
            onNeverAskAgain = { onDismissReviewPrompt(true) }
        )
    }

    // Post-Workout Raid Summary Sheet
    if (uiState.showWorkoutSummary && uiState.lastCompletedSession != null) {
        val summarySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        WorkoutSummarySheet(
            sheetState = summarySheetState,
            session = uiState.lastCompletedSession,
            hunterName = uiState.hunterProfile.name,
            hunterLevel = uiState.hunterProfile.level,
            onClaimRewards = onClaimRaidRewards,
            onDismiss = onDismissWorkoutSummary
        )
    }

    // Translucent Holographic Blue Solo Leveling System Dialog
    if (uiState.showSystemDialog) {
        SystemHologramDialog(
            titleRes = uiState.systemDialogTitleRes,
            bodyText = uiState.systemDialogBody,
            eventType = uiState.systemDialogEventType,
            onConfirm = onDismissSystemNotification,
            onDismiss = onDismissSystemNotification
        )
    }
}
