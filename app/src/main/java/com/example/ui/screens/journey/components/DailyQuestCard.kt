package com.example.ui.screens.journey.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.example.R
import com.example.domain.model.DailyQuest
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing
import com.example.util.MetricFormatter

@Composable
fun DailyQuestCard(
    quest: DailyQuest,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val primaryAccent = MaterialTheme.colorScheme.primary
    val secondaryAccent = MaterialTheme.colorScheme.secondary
    val tertiaryAccent = MaterialTheme.colorScheme.tertiary
    val cardBackground = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val trackBg = MaterialTheme.colorScheme.surface

    val borderBrush = if (quest.isCompleted) {
        Brush.horizontalGradient(listOf(secondaryAccent, primaryAccent))
    } else {
        Brush.horizontalGradient(listOf(primaryAccent, tertiaryAccent))
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        border = BorderStroke(1.dp, borderBrush),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .testTag("quest_card_${quest.id}")
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)
        ) {
            // Header Row: System Directive Tag & Rank Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.quest_rank_e_bounty),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = if (quest.isCompleted) secondaryAccent else primaryAccent,
                    letterSpacing = 1.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.quest_tap_to_inspect),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = tertiaryAccent
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = tertiaryAccent,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(MaterialTheme.dimens.minTouchTarget)
                            .clip(CircleShape)
                            .background(trackBg)
                            .border(1.dp, if (quest.isCompleted) secondaryAccent else primaryAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = quest.iconEmoji,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))

                    Column {
                        Text(
                            text = stringResource(quest.titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = onSurfaceColor
                        )
                        Text(
                            text = stringResource(quest.descRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurfaceVariantColor
                        )
                    }
                }

                // XP Reward Badge or Completed Check
                if (quest.isCompleted) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(secondaryAccent.copy(alpha = 0.15f))
                            .border(1.dp, secondaryAccent, MaterialTheme.appShapes.pill)
                            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.quest_completed_badge),
                            tint = secondaryAccent,
                            modifier = Modifier.size(MaterialTheme.dimens.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                        Text(
                            text = stringResource(R.string.quest_completed_badge),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = secondaryAccent
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.appShapes.pill)
                            .background(primaryAccent.copy(alpha = 0.15f))
                            .border(1.dp, primaryAccent, MaterialTheme.appShapes.pill)
                            .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                    ) {
                        Text(
                            text = stringResource(R.string.quest_reward_format, quest.rewardXp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = primaryAccent
                        )
                    }
                }
            }

            // High-Tech Cyber Progress Bar
            LinearProgressIndicator(
                progress = { quest.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(MaterialTheme.appShapes.pill),
                color = if (quest.isCompleted) secondaryAccent else primaryAccent,
                trackColor = trackBg
            )

            // Progress numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${MetricFormatter.formatSteps(quest.currentValue)} / ${MetricFormatter.formatSteps(quest.targetValue)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = onSurfaceVariantColor
                )
                Text(
                    text = "${(quest.progressPercent * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (quest.isCompleted) secondaryAccent else primaryAccent
                )
            }
        }
    }
}
