package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.data.local.DailySteps

@Composable
fun TrendChart(
    days: List<DailySteps>,
    goalTarget: Int = 10000,
    modifier: Modifier = Modifier
) {
    if (days.isEmpty()) return

    val barColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
    val successBarColor = MaterialTheme.colorScheme.secondary
    val maLineColor = MaterialTheme.colorScheme.tertiary
    val goalLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

    val averageSteps = (days.sumOf { it.totalSteps } / days.size.coerceAtLeast(1))
    val chartAccessibilityText = "Step trend chart for ${days.size} days. Average: $averageSteps steps per day. Target goal: $goalTarget."

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .semantics { contentDescription = chartAccessibilityText }
            .testTag("trend_chart")
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 16.dp)) {
            val maxSteps = maxOf(days.maxOfOrNull { it.totalSteps } ?: 1, goalTarget + 1000).toFloat()
            val canvasWidth = size.width
            val canvasHeight = size.height

            val barCount = days.size
            val barSpacing = canvasWidth / barCount
            val barWidth = (barSpacing * 0.6f).coerceIn(6f, 32f)

            // 1. Draw goal line (dashed)
            val goalY = canvasHeight - ((goalTarget / maxSteps) * canvasHeight)
            drawLine(
                color = goalLineColor,
                start = Offset(0f, goalY),
                end = Offset(canvasWidth, goalY),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
            )

            // 2. Draw bars
            val points = mutableListOf<Offset>()
            days.forEachIndexed { index, day ->
                val barHeight = ((day.totalSteps.toFloat() / maxSteps) * canvasHeight).coerceAtLeast(4f)
                val x = (index * barSpacing) + (barSpacing / 2f)
                val y = canvasHeight - barHeight

                val color = if (day.totalSteps >= day.goalSteps) successBarColor else barColor

                drawRoundRect(
                    color = color,
                    topLeft = Offset(x - (barWidth / 2f), y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )

                points.add(Offset(x, y))
            }

            // 3. Draw Moving Average (smooth curved line over bars)
            if (points.size >= 2) {
                val path = Path()
                path.moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    val midX = (prev.x + curr.x) / 2f
                    path.quadraticTo(prev.x, prev.y, midX, (prev.y + curr.y) / 2f)
                }
                path.lineTo(points.last().x, points.last().y)

                drawPath(
                    path = path,
                    color = maLineColor,
                    style = Stroke(width = 4f)
                )
            }
        }
    }
}
