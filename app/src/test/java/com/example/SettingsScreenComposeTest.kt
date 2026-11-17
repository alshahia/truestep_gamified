package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.data.local.UserGoalSettings
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.settings.model.SettingsActions
import com.example.ui.screens.settings.model.SettingsUiState
import com.example.ui.theme.DarkModeConfig
import com.example.ui.theme.StepTrackTheme
import com.example.ui.theme.ThemeChoice
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSettingsScreenRendersAllSectionsAndTestTags() {
        val uiState = SettingsUiState(
            selectedTheme = ThemeChoice.CLASSIC_TEAL,
            darkModeConfig = DarkModeConfig.SYSTEM,
            userGoalSettings = UserGoalSettings(
                adaptiveGoalsEnabled = true,
                lockedGoalEnabled = false,
                recoveryModeEnabled = false,
                carryingMode = "Pocket"
            ),
            isLiveNotificationEnabled = true,
            isHighContrastEnabled = false
        )

        composeTestRule.setContent {
            StepTrackTheme {
                SettingsScreen(
                    uiState = uiState,
                    actions = SettingsActions()
                )
            }
        }

        // Verify primary testTags exist in the hierarchy
        composeTestRule.onNodeWithTag("theme_selection_card").assertExists()
        composeTestRule.onNodeWithTag("carrying_mode_card").assertExists()
        composeTestRule.onNodeWithTag("adaptive_goals_switch").assertExists()
        composeTestRule.onNodeWithTag("lock_goals_switch").assertExists()
        composeTestRule.onNodeWithTag("recovery_mode_switch").assertExists()
        composeTestRule.onNodeWithTag("live_notification_switch").assertExists()
        composeTestRule.onNodeWithTag("high_contrast_switch").assertExists()
        composeTestRule.onNodeWithTag("recalculate_goal_button").assertExists()
        composeTestRule.onNodeWithTag("export_csv_button").assertExists()
        composeTestRule.onNodeWithTag("export_json_button").assertExists()
        composeTestRule.onNodeWithTag("load_demo_data_button").assertExists()
        composeTestRule.onNodeWithTag("delete_local_data_button").assertExists()
        composeTestRule.onNodeWithTag("open_trust_center_button").assertExists()
        composeTestRule.onNodeWithTag("sync_health_connect_button").assertExists()
        composeTestRule.onNodeWithTag("battery_optimization_button").assertExists()
    }

    @Test
    fun testOpenTrustCenterActionRowDispatches() {
        var trustCenterOpened = false
        val uiState = SettingsUiState()

        composeTestRule.setContent {
            StepTrackTheme {
                SettingsScreen(
                    uiState = uiState,
                    actions = SettingsActions(
                        onOpenTrustCenter = { trustCenterOpened = true }
                    )
                )
            }
        }

        composeTestRule.onNodeWithTag("open_trust_center_button").performScrollTo().performClick()
        assertTrue(trustCenterOpened)
    }

    @Test
    fun testThemeSelectionSheetOpensAndSelectsTheme() {
        var selectedTheme: ThemeChoice? = null
        val uiState = SettingsUiState(selectedTheme = ThemeChoice.CLASSIC_TEAL)

        composeTestRule.setContent {
            StepTrackTheme {
                SettingsScreen(
                    uiState = uiState,
                    actions = SettingsActions(
                        onSelectTheme = { selectedTheme = it }
                    )
                )
            }
        }

        // Tap theme card to open bottom sheet
        composeTestRule.onNodeWithTag("theme_selection_card").performClick()
        composeTestRule.waitForIdle()

        // Sheet modal is displayed
        composeTestRule.onNodeWithTag("theme_sheet_modal").assertIsDisplayed()

        // Select a theme
        composeTestRule.onNodeWithTag("theme_item_neon_futuristic").performClick()
        composeTestRule.waitForIdle()

        assertEquals(ThemeChoice.NEON_FUTURISTIC, selectedTheme)
    }

    @Test
    fun testCarryingModeSelection() {
        var chosenMode: String? = null
        val uiState = SettingsUiState(
            userGoalSettings = UserGoalSettings(carryingMode = "Pocket")
        )

        composeTestRule.setContent {
            StepTrackTheme {
                SettingsScreen(
                    uiState = uiState,
                    actions = SettingsActions(
                        onSelectCarryingMode = { chosenMode = it }
                    )
                )
            }
        }

        composeTestRule.onNodeWithTag("carrying_mode_hand").performScrollTo().performClick()
        assertEquals("Hand", chosenMode)
    }

    @Test
    fun testSwitchToggles() {
        var adaptiveToggled = false
        var lockToggled = false
        var recoveryToggled = false
        var liveNotifToggled = false
        var highContrastToggled = false

        val uiState = SettingsUiState(
            userGoalSettings = UserGoalSettings(
                adaptiveGoalsEnabled = false,
                lockedGoalEnabled = false,
                recoveryModeEnabled = false
            ),
            isLiveNotificationEnabled = false,
            isHighContrastEnabled = false
        )

        composeTestRule.setContent {
            StepTrackTheme {
                SettingsScreen(
                    uiState = uiState,
                    actions = SettingsActions(
                        onToggleAdaptiveGoals = { adaptiveToggled = it },
                        onToggleLockGoals = { lockToggled = it },
                        onToggleRecoveryMode = { recoveryToggled = it },
                        onToggleLiveNotification = { liveNotifToggled = it },
                        onToggleHighContrast = { highContrastToggled = it }
                    )
                )
            }
        }

        composeTestRule.onNodeWithTag("adaptive_goals_switch").performScrollTo().performClick()
        assertTrue(adaptiveToggled)

        composeTestRule.onNodeWithTag("lock_goals_switch").performScrollTo().performClick()
        assertTrue(lockToggled)

        composeTestRule.onNodeWithTag("recovery_mode_switch").performScrollTo().performClick()
        assertTrue(recoveryToggled)

        composeTestRule.onNodeWithTag("live_notification_switch").performScrollTo().performClick()
        assertTrue(liveNotifToggled)

        composeTestRule.onNodeWithTag("high_contrast_switch").performScrollTo().performClick()
        assertTrue(highContrastToggled)
    }

    @Test
    fun testDeleteDataDialogAndConfirm() {
        var clearDataCalled = false
        val uiState = SettingsUiState()

        composeTestRule.setContent {
            StepTrackTheme {
                SettingsScreen(
                    uiState = uiState,
                    actions = SettingsActions(
                        onClearAllData = { clearDataCalled = true }
                    )
                )
            }
        }

        // Click delete button after scrolling to it
        composeTestRule.onNodeWithTag("delete_local_data_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Confirmation dialog appears
        composeTestRule.onNodeWithTag("delete_confirm_dialog").assertIsDisplayed()

        // Click confirm
        composeTestRule.onNodeWithTag("confirm_delete_button").performClick()
        composeTestRule.waitForIdle()

        assertTrue(clearDataCalled)
    }

    @Test
    fun testPrivacyPolicyDialogAndDismiss() {
        val uiState = SettingsUiState()

        composeTestRule.setContent {
            StepTrackTheme {
                SettingsScreen(
                    uiState = uiState,
                    actions = SettingsActions()
                )
            }
        }

        // Click privacy policy button
        composeTestRule.onNodeWithTag("privacy_policy_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Privacy Policy dialog appears
        composeTestRule.onNodeWithTag("privacy_policy_dialog").assertIsDisplayed()

        // Click close
        composeTestRule.onNodeWithTag("close_privacy_policy_button").performClick()
        composeTestRule.waitForIdle()

        // Dialog is dismissed
        composeTestRule.onNodeWithTag("privacy_policy_dialog").assertDoesNotExist()
    }
}
