package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing
import com.example.util.MetricFormatter

@Composable
fun CelebrationDialog(
    steps: Int,
    distanceKm: Double,
    streak: Int,
    onShare: () -> Unit,
    onDismiss: () -> Unit,
    onRateApp: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val primaryAccent = MaterialTheme.colorScheme.primary
    val secondaryAccent = MaterialTheme.colorScheme.secondary
    val tertiaryAccent = MaterialTheme.colorScheme.tertiary
    val containerBg = MaterialTheme.colorScheme.surface
    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = containerBg,
        modifier = modifier
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(listOf(primaryAccent, tertiaryAccent)),
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("celebration_dialog"),
        shape = RoundedCornerShape(20.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(primaryAccent.copy(alpha = 0.15f))
                        .border(1.dp, primaryAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 26.sp
                    )
                }

                Text(
                    text = stringResource(R.string.celebration_title).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = onSurfaceColor,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "HUNTER ASCENSION PROTOCOL ACHIEVED",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryAccent,
                    letterSpacing = 1.sp
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
                    text = stringResource(R.string.celebration_praise),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = onSurfaceVariantColor
                )

                // Stylized Shareable Milestone Card Preview
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBg
                    ),
                    border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("shareable_milestone_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.md),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                    ) {
                        // Card Header Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = primaryAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = stringResource(R.string.share_card_brand_badge).uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = primaryAccent,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.appShapes.pill)
                                    .background(secondaryAccent.copy(alpha = 0.15f))
                                    .border(1.dp, secondaryAccent, MaterialTheme.appShapes.pill)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "VERIFIED",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = secondaryAccent
                                )
                            }
                        }

                        // Metric pills inside card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = MaterialTheme.spacing.xs),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = MetricFormatter.formatSteps(steps),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = onSurfaceColor
                                )
                                Text(
                                    text = stringResource(R.string.steps_unit).uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = onSurfaceVariantColor,
                                    fontSize = 9.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = MetricFormatter.formatDistance(distanceKm.toFloat()),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = primaryAccent
                                )
                                Text(
                                    text = stringResource(R.string.km_unit).uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = onSurfaceVariantColor,
                                    fontSize = 9.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$streak 🔥",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = tertiaryAccent
                                )
                                Text(
                                    text = stringResource(R.string.days_unit).uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = onSurfaceVariantColor,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onShare,
                shape = MaterialTheme.appShapes.pill,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryAccent,
                    contentColor = onPrimaryColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("celebration_share_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = onPrimaryColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                Text(
                    text = stringResource(R.string.celebration_share_btn).uppercase(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp
                )
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onRateApp != null) {
                    TextButton(
                        onClick = onRateApp,
                        modifier = Modifier.testTag("celebration_rate_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = tertiaryAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.rate_app_title).uppercase(),
                            color = tertiaryAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("celebration_dismiss_button")
                ) {
                    Text(
                        text = stringResource(R.string.celebration_dismiss).uppercase(),
                        color = onSurfaceVariantColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    )
}
