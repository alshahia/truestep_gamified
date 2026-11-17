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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.local.DailySteps
import com.example.ui.theme.tokens.appShapes
import com.example.util.MetricFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailSheet(
    day: DailySteps?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSaveNotes: (date: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (day == null) return

    val primaryAccent = MaterialTheme.colorScheme.primary
    val secondaryAccent = MaterialTheme.colorScheme.secondary
    val tertiaryAccent = MaterialTheme.colorScheme.tertiary
    val containerBg = MaterialTheme.colorScheme.surface
    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val outlineColor = MaterialTheme.colorScheme.outline

    var notesText by remember(day.date, day.notes) { mutableStateOf(day.notes) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = containerBg,
        modifier = modifier.testTag("day_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.day_detail_title).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = onSurfaceColor
                    )
                    Text(
                        text = "TACTICAL DATE ARCHIVE: ${day.date}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = primaryAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Confidence badge
                val badgeText = stringResource(com.example.util.LocalizationUtil.getConfidenceBadgeRes(day.confidence))
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.appShapes.pill)
                        .background(secondaryAccent.copy(alpha = 0.15f))
                        .border(1.dp, secondaryAccent, MaterialTheme.appShapes.pill)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        color = secondaryAccent,
                        fontSize = 9.sp
                    )
                }
            }

            // Hero telemetry stats
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(primaryAccent.copy(alpha = 0.6f), tertiaryAccent.copy(alpha = 0.5f)))
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = MetricFormatter.formatSteps(day.totalSteps),
                            style = MaterialTheme.typography.headlineMedium,
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
                            text = "${day.cumulativeKm} km",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryAccent
                        )
                        Text(
                            text = stringResource(R.string.distance_label).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = onSurfaceVariantColor,
                            fontSize = 9.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${day.caloriesKcal} kcal",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = secondaryAccent
                        )
                        Text(
                            text = stringResource(R.string.calories_burned).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = onSurfaceVariantColor,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Source transparency block
            val confidenceBadge = stringResource(com.example.util.LocalizationUtil.getConfidenceBadgeRes(day.confidence))
            Text(
                text = stringResource(R.string.source_status_format, day.dataSource, confidenceBadge),
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = onSurfaceVariantColor,
                fontSize = 11.sp
            )

            // Notes input
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text(stringResource(R.string.notes_placeholder), color = primaryAccent) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("day_notes_field"),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryAccent,
                    unfocusedBorderColor = outlineColor,
                    focusedTextColor = onSurfaceColor,
                    unfocusedTextColor = onSurfaceVariantColor
                )
            )

            Button(
                onClick = {
                    onSaveNotes(day.date, notesText)
                    onDismiss()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryAccent,
                    contentColor = onPrimaryColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_notes_button")
            ) {
                Text(
                    text = stringResource(R.string.save).uppercase(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
