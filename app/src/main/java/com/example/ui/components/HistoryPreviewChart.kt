package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.model.ChartRangeData
import com.example.ui.model.HistoryChartRange

@Composable
fun HistoryPreviewChart(
    chartData: ChartRangeData?,
    selectedRange: HistoryChartRange,
    onSelectRange: (HistoryChartRange) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToHistory: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    // Scroll to the end (most recent day) when range changes
    LaunchedEffect(selectedRange, chartData) {
        if (chartData != null && chartData.bars.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Card(
        modifier = modifier.testTag("history_preview_chart_card"),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Title & Link to Full History
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.history_chart),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (chartData != null) {
                        Text(
                            text = stringResource(chartData.titleRes),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TextButton(
                    onClick = onNavigateToHistory,
                    modifier = Modifier.testTag("view_full_history_button")
                ) {
                    Text(
                        text = stringResource(R.string.view_full_history_link),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Segmented Chips (Daily, Weekly, Monthly)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedRange == HistoryChartRange.DAILY,
                    onClick = { onSelectRange(HistoryChartRange.DAILY) },
                    label = { Text(stringResource(R.string.daily), style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f).testTag("range_chip_daily"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                FilterChip(
                    selected = selectedRange == HistoryChartRange.WEEKLY,
                    onClick = { onSelectRange(HistoryChartRange.WEEKLY) },
                    label = { Text(stringResource(R.string.weekly), style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f).testTag("range_chip_weekly"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                FilterChip(
                    selected = selectedRange == HistoryChartRange.MONTHLY,
                    onClick = { onSelectRange(HistoryChartRange.MONTHLY) },
                    label = { Text(stringResource(R.string.monthly), style = MaterialTheme.typography.labelSmall) },
                    modifier = Modifier.weight(1f).testTag("range_chip_monthly"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }

            // Chart Scroll Container
            if (chartData != null && chartData.bars.isNotEmpty()) {
                val goalValue = chartData.goalValue
                val maxValue = chartData.maxValue.coerceAtLeast(goalValue + 1)
                val goalFraction = (goalValue.toFloat() / maxValue).coerceIn(0.1f, 0.95f)

                val goalTagText = if (goalValue >= 1000) {
                    "${(goalValue / 1000)}k"
                } else goalValue.toString()

                val outlineColor = MaterialTheme.colorScheme.outlineVariant

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                        .testTag("chart_bars_scroll_box")
                ) {
                    // Dashed Goal Line across the full width
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .drawBehind {
                                val y = size.height * (1f - goalFraction)
                                drawLine(
                                    color = outlineColor,
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                                    strokeWidth = 1.5.dp.toPx()
                                )
                            }
                    )

                    // Goal Tag on trailing edge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(y = 96.dp * (1f - goalFraction) - 10.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = goalTagText,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Horizontally scrollable bars
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .horizontalScroll(scrollState)
                            .padding(end = 36.dp), // Give room for goal tag
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        chartData.bars.forEach { bar ->
                            val barFraction = (bar.value.toFloat() / maxValue).coerceIn(0.04f, 1f)
                            val animatedBarFraction by animateFloatAsState(
                                targetValue = barFraction,
                                label = "chart_bar_height"
                            )

                            val barColor = when {
                                bar.isCurrent -> MaterialTheme.colorScheme.primary
                                bar.isGoalMet -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            Column(
                                modifier = Modifier
                                    .width(28.dp)
                                    .fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .fillMaxHeight(animatedBarFraction * 0.82f) // Leave room for label below
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(barColor)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = bar.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    fontWeight = if (bar.isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = if (bar.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
