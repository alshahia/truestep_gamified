package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HourlyScrubRibbon(
    hourlySteps: List<Int>,
    selectedHour: Int?,
    onScrubHour: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLocalHour = remember { LocalTime.now().hour }
    val hoursCount = 24 // Full 24-hour day: 00:00 to 23:00

    val isScrubbing = selectedHour != null
    val coroutineScope = rememberCoroutineScope()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val maxStepsInHour = remember(hourlySteps) {
        (hourlySteps.maxOrNull() ?: 1).coerceAtLeast(100)
    }

    val peakHour = remember(hourlySteps) {
        (0 until hoursCount).maxByOrNull { hourlySteps.getOrElse(it) { 0 } } ?: 12
    }

    val displayTimeLabel = remember(selectedHour) {
        if (selectedHour != null) String.format(Locale.getDefault(), "%02d:00", selectedHour.coerceIn(0, 23)) else ""
    }

    val barWidth = 26.dp
    val barSpacing = MaterialTheme.spacing.sm
    val itemTotalWidth = barWidth + barSpacing
    val density = LocalDensity.current
    val itemTotalWidthPx = with(density) { itemTotalWidth.toPx() }

    val scrollState = rememberScrollState()

    // Sync scroll when user scrolls: update selected hour while user drags/scrolls
    LaunchedEffect(scrollState, itemTotalWidthPx) {
        snapshotFlow { Pair(scrollState.isScrollInProgress, scrollState.value) }
            .collect { (isScrolling, scrollValue) ->
                if (isScrolling && itemTotalWidthPx > 0f) {
                    val computedHour = (scrollValue / itemTotalWidthPx).roundToInt().coerceIn(0, hoursCount - 1)
                    if (selectedHour != computedHour) {
                        onScrubHour(computedHour)
                    }
                }
            }
    }

    Card(
        modifier = modifier.testTag("hourly_scrub_card"),
        shape = MaterialTheme.appShapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.dimens.elevationLow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            // Header Row: Title & Active Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.day_shape),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isScrubbing) {
                            stringResource(R.string.elapsed_so_far)
                        } else {
                            stringResource(R.string.full_day)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isScrubbing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                ) {
                    if (isScrubbing) {
                        Text(
                            text = displayTimeLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Clear scrub button to restore full-day view
                        AssistChip(
                            onClick = { onScrubHour(null) },
                            label = { Text(stringResource(R.string.full_day), style = MaterialTheme.typography.labelSmall) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.full_day),
                                    modifier = Modifier.size(MaterialTheme.dimens.iconSmall)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = MaterialTheme.appShapes.medium,
                            modifier = Modifier.height(28.dp)
                        )
                    } else {
                        // In Full Day mode, display today's total tag
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.appShapes.small)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                        ) {
                            Text(
                                text = stringResource(R.string.full_day),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Scrollable Timeline Chart with Anchored Center Pin
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .testTag("scrub_track_gesture_box")
            ) {
                val viewportWidth = maxWidth
                val halfViewportPadding = (viewportWidth - barWidth) / 2

                // 1. Horizontally scrollable row of bars
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(scrollState),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Leading spacer: allows hour 0 to be centered under the cursor
                    Spacer(modifier = Modifier.width(halfViewportPadding))

                    // 24 Hour Bars (00:00 to 23:00)
                    for (hour in 0 until hoursCount) {
                        val steps = hourlySteps.getOrElse(hour) { 0 }
                        val heightFraction = (steps.toFloat() / maxStepsInHour).coerceIn(0.08f, 1f)
                        val isFuture = hour > currentLocalHour
                        val isPeak = hour == peakHour && steps > 0
                        val isCurrentHour = hour == currentLocalHour
                        val isSelected = hour == selectedHour

                        val barColor = when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            isCurrentHour -> MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                            isPeak -> MaterialTheme.colorScheme.tertiary
                            isFuture -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            steps > 0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        }

                        val animatedHeightFraction by animateFloatAsState(
                            targetValue = heightFraction,
                            label = "hourly_bar_height_$hour"
                        )

                        Column(
                            modifier = Modifier
                                .testTag("hourly_bar_$hour")
                                .width(barWidth)
                                .fillMaxHeight()
                                .clickable {
                                    if (selectedHour == hour) {
                                        onScrubHour(null) // Unselect on repeat tap
                                    } else {
                                        onScrubHour(hour)
                                        coroutineScope.launch {
                                            val targetOffset = (hour * itemTotalWidthPx).roundToInt()
                                            scrollState.animateScrollTo(targetOffset)
                                        }
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // Vertical Bar
                            Box(
                                modifier = Modifier
                                    .width(if (isSelected) barWidth else 20.dp)
                                    .fillMaxHeight(animatedHeightFraction * 0.72f)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(barColor)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Hour text label (e.g., "00", "06", "12", "18", "23")
                            Text(
                                text = String.format(Locale.getDefault(), "%02d", hour),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected || isCurrentHour) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    isCurrentHour -> MaterialTheme.colorScheme.onSurface
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                }
                            )
                        }

                        // Inter-bar spacing (except after the final bar)
                        if (hour < hoursCount - 1) {
                            Spacer(modifier = Modifier.width(barSpacing))
                        }
                    }

                    // Trailing spacer: allows hour 23 to be centered under the cursor
                    Spacer(modifier = Modifier.width(halfViewportPadding))
                }

                // 2. Anchored Center Pin Indicator (Active Cursor)
                if (isScrubbing) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxHeight()
                    ) {
                        // Playhead top dot
                        Box(
                            modifier = Modifier
                                .size(MaterialTheme.spacing.sm)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        // Playhead vertical needle
                        Box(
                            modifier = Modifier
                                .width(MaterialTheme.spacing.xxs)
                                .fillMaxHeight(0.72f)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }

            // Timeline Legend: Start of Day -> Noon -> End of Day
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRtl) "23:00 (${stringResource(R.string.end_of_day)})" else "00:00 (${stringResource(R.string.start_of_day)})",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = "12:00",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = if (isRtl) "00:00 (${stringResource(R.string.start_of_day)})" else "23:00 (${stringResource(R.string.end_of_day)})",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }
    }
}
