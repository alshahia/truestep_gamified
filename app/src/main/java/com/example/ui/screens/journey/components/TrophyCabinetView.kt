package com.example.ui.screens.journey.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.domain.content.AchievementRegistry
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing

@Composable
fun TrophyCabinetView(
    unlockedAchievementIds: Set<String>,
    modifier: Modifier = Modifier
) {
    val allAchievements = AchievementRegistry.allAchievements
    val unlockedCount = allAchievements.count { unlockedAchievementIds.contains(it.id) }
    val tertiaryAccent = MaterialTheme.colorScheme.tertiary
    val primaryAccent = MaterialTheme.colorScheme.primary
    val secondaryAccent = MaterialTheme.colorScheme.secondary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trophy_cabinet_view"),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
    ) {
        // Cabinet Header Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                Brush.horizontalGradient(listOf(tertiaryAccent.copy(alpha = 0.6f), primaryAccent.copy(alpha = 0.5f)))
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(MaterialTheme.spacing.lg),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.trophies_hall_of_honors),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$unlockedCount / ${allAchievements.size} UNLOCKED • S-RANK ARCHIVES",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
                        fontFamily = FontFamily.Monospace,
                        color = tertiaryAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(tertiaryAccent.copy(alpha = 0.15f))
                        .border(1.dp, tertiaryAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏆", style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        // Trophies List
        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm)) {
            allAchievements.forEach { achievement ->
                val isUnlocked = unlockedAchievementIds.contains(achievement.id)

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isUnlocked) tertiaryAccent.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 2.dp else 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trophy_item_${achievement.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(MaterialTheme.spacing.md),
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
                                    .background(
                                        if (isUnlocked) tertiaryAccent.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        1.dp,
                                        if (isUnlocked) tertiaryAccent else MaterialTheme.colorScheme.outline,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = achievement.iconEmoji,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(achievement.titleRes),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (isUnlocked) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(MaterialTheme.appShapes.pill)
                                                .background(tertiaryAccent.copy(alpha = 0.2f))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "+50 CP",
                                                fontSize = 8.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = FontFamily.Monospace,
                                                color = tertiaryAccent
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = if (isUnlocked) stringResource(achievement.requirementRes)
                                    else "[ ${stringResource(R.string.trophy_encrypted_status)} ] ${stringResource(achievement.requirementRes)}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    fontFamily = if (!isUnlocked) FontFamily.Monospace else null,
                                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }

                        if (isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = secondaryAccent,
                                modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(MaterialTheme.dimens.iconSmall)
                            )
                        }
                    }
                }
            }
        }
    }
}
