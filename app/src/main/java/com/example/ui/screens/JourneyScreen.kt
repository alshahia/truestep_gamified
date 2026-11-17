package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.domain.model.DailyQuest
import com.example.ui.screens.journey.components.DailyQuestCard
import com.example.ui.screens.journey.components.ExpeditionTrailCard
import com.example.ui.screens.journey.components.HeroLoadoutView
import com.example.ui.screens.journey.components.StreakVaultCard
import com.example.ui.screens.journey.components.TrophyCabinetView
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.spacing
import com.example.ui.viewmodel.StepUiState

private enum class JourneyTab(
    val titleRes: Int,
    val icon: ImageVector,
    val tag: String
) {
    QUESTS(R.string.tab_quests, Icons.Default.TaskAlt, "tab_quests"),
    HERO(R.string.tab_hero, Icons.Default.SportsMartialArts, "tab_hero"),
    EXPEDITIONS(R.string.tab_expeditions, Icons.Default.Explore, "tab_expeditions"),
    STREAK_VAULT(R.string.tab_streak_vault, Icons.Default.LocalFireDepartment, "tab_streak_vault"),
    TROPHIES(R.string.tab_trophies, Icons.Default.EmojiEvents, "tab_trophies")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyScreen(
    uiState: StepUiState,
    onEquipGear: (slotType: String, gearId: String) -> Unit = { _, _ -> },
    onTuneGear: (gearId: String) -> Unit = {},
    onAllocateStatPoint: (statType: String) -> Unit = {},
    onOpenQuestPopup: (DailyQuest) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = JourneyTab.entries
    val primaryAccent = MaterialTheme.colorScheme.primary
    val tertiaryAccent = MaterialTheme.colorScheme.tertiary
    val secondaryAccent = MaterialTheme.colorScheme.secondary

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.lg, vertical = MaterialTheme.spacing.md)
            .testTag("journey_screen"),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        // Screen Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.journey_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.journey_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Fluid Responsive Cyber Scrollable Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(12.dp))
                .testTag("journey_tab_row"),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = primaryAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = primaryAccent,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTabIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = stringResource(tab.titleRes).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isSelected) primaryAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(tab.titleRes),
                            tint = if (isSelected) primaryAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .testTag(tab.tag)
                )
            }
        }

        // Tab Content
        when (tabs[selectedTabIndex]) {
            JourneyTab.QUESTS -> {
                Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
                    // Quests Header Card: Solo Leveling System Directive Hub
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = BorderStroke(
                            1.dp,
                            Brush.horizontalGradient(listOf(primaryAccent.copy(alpha = 0.6f), tertiaryAccent.copy(alpha = 0.5f)))
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(MaterialTheme.spacing.md),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.daily_quests_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                FilledTonalButton(
                                    onClick = {
                                        val targetQuest = uiState.dailyQuests.firstOrNull { !it.isCompleted } ?: uiState.dailyQuests.firstOrNull()
                                        if (targetQuest != null) {
                                            onOpenQuestPopup(targetQuest)
                                        }
                                    },
                                    shape = MaterialTheme.appShapes.pill,
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = primaryAccent,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier.testTag("journey_summon_hud_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = stringResource(R.string.home_summon_quest_hud),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Text(
                                text = stringResource(
                                    R.string.home_quests_glance_format,
                                    uiState.completedQuestsCount,
                                    uiState.totalQuestsCount
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = primaryAccent,
                                fontWeight = FontWeight.Bold
                            )
                            if (uiState.completedQuestsCount == uiState.totalQuestsCount && uiState.totalQuestsCount > 0) {
                                Text(
                                    text = stringResource(R.string.quest_all_completed),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = secondaryAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Render list of quests (interactive with full Solo Leveling popup)
                    uiState.dailyQuests.forEach { quest ->
                        DailyQuestCard(
                            quest = quest,
                            onClick = { onOpenQuestPopup(quest) }
                        )
                    }
                }
            }

            JourneyTab.HERO -> {
                HeroLoadoutView(
                    hunterProfile = uiState.hunterProfile,
                    allGearItems = uiState.allGearItems,
                    onEquipGear = onEquipGear,
                    onTuneGear = onTuneGear,
                    onAllocateStatPoint = onAllocateStatPoint
                )
            }

            JourneyTab.EXPEDITIONS -> {
                val allTimeKm = uiState.allHistory.sumOf { it.cumulativeKm } + uiState.cumulativeKm
                ExpeditionTrailCard(
                    allTimeKm = allTimeKm,
                    nextDestination = uiState.nextDestination
                )
            }

            JourneyTab.STREAK_VAULT -> {
                StreakVaultCard(
                    currentStreak = uiState.streak,
                    bestStreak = uiState.bestStreak,
                    bankedFreezes = uiState.bankedStreakFreezes,
                    isProtectedToday = uiState.isStreakProtectedToday
                )
            }

            JourneyTab.TROPHIES -> {
                TrophyCabinetView(
                    unlockedAchievementIds = uiState.unlockedAchievementIds
                )
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.xl))
    }
}
