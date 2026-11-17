package com.example.ui.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R

@Composable
fun PauseButton(
    isPaused: Boolean,
    onTogglePause: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onTogglePause,
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .testTag("pause_toggle_button"),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = if (isPaused) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isPaused) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Icon(
            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
            contentDescription = stringResource(if (isPaused) R.string.resume else R.string.pause)
        )
        Text(
            text = " " + stringResource(if (isPaused) R.string.resume else R.string.pause),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
