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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
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
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing

@Composable
fun StreakVaultCard(
    currentStreak: Int,
    bestStreak: Int,
    bankedFreezes: Int,
    isProtectedToday: Boolean,
    modifier: Modifier = Modifier
) {
    val primaryAccent = MaterialTheme.colorScheme.primary
    val fireAccent = MaterialTheme.colorScheme.error
    val amberAccent = MaterialTheme.colorScheme.tertiary

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(fireAccent.copy(alpha = 0.6f), primaryAccent.copy(alpha = 0.6f))
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("streak_vault_card")
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(MaterialTheme.dimens.minTouchTarget)
                            .clip(CircleShape)
                            .background(fireAccent.copy(alpha = 0.15f))
                            .border(1.dp, fireAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = fireAccent,
                            modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                        )
                    }
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))
                    Column {
                        Text(
                            text = stringResource(R.string.streak_cryo_stasis_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.streak_label).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            fontFamily = FontFamily.Monospace,
                            color = fireAccent
                        )
                    }
                }

                // Banked Freezes Count Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(MaterialTheme.appShapes.pill)
                        .background(primaryAccent.copy(alpha = 0.15f))
                        .border(1.dp, primaryAccent, MaterialTheme.appShapes.pill)
                        .padding(horizontal = MaterialTheme.spacing.sm, vertical = MaterialTheme.spacing.xs)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = primaryAccent,
                        modifier = Modifier.size(MaterialTheme.dimens.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                    Text(
                        text = stringResource(R.string.banked_freezes_count, bankedFreezes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = primaryAccent
                    )
                }
            }

            // Streak Metric KPIs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentStreak",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = fireAccent
                    )
                    Text(
                        text = stringResource(R.string.streak_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$bestStreak",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = amberAccent
                    )
                    Text(
                        text = stringResource(R.string.best_day),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(2) { index ->
                            val isCharged = index < bankedFreezes
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (isCharged) primaryAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, if (isCharged) primaryAccent else MaterialTheme.colorScheme.outline, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (isCharged) primaryAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            if (index == 0) Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.banked_freezes_label),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Protection Active Status Banner if protected today
            if (isProtectedToday) {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = primaryAccent.copy(alpha = 0.12f)
                    ),
                    border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(MaterialTheme.spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = primaryAccent,
                            modifier = Modifier.size(MaterialTheme.dimens.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.xs))
                        Text(
                            text = stringResource(R.string.streak_protection_protected_today),
                            style = MaterialTheme.typography.bodySmall,
                            color = primaryAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Description
            Text(
                text = stringResource(R.string.streak_vault_desc),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Hint
            val daysUntilNext = 7 - (currentStreak % 7)
            val hintText = if (bankedFreezes >= 2) {
                stringResource(R.string.freeze_max_banked)
            } else {
                stringResource(R.string.earn_next_freeze_hint, daysUntilNext)
            }

            Text(
                text = hintText,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = primaryAccent
            )
        }
    }
}
