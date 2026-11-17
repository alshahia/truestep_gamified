package com.example.ui.screens.journey.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.content.DestinationRegistry
import com.example.domain.model.Destination
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing
import com.example.util.MetricFormatter

@Composable
fun ExpeditionTrailCard(
    allTimeKm: Double,
    nextDestination: Destination?,
    modifier: Modifier = Modifier
) {
    val completedList = DestinationRegistry.getCompletedDestinations(allTimeKm)
    val activeDest = nextDestination ?: DestinationRegistry.destinations.last()
    val primaryAccent = MaterialTheme.colorScheme.primary
    val secondaryAccent = MaterialTheme.colorScheme.secondary
    val tertiaryAccent = MaterialTheme.colorScheme.tertiary
    val errorAccent = MaterialTheme.colorScheme.error

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(primaryAccent.copy(alpha = 0.7f), tertiaryAccent.copy(alpha = 0.6f)))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("expedition_card")
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            // TOP HEADER: Cybernetic Hunter Expedition & Gate Rank Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(primaryAccent, tertiaryAccent.copy(alpha = 0.3f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = activeDest.emoji,
                            fontSize = 20.sp
                        )
                    }
                    Column {
                        Text(
                            text = stringResource(R.string.cyber_gate_title).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.cyber_gate_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Active Gate Rank Badge (e.g. S-RANK, A-RANK)
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.appShapes.pill)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, primaryAccent, MaterialTheme.appShapes.pill)
                        .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                        .testTag("cyber_gate_rank_badge")
                ) {
                    Text(
                        text = activeDest.rankGrade,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryAccent
                    )
                }
            }

            // DIMENSIONAL GATE VISUAL PORTAL CHAMBER PREVIEW
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, primaryAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dungeon_gate_preview_1791739430511),
                    contentDescription = "Dimensional Gate Portal",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(primaryAccent)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PORTAL BREACH",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = activeDest.chamberTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // OPTION C DIMENSIONAL CYBER-GATE 04 PORTAL CORE
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cyber_gate_core_card")
            ) {
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    // Portal Chamber Badge & Arcane Lore
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = activeDest.chamberTitle,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = primaryAccent
                            )
                        }

                        Text(
                            text = stringResource(R.string.cyber_gate_boss_approaching),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = errorAccent,
                            modifier = Modifier
                                .clip(MaterialTheme.appShapes.pill)
                                .background(errorAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Gate Name & Description
                    Text(
                        text = stringResource(activeDest.nameRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(activeDest.descriptionRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Progress calculation
                    val prevKm = DestinationRegistry.destinations.takeWhile { it.id != activeDest.id }.lastOrNull()?.kmRequired ?: 0.0
                    val segmentTotal = (activeDest.kmRequired - prevKm).coerceAtLeast(0.1)
                    val segmentProgress = ((allTimeKm - prevKm).coerceAtLeast(0.0) / segmentTotal).coerceIn(0.0, 1.0).toFloat()
                    val percent = (segmentProgress * 100).toInt()

                    LinearProgressIndicator(
                        progress = { segmentProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(MaterialTheme.appShapes.pill)
                            .testTag("cyber_gate_progress_bar"),
                        color = primaryAccent,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(
                                R.string.trail_progress_format,
                                allTimeKm,
                                activeDest.kmRequired,
                                percent
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val remainingKm = maxOf(0.0, activeDest.kmRequired - allTimeKm)
                        Text(
                            text = "${MetricFormatter.formatDistance(remainingKm.toFloat())} ${stringResource(R.string.km_unit)} to Breach",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = secondaryAccent
                        )
                    }
                }
            }

            // ARCADE COMBO & OVERDRIVE MULTIPLIER BANNER
            Card(
                shape = MaterialTheme.appShapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cyber_gate_combo_banner")
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
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(MaterialTheme.appShapes.pill)
                                .background(errorAccent.copy(alpha = 0.2f))
                                .border(1.dp, errorAccent, MaterialTheme.appShapes.pill)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.cyber_gate_combo_format, 48),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = errorAccent
                            )
                        }

                        Column {
                            Text(
                                text = stringResource(R.string.cyber_gate_score_format, activeDest.arcadeScoreBonus),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = secondaryAccent
                            )
                            Text(
                                text = "Cadence Combo Active • 1.25x Overdrive Surge",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // OPTION C 4-TIER LOOT CACHE REWARDS GRID
            Column(
                modifier = Modifier.testTag("cyber_gate_loot_grid"),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = tertiaryAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "PORTAL LOOT CACHE DROPS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "BREACH REWARDS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryAccent
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                ) {
                    LootCacheCard(
                        emoji = activeDest.primaryLootEmoji,
                        title = activeDest.primaryLootName,
                        rarityTag = "[S]",
                        accentColor = secondaryAccent,
                        modifier = Modifier.weight(1f)
                    )
                    LootCacheCard(
                        emoji = "💾",
                        title = activeDest.secondaryLootName,
                        rarityTag = "[A]",
                        accentColor = primaryAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                ) {
                    LootCacheCard(
                        emoji = "✨",
                        title = stringResource(R.string.cyber_gate_loot_mystic_dust),
                        rarityTag = "[B]",
                        accentColor = tertiaryAccent,
                        modifier = Modifier.weight(1f)
                    )
                    LootCacheCard(
                        emoji = "💠",
                        title = stringResource(R.string.cyber_gate_loot_cyber_fragment),
                        rarityTag = "[C]",
                        accentColor = primaryAccent.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // CONTINUE RAID ACTION BUTTON
            Button(
                onClick = {},
                shape = MaterialTheme.appShapes.pill,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryAccent,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cyber_gate_continue_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                Text(
                    text = stringResource(R.string.cyber_gate_continue_raid),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
            }

            // COMPLETED / BREACHED DIMENSIONAL GATES SECTION
            Column(
                modifier = Modifier.testTag("breached_gates_list"),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                Text(
                    text = stringResource(R.string.completed_expeditions_title),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                if (completedList.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_completed_expeditions),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    completedList.forEach { dest ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.appShapes.small)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = dest.emoji, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.sm))
                                Text(
                                    text = stringResource(dest.nameRes),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "BREACHED",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = secondaryAccent,
                                    modifier = Modifier
                                        .clip(MaterialTheme.appShapes.pill)
                                        .background(secondaryAccent.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = secondaryAccent,
                                    modifier = Modifier.size(MaterialTheme.dimens.iconSmall)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LootCacheCard(
    emoji: String,
    title: String,
    rarityTag: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.appShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = rarityTag,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
