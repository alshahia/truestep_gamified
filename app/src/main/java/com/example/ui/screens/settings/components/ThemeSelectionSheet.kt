package com.example.ui.screens.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.ThemeChoice
import com.example.ui.theme.ThemeRegistry
import com.example.ui.theme.tokens.appShapes
import com.example.ui.theme.tokens.dimens
import com.example.ui.theme.tokens.spacing
import com.example.util.LocalizationUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionSheet(
    selectedTheme: ThemeChoice,
    onSelectTheme: (ThemeChoice) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = MaterialTheme.appShapes.bottomSheet,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("theme_sheet_modal")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.xl, vertical = MaterialTheme.spacing.sm)
        ) {
            Text(
                text = stringResource(R.string.theme_selection).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.theme_card_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = MaterialTheme.spacing.xxxl),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                items(ThemeChoice.values(), key = { it.name }) { choice ->
                    val spec = ThemeRegistry.getThemeSpec(choice)
                    val isSelected = choice == selectedTheme
                    val primaryColor = spec.lightColors.primary
                    val secondaryColor = spec.lightColors.secondary

                    Card(
                        shape = MaterialTheme.appShapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = if (isSelected) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.appShapes.medium)
                            .clickable {
                                onSelectTheme(choice)
                                onDismissRequest()
                            }
                            .testTag("theme_item_${choice.name.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.md)
                        ) {
                            // Two-circle color preview swatch
                            Box(
                                modifier = Modifier
                                    .size(MaterialTheme.dimens.badgeSize)
                                    .clip(CircleShape)
                                    .background(primaryColor)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(MaterialTheme.dimens.iconSmall)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(secondaryColor)
                                )
                            }

                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.md))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = LocalizationUtil.getThemeDisplayName(choice),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
