package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing

@Composable
fun RatingReviewDialog(
    onRateOnStore: () -> Unit,
    onSendFeedback: () -> Unit,
    onDismiss: () -> Unit,
    onNeverAskAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStars by remember { mutableIntStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier.testTag("rating_dialog"),
        shape = MaterialTheme.appShapes.extraLarge,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
            ) {
                val starColor = MaterialTheme.colorScheme.tertiary
                Box(
                    modifier = Modifier
                        .size(MaterialTheme.dimens.minTouchTarget)
                        .clip(CircleShape)
                        .background(starColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = starColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.rating_prompt_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
            ) {
                Text(
                    text = stringResource(R.string.rating_prompt_body),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 5 Interactive Star Selectors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = MaterialTheme.spacing.xs),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (star in 1..5) {
                        val isSelected = star <= selectedStars
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = "Rate $star stars",
                            tint = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { selectedStars = star }
                                .padding(4.dp)
                                .testTag("rating_star_$star")
                        )
                    }
                }

                Text(
                    text = if (selectedStars >= 4) {
                        stringResource(R.string.rating_thank_you)
                    } else {
                        stringResource(R.string.rating_how_do_you_feel)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                Button(
                    onClick = onRateOnStore,
                    shape = MaterialTheme.appShapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rate_google_play_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(MaterialTheme.dimens.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                    Text(
                        text = stringResource(R.string.rating_rate_play_store),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (selectedStars < 4) {
                    TextButton(
                        onClick = onSendFeedback,
                        shape = MaterialTheme.appShapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_feedback_button")
                    ) {
                        Text(
                            text = stringResource(R.string.rating_feedback_btn),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onNeverAskAgain,
                    modifier = Modifier.testTag("rating_never_ask_button")
                ) {
                    Text(
                        text = stringResource(R.string.rating_never_ask),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("rating_maybe_later_button")
                ) {
                    Text(
                        text = stringResource(R.string.rating_maybe_later),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    )
}
