package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.content.AchievementRegistry
import com.example.domain.content.DestinationRegistry
import com.example.domain.content.MultiplierRegistry
import com.example.domain.model.AchievementCategory
import com.example.domain.model.Destination
import com.example.ui.viewmodel.StepUiState
import com.example.util.LocalizationUtil

@Composable
fun AchievementsScreen(
    uiState: StepUiState,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_daily_goals),
        stringResource(R.string.tab_streaks),
        stringResource(R.string.tab_days),
        stringResource(R.string.tab_destinations)
    )

    val maxDaySteps = remember(uiState.allHistory) {
        uiState.allHistory.maxOfOrNull { it.totalSteps } ?: uiState.currentSteps
    }
    val totalActiveDays = remember(uiState.allHistory) {
        uiState.allHistory.size.coerceAtLeast(1)
    }
    val totalLifetimeKm = remember(uiState.allHistory) {
        uiState.allHistory.sumOf { it.cumulativeKm }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.achievements_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 20.dp,
            modifier = Modifier.fillMaxWidth().testTag("achievements_tab_row")
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(text = title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                    modifier = Modifier.testTag("achievements_tab_$index")
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            when (selectedTab) {
                0 -> DailyGoalsTab(maxDaySteps = maxDaySteps)
                1 -> StreaksTab(currentStreak = uiState.streak, bestStreak = uiState.bestStreak)
                2 -> ActiveDaysTab(totalDays = totalActiveDays)
                3 -> DestinationsTab(totalKm = totalLifetimeKm)
            }
        }
    }
}

@Composable
private fun DailyGoalsTab(maxDaySteps: Int) {
    val goals = remember {
        AchievementRegistry.allAchievements.filter { it.category == AchievementCategory.DAILY_GOAL }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(goals, key = { it.id }) { ach ->
            val requiredSteps = when (ach.id) {
                "goal_4k" -> 4000
                "goal_6k" -> 6000
                "goal_8k" -> 8000
                "goal_10k" -> 10000
                "goal_12k" -> 12000
                "goal_15k" -> 15000
                "goal_20k" -> 20000
                "goal_25k" -> 25000
                else -> 10000
            }
            val isUnlocked = maxDaySteps >= requiredSteps

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = ach.iconEmoji, fontSize = 28.sp)
                    Text(text = LocalizationUtil.getAchievementTitle(ach), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = LocalizationUtil.getAchievementRequirement(ach), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isUnlocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isUnlocked) stringResource(R.string.unlocked_badge) else stringResource(R.string.locked_badge),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUnlocked) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreaksTab(currentStreak: Int, bestStreak: Int) {
    val tiers = remember { MultiplierRegistry.tiers }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(tiers, key = { it.multiplierLabel }) { tier ->
            val isUnlocked = bestStreak >= tier.daysRequired

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = tier.emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "${tier.multiplierLabel} — ${LocalizationUtil.getStreakTierTitle(tier)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = stringResource(R.string.streak_days_consecutive_format, tier.daysRequired), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isUnlocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isUnlocked) stringResource(R.string.unlocked_badge) else "${bestStreak}/${tier.daysRequired}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUnlocked) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveDaysTab(totalDays: Int) {
    val daysAchievements = remember {
        AchievementRegistry.allAchievements.filter { it.category == AchievementCategory.ACTIVE_DAYS }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(daysAchievements, key = { it.id }) { ach ->
            val requiredDays = when (ach.id) {
                "days_1" -> 1
                "days_3" -> 3
                "days_7" -> 7
                "days_14" -> 14
                "days_30" -> 30
                "days_60" -> 60
                "days_90" -> 90
                "days_180" -> 180
                else -> 1
            }
            val isUnlocked = totalDays >= requiredDays

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUnlocked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = ach.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = LocalizationUtil.getAchievementTitle(ach), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = LocalizationUtil.getAchievementRequirement(ach), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isUnlocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isUnlocked) stringResource(R.string.unlocked_badge) else "$totalDays/$requiredDays",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isUnlocked) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationsTab(totalKm: Double) {
    val destinations = remember { DestinationRegistry.destinations }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(destinations, key = { it.id }) { dest ->
            val isCompleted = totalKm >= dest.kmRequired
            val progress = (totalKm / dest.kmRequired).toFloat().coerceIn(0f, 1f)

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = dest.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = LocalizationUtil.getDestinationName(dest), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.dest_total_km_format, dest.kmRequired), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(text = LocalizationUtil.getDestinationDescription(dest), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    // Thin progress line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }
    }
}
