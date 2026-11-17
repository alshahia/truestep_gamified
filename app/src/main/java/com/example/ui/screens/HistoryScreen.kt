package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.DailySteps
import com.example.ui.components.DayDetailSheet
import com.example.ui.theme.tokens.appShapes
import com.example.ui.viewmodel.StepUiState
import com.example.util.MetricFormatter

enum class HistoryFilter {
    ALL,
    GOALS_MET,
    WITH_NOTES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: StepUiState,
    onSaveNotes: (date: String, notes: String) -> Unit,
    onNavigateToAnalytics: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeFilter by remember { mutableStateOf(HistoryFilter.ALL) }
    var selectedDayForDetail by remember { mutableStateOf<DailySteps?>(null) }
    val sheetState = rememberModalBottomSheetState()

    val allDays = uiState.allHistory
    val primaryAccent = MaterialTheme.colorScheme.primary
    val secondaryAccent = MaterialTheme.colorScheme.secondary
    val tertiaryAccent = MaterialTheme.colorScheme.tertiary
    val cardBackground = MaterialTheme.colorScheme.surfaceVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    val filteredDays = remember(allDays, activeFilter) {
        when (activeFilter) {
            HistoryFilter.ALL -> allDays
            HistoryFilter.GOALS_MET -> allDays.filter { it.totalSteps >= it.goalSteps }
            HistoryFilter.WITH_NOTES -> allDays.filter { it.notes.isNotBlank() }
        }
    }

    val averageSteps = remember(allDays) {
        if (allDays.isEmpty()) 0 else allDays.sumOf { it.totalSteps } / allDays.size
    }

    val bestDay = remember(allDays) {
        allDays.maxByOrNull { it.totalSteps }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Title & Summary Strip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = stringResource(R.string.history_mission_archives_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = onSurfaceColor
                )
                Text(
                    text = stringResource(R.string.history_mission_archives_subtitle),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = onSurfaceVariantColor
                )
            }

            FilledTonalButton(
                onClick = onNavigateToAnalytics,
                shape = MaterialTheme.appShapes.pill,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = surfaceColor,
                    contentColor = primaryAccent
                ),
                border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.5f)),
                modifier = Modifier.testTag("history_analytics_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Insights,
                    contentDescription = stringResource(R.string.analytics_title),
                    tint = primaryAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.nav_analytics).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Summary Hero Card: Lifetime Tactical Dossier
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            border = BorderStroke(
                1.dp,
                Brush.horizontalGradient(listOf(primaryAccent.copy(alpha = 0.5f), tertiaryAccent.copy(alpha = 0.5f)))
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.history_lifetime_dossier),
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = primaryAccent.copy(alpha = 0.85f),
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.weekly_average).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = onSurfaceVariantColor
                        )
                        Text(
                            text = MetricFormatter.formatSteps(averageSteps),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryAccent
                        )
                    }

                    if (bestDay != null) {
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = secondaryAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(R.string.best_day).uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = onSurfaceVariantColor
                                )
                            }
                            Text(
                                text = "${MetricFormatter.formatSteps(bestDay.totalSteps)} [S]",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = secondaryAccent
                            )
                        }
                    }
                }
            }
        }

        // Cyberpunk Filter chips (All, Goals Met, Notes)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = activeFilter == HistoryFilter.ALL,
                onClick = { activeFilter = HistoryFilter.ALL },
                label = {
                    Text(
                        text = stringResource(R.string.all_filter, allDays.size).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (activeFilter == HistoryFilter.ALL) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryAccent.copy(alpha = 0.15f),
                    selectedLabelColor = primaryAccent,
                    containerColor = surfaceColor,
                    labelColor = onSurfaceVariantColor
                ),
                border = BorderStroke(1.dp, if (activeFilter == HistoryFilter.ALL) primaryAccent else outlineVariantColor),
                modifier = Modifier.testTag("filter_all_chip")
            )
            FilterChip(
                selected = activeFilter == HistoryFilter.GOALS_MET,
                onClick = { activeFilter = HistoryFilter.GOALS_MET },
                label = {
                    Text(
                        text = stringResource(R.string.goals_met_filter).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (activeFilter == HistoryFilter.GOALS_MET) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = secondaryAccent.copy(alpha = 0.15f),
                    selectedLabelColor = secondaryAccent,
                    containerColor = surfaceColor,
                    labelColor = onSurfaceVariantColor
                ),
                border = BorderStroke(1.dp, if (activeFilter == HistoryFilter.GOALS_MET) secondaryAccent else outlineVariantColor),
                modifier = Modifier.testTag("filter_goals_met_chip")
            )
            FilterChip(
                selected = activeFilter == HistoryFilter.WITH_NOTES,
                onClick = { activeFilter = HistoryFilter.WITH_NOTES },
                label = {
                    Text(
                        text = stringResource(R.string.with_notes_filter).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (activeFilter == HistoryFilter.WITH_NOTES) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = tertiaryAccent.copy(alpha = 0.15f),
                    selectedLabelColor = tertiaryAccent,
                    containerColor = surfaceColor,
                    labelColor = onSurfaceVariantColor
                ),
                border = BorderStroke(1.dp, if (activeFilter == HistoryFilter.WITH_NOTES) tertiaryAccent else outlineVariantColor),
                modifier = Modifier.testTag("filter_with_notes_chip")
            )
        }

        // History Daily Records List
        if (filteredDays.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_history_yet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = onSurfaceVariantColor
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredDays, key = { it.date }) { day ->
                    DayRowItem(
                        day = day,
                        onClick = { selectedDayForDetail = day }
                    )
                }
            }
        }
    }

    if (selectedDayForDetail != null) {
        DayDetailSheet(
            day = selectedDayForDetail,
            sheetState = sheetState,
            onDismiss = { selectedDayForDetail = null },
            onSaveNotes = onSaveNotes
        )
    }
}

@Composable
private fun DayRowItem(
    day: DailySteps,
    onClick: () -> Unit
) {
    val isAchieved = day.totalSteps >= day.goalSteps
    val progress = if (day.goalSteps > 0) (day.totalSteps.toFloat() / day.goalSteps).coerceIn(0f, 1f) else 0f

    val gradeTag = when {
        isAchieved -> "S"
        progress >= 0.8f -> "A"
        progress >= 0.5f -> "B"
        else -> "C"
    }

    val gradeColor = when (gradeTag) {
        "S" -> MaterialTheme.colorScheme.secondary
        "A" -> MaterialTheme.colorScheme.primary
        "B" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("day_row_${day.date}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, gradeColor.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Clearance Grade Badge
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(gradeColor.copy(alpha = 0.18f))
                            .border(0.5.dp, gradeColor, MaterialTheme.appShapes.pill)
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "GRADE $gradeTag",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = gradeColor
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = day.date,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (day.isRestDay) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.rest_badge),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                    if (day.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = stringResource(R.string.has_note),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (day.notes.isNotBlank()) {
                    Text(
                        text = day.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Segmented Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(gradeColor)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = MetricFormatter.formatSteps(day.totalSteps),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isAchieved) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${MetricFormatter.formatDistance(day.cumulativeKm.toFloat())} km",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = gradeColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
