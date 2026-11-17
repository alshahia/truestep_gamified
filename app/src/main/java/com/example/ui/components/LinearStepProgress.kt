package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun LinearStepProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    fillBrush: Brush? = null,
    isCompleted: Boolean = false,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = tween(durationMillis = 500),
        label = "LinearStepProgressAnimation"
    )

    // Secondary accent is used for achievement/success moments
    val successColor = MaterialTheme.colorScheme.secondary
    val successBrush = Brush.horizontalGradient(listOf(successColor, successColor.copy(alpha = 0.85f)))
    val defaultPrimary = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(trackColor)
            .testTag("linear_step_progress_bar")
    ) {
        if (animatedProgress > 0f) {
            val finalBrush = when {
                isCompleted -> successBrush
                fillBrush != null -> fillBrush
                else -> Brush.horizontalGradient(listOf(defaultPrimary, defaultPrimary))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(finalBrush)
            )
        }
    }
}
