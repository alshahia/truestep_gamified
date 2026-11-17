package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.tokens.appShapes

@Composable
fun HeroStepRing(
    steps: Int,
    goal: Int,
    percent: Int,
    isPaused: Boolean,
    onTogglePause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (goal > 0) (steps.toFloat() / goal).coerceIn(0f, 1.5f) else 0f
    val isGoalMet = steps >= goal && goal > 0

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "ring_progress"
    )

    val activeColor = when {
        isPaused -> MaterialTheme.colorScheme.tertiary
        isGoalMet -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }

    val animatedArcColor by animateColorAsState(
        targetValue = activeColor,
        animationSpec = tween(durationMillis = 400),
        label = "ring_color"
    )

    val cd = "${steps} ${stringResource(R.string.steps_unit)}, $percent% ${stringResource(R.string.of_goal)}"

    Card(
        modifier = modifier
            .testTag("hero_step_ring_card"),
        shape = MaterialTheme.appShapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            // Top-Start HUD Telemetry Label
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isPaused) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = if (isPaused) stringResource(R.string.telemetry_standby) else stringResource(R.string.telemetry_arc_reactor),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.8.sp),
                    fontWeight = FontWeight.Bold,
                    color = if (isPaused) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Top-End Dedicated Tactical Pause / Resume button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(MaterialTheme.appShapes.pill)
                    .background(
                        if (isPaused) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
            ) {
                IconButton(
                    onClick = onTogglePause,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("hero_ring_toggle")
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = stringResource(if (isPaused) R.string.resume_tracking_desc else R.string.pause_tracking_desc),
                        tint = if (isPaused) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Central Sci-Fi Arc Reactor Dial (Entire ring clickable to toggle pause/resume)
            val pauseActionLabel = stringResource(if (isPaused) R.string.resume_tracking_desc else R.string.pause_tracking_desc)

            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(196.dp)
                    .clip(CircleShape)
                    .clickable(
                        onClickLabel = pauseActionLabel,
                        role = Role.Button,
                        onClick = onTogglePause
                    )
                    .testTag("hero_ring_dial")
                    .semantics {
                        contentDescription = "$cd, $pauseActionLabel"
                    },
                contentAlignment = Alignment.Center
            ) {
                // Background radial cyber glow
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    animatedArcColor.copy(alpha = 0.14f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                val ringTrackColor = MaterialTheme.colorScheme.surfaceVariant
                val ringOutlineVariant = MaterialTheme.colorScheme.outlineVariant

                // Concentric Tech Ticks & Glow Arc Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val outerTickRadius = size.minDimension / 2f - 2.dp.toPx()

                    // Draw 48 Radial HUD Dial Ticks
                    for (i in 0 until 48) {
                        val angleRad = Math.toRadians((i * (360f / 48) - 90).toDouble())
                        val isMajor = i % 4 == 0
                        val tickLen = if (isMajor) 6.dp.toPx() else 3.dp.toPx()
                        val start = Offset(
                            (center.x + (outerTickRadius - tickLen) * Math.cos(angleRad)).toFloat(),
                            (center.y + (outerTickRadius - tickLen) * Math.sin(angleRad)).toFloat()
                        )
                        val end = Offset(
                            (center.x + outerTickRadius * Math.cos(angleRad)).toFloat(),
                            (center.y + outerTickRadius * Math.sin(angleRad)).toFloat()
                        )
                        drawLine(
                            color = if (isMajor) animatedArcColor.copy(alpha = 0.75f)
                            else ringOutlineVariant.copy(alpha = 0.4f),
                            start = start,
                            end = end,
                            strokeWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx()
                        )
                    }

                    // Background Track Circle
                    val arcStroke = 11.dp.toPx()
                    drawCircle(
                        color = ringTrackColor,
                        style = Stroke(width = arcStroke),
                        radius = outerTickRadius - 14.dp.toPx()
                    )

                    // Thin inner tech boundary ring
                    drawCircle(
                        color = ringOutlineVariant.copy(alpha = 0.25f),
                        style = Stroke(width = 1.dp.toPx()),
                        radius = outerTickRadius - 21.dp.toPx()
                    )

                    // Multi-pass Glowing Progress Arc
                    val sweepAngle = (animatedProgress * 360f).coerceAtMost(360f)
                    val arcRadius = outerTickRadius - 14.dp.toPx()
                    val arcTopLeft = Offset(center.x - arcRadius, center.y - arcRadius)
                    val arcSize = androidx.compose.ui.geometry.Size(arcRadius * 2, arcRadius * 2)

                    if (sweepAngle > 0f) {
                        // Pass 1: Semi-transparent neon bloom
                        drawArc(
                            color = animatedArcColor.copy(alpha = 0.35f),
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = arcStroke + 6.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Pass 2: Sharp electric core stroke
                        drawArc(
                            color = animatedArcColor,
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = arcTopLeft,
                            size = arcSize,
                            style = Stroke(width = arcStroke, cap = StrokeCap.Round)
                        )

                        // Pass 3: Leading Edge High-Energy Spark Bead
                        val tipRad = Math.toRadians((sweepAngle - 90).toDouble())
                        val beadCenter = Offset(
                            (center.x + arcRadius * Math.cos(tipRad)).toFloat(),
                            (center.y + arcRadius * Math.sin(tipRad)).toFloat()
                        )
                        drawCircle(color = Color.White, radius = 3.5.dp.toPx(), center = beadCenter)
                        drawCircle(color = animatedArcColor.copy(alpha = 0.6f), radius = 7.dp.toPx(), center = beadCenter)
                    }
                }

                // Center Reactor Readout
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    val stepFontSize = if (steps >= 100_000) 24.sp else if (steps >= 10_000) 28.sp else 32.sp
                    Text(
                        text = "%,d".format(steps),
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = stepFontSize),
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.testTag("ring_step_count_text")
                    )

                    Text(
                        text = stringResource(R.string.steps_unit).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 1.2.sp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Glowing Overdrive Charge Pill
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(animatedArcColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isGoalMet) stringResource(R.string.overdrive_charge_badge) else stringResource(R.string.charge_badge_format, percent),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            fontWeight = FontWeight.Bold,
                            color = animatedArcColor,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Explicit tap to pause / resume hint pill (Auto-fit into bottom chord)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("ring_pause_hint")
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = null,
                            tint = if (isPaused) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = stringResource(if (isPaused) R.string.tap_to_resume else R.string.tap_to_pause).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                            fontWeight = FontWeight.Bold,
                            color = if (isPaused) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

