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
import androidx.compose.material.icons.filled.Palette
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
import com.example.ui.screens.settings.components.PickerOption
import com.example.ui.screens.settings.components.SettingsActionRow
import com.example.ui.screens.settings.components.SettingsGroupCard
import com.example.ui.screens.settings.components.SettingsPickerRow
import com.example.ui.theme.DarkModeConfig
import com.example.ui.theme.ThemeChoice
import com.example.ui.theme.ThemeRegistry
import com.example.util.AppLanguage
import com.example.util.LocalizationUtil

@Composable
fun AppearanceSettingsSection(
    selectedTheme: ThemeChoice,
    darkModeConfig: DarkModeConfig,
    selectedLanguage: AppLanguage,
    onOpenThemeSheet: () -> Unit,
    onSelectDarkMode: (DarkModeConfig) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeSpec = ThemeRegistry.getThemeSpec(selectedTheme)
    val localizedThemeName = LocalizationUtil.getThemeDisplayName(selectedTheme)

    SettingsGroupCard(
        title = stringResource(R.string.section_appearance),
        icon = Icons.Default.Palette,
        modifier = modifier
    ) {
        // Theme selection card / row with test tag for backward-compatibility
        SettingsActionRow(
            title = stringResource(R.string.theme_selection),
            subtitle = localizedThemeName,
            onClick = onOpenThemeSheet,
            testTag = "theme_selection_card",
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(activeSpec.lightColors.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = localizedThemeName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        // Dark mode mode options
        val darkModeOptions = listOf(
            PickerOption(DarkModeConfig.SYSTEM, stringResource(R.string.dark_mode_system), "dark_mode_system"),
            PickerOption(DarkModeConfig.LIGHT, stringResource(R.string.dark_mode_off), "dark_mode_light"),
            PickerOption(DarkModeConfig.DARK, stringResource(R.string.dark_mode_on), "dark_mode_dark")
        )
        SettingsPickerRow(
            title = stringResource(R.string.dark_mode_option),
            subtitle = stringResource(R.string.dark_mode_desc),
            options = darkModeOptions,
            selectedValue = darkModeConfig,
            onOptionSelected = onSelectDarkMode
        )

        // Language options
        val languageOptions = listOf(
            PickerOption(AppLanguage.SYSTEM, stringResource(R.string.language_system), "lang_system"),
            PickerOption(AppLanguage.ENGLISH, stringResource(R.string.language_english), "lang_english"),
            PickerOption(AppLanguage.ARABIC, stringResource(R.string.language_arabic), "lang_arabic")
        )
        SettingsPickerRow(
            title = stringResource(R.string.language_label),
            subtitle = stringResource(R.string.language_desc),
            options = languageOptions,
            selectedValue = selectedLanguage,
            onOptionSelected = onSelectLanguage
        )
    }
}
