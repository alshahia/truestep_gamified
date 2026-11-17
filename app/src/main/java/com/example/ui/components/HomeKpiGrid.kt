package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing
import com.example.util.MetricFormatter

@Composable
fun HomeKpiGrid(
    stepsInHour: Int,
    distanceKm: Double,
    caloriesKcal: Int,
    shareOfDayPercent: Int,
    isScrubbing: Boolean,
    isPaused: Boolean,
    modifier: Modifier = Modifier,
    stepsDeltaInHour: Int = 0,
    activeMinutes: Int = 0
) {
    val stepsLabel = if (isScrubbing) {
        if (stepsDeltaInHour > 0) {
            "${stringResource(R.string.elapsed_so_far)} (+${MetricFormatter.formatSteps(stepsDeltaInHour)})"
        } else {
            stringResource(R.string.elapsed_so_far)
        }
    } else {
        stringResource(R.string.today_steps)
    }

    val progressLabel = if (isScrubbing) {
        stringResource(R.string.share_of_day)
    } else if (activeMinutes > 0) {
        "${stringResource(R.string.goal_progress)} • ${stringResource(R.string.active_time_mins_format, activeMinutes)}"
    } else {
        stringResource(R.string.goal_progress)
    }

    val progressIcon = if (isScrubbing) Icons.Default.Schedule else Icons.Default.Flag

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
        ) {
            KpiTile(
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                value = MetricFormatter.formatSteps(stepsInHour),
                label = stepsLabel,
                accentColor = MaterialTheme.colorScheme.primary,
                isPaused = isPaused,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_steps_hour")
            )

            KpiTile(
                icon = Icons.Default.Place,
                value = MetricFormatter.formatDistance(distanceKm.toFloat()),
                label = stringResource(R.string.km_unit),
                accentColor = MaterialTheme.colorScheme.secondary,
                isPaused = isPaused,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_distance")
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
        ) {
            KpiTile(
                icon = Icons.Default.LocalFireDepartment,
                value = MetricFormatter.formatCalories(caloriesKcal),
                label = stringResource(R.string.kcal_unit),
                accentColor = MaterialTheme.colorScheme.tertiary,
                isPaused = isPaused,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_calories")
            )

            KpiTile(
                icon = progressIcon,
                value = MetricFormatter.formatPercentage(shareOfDayPercent),
                label = progressLabel,
                accentColor = MaterialTheme.colorScheme.primary,
                isPaused = isPaused,
                modifier = Modifier
                    .weight(1f)
                    .testTag("kpi_share_of_day")
            )
        }
    }
}

@Composable
private fun KpiTile(
    icon: ImageVector,
    value: String,
    label: String,
    accentColor: Color,
    isPaused: Boolean,
    modifier: Modifier = Modifier
) {
    val activeAccent = if (isPaused) MaterialTheme.colorScheme.tertiary else accentColor

    Card(
        modifier = modifier,
        shape = MaterialTheme.appShapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(activeAccent.copy(alpha = 0.15f), MaterialTheme.appShapes.pill),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = activeAccent,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Decorative Mini HUD Corner Indicator
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(2.dp)
                        .background(activeAccent.copy(alpha = 0.6f))
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp, letterSpacing = 0.3.sp),
                color = activeAccent.copy(alpha = 0.85f),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

