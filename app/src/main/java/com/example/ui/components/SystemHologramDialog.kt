package com.example.ui.components

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.spacing

@Composable
fun SystemHologramDialog(
    titleRes: Int,
    bodyText: String,
    eventType: String = "INFO",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryAccent = MaterialTheme.colorScheme.primary
    val secondaryAccent = MaterialTheme.colorScheme.secondary
    val tertiaryAccent = MaterialTheme.colorScheme.tertiary
    val errorColor = MaterialTheme.colorScheme.error
    val backdropColor = MaterialTheme.colorScheme.surface
    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    val eventAccentColor = when (eventType.uppercase()) {
        "LEVEL_UP" -> primaryAccent
        "QUEST_ARRIVAL" -> secondaryAccent
        "GATE_BREACH" -> errorColor
        else -> primaryAccent
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(backdropColor)
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(eventAccentColor, eventAccentColor.copy(alpha = 0.3f), tertiaryAccent)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .testTag("system_hologram_dialog")
        ) {
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.lg),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
            ) {
                // TOP SYSTEM HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(eventAccentColor)
                        )
                        Text(
                            text = stringResource(R.string.system_window_header),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            color = eventAccentColor,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(eventAccentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = eventType.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = eventAccentColor
                        )
                    }
                }

                // Cyan Technical Divider Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(eventAccentColor, Color.Transparent)
                            )
                        )
                )

                // DIALOG CONTENT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(eventAccentColor.copy(alpha = 0.15f))
                            .border(1.dp, eventAccentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = when (eventType.uppercase()) {
                                "LEVEL_UP" -> Icons.Default.Bolt
                                "QUEST_ARRIVAL", "DAILY_QUEST" -> Icons.Default.TaskAlt
                                "GATE_BREACH" -> Icons.Default.AutoAwesome
                                else -> Icons.Default.Bolt
                            },
                            contentDescription = null,
                            tint = eventAccentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs)
                    ) {
                        Text(
                            text = stringResource(titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = onSurfaceColor
                        )
                        Text(
                            text = bodyText.ifEmpty {
                                stringResource(R.string.system_level_up_body, 15)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = onSurfaceVariantColor,
                            lineHeight = 18.sp
                        )
                    }
                }

                // High-Stakes Solo Leveling Penalty Zone Warning Callout
                if (eventType.uppercase() in listOf("DAILY_QUEST", "QUEST_ARRIVAL")) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(errorColor.copy(alpha = 0.12f))
                            .border(1.dp, errorColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "⚠",
                                fontSize = 12.sp,
                                color = errorColor
                            )
                            Text(
                                text = stringResource(R.string.quest_penalty_warning_short),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = errorColor,
                                lineHeight = 13.sp
                            )
                        }
                    }

                    // Itemized Quest Rewards Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(MaterialTheme.appShapes.small)
                                .background(cardBg)
                                .border(0.5.dp, primaryAccent.copy(alpha = 0.3f), MaterialTheme.appShapes.small)
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+150 EXP ⚡",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryAccent
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(MaterialTheme.appShapes.small)
                                .background(cardBg)
                                .border(0.5.dp, secondaryAccent.copy(alpha = 0.3f), MaterialTheme.appShapes.small)
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+10 SPARKS 💎",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = secondaryAccent
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(MaterialTheme.appShapes.small)
                                .background(cardBg)
                                .border(0.5.dp, tertiaryAccent.copy(alpha = 0.3f), MaterialTheme.appShapes.small)
                                .padding(vertical = 4.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+1 STAT PT ✨",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = tertiaryAccent
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ACTION BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = MaterialTheme.appShapes.pill,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = onSurfaceVariantColor
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("system_hologram_dismiss_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.cancel),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        shape = MaterialTheme.appShapes.pill,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = eventAccentColor,
                            contentColor = onPrimaryColor
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("system_hologram_confirm_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.system_confirm_button),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}
