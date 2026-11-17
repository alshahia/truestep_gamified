package com.example.ui.screens.settings.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.sensor.SensorMode
import com.example.ui.screens.settings.components.PickerOption
import com.example.ui.screens.settings.components.SettingsActionRow
import com.example.ui.screens.settings.components.SettingsGroupCard
import com.example.ui.screens.settings.components.SettingsPickerRow
import com.example.util.LocalizationUtil

@Composable
fun TrackingSettingsSection(
    activeSensorMode: SensorMode,
    isSensorAvailable: Boolean,
    currentCarryingMode: String,
    onSelectCarryingMode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsGroupCard(
        title = stringResource(R.string.section_general),
        icon = Icons.Default.PhoneAndroid,
        modifier = modifier
    ) {
        // Sensor status row
        SettingsActionRow(
            title = stringResource(R.string.sensor_status_title),
            subtitle = if (isSensorAvailable) LocalizationUtil.getSensorModeDisplayName(activeSensorMode) else stringResource(R.string.sensor_unavailable_desc),
            onClick = {},
            showChevron = false,
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSensorAvailable) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSensorAvailable) stringResource(R.string.status_active) else stringResource(R.string.status_offline),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSensorAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        // Carrying Mode options with testTag "carrying_mode_card"
        val carryingOptions = listOf(
            PickerOption("Pocket", stringResource(R.string.mode_pocket), "carrying_mode_pocket"),
            PickerOption("Hand", stringResource(R.string.mode_hand), "carrying_mode_hand"),
            PickerOption("Bag", stringResource(R.string.mode_bag), "carrying_mode_bag"),
            PickerOption("Backpack", stringResource(R.string.mode_backpack), "carrying_mode_backpack")
        )

        SettingsPickerRow(
            title = stringResource(R.string.carrying_mode_label),
            subtitle = stringResource(R.string.carrying_mode_subtitle),
            options = carryingOptions,
            selectedValue = currentCarryingMode,
            onOptionSelected = onSelectCarryingMode,
            testTag = "carrying_mode_card"
        )
    }
}
