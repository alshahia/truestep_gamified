package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.domain.content.QuestRegistry
import com.example.ui.components.CelebrationDialog
import com.example.ui.screens.JourneyScreen
import com.example.ui.theme.StepTrackTheme
import com.example.ui.viewmodel.StepUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class JourneyScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testJourneyScreenRendersTabsAndSwitchesContent() {
        val quests = QuestRegistry.generateDailyQuests(
            currentSteps = 3500,
            goalSteps = 8000,
            activeMinutes = 25,
            hourlySteps = List(24) { 0 },
            completedIds = emptySet(),
            currentHour = 11
        )

        val uiState = StepUiState(
            currentSteps = 3500,
            goalSteps = 8000,
            cumulativeKm = 2.65,
            streak = 5,
            bestStreak = 12,
            bankedStreakFreezes = 2,
            isStreakProtectedToday = false,
            dailyQuests = quests,
            unlockedAchievementIds = setOf("goal_4k", "days_1")
        )

        composeTestRule.setContent {
            StepTrackTheme {
                JourneyScreen(uiState = uiState)
            }
        }

        // Verify root screen and tab bar exist
        composeTestRule.onNodeWithTag("journey_screen").assertExists()
        composeTestRule.onNodeWithTag("tab_quests").assertExists()
        composeTestRule.onNodeWithTag("tab_hero").assertExists()
        composeTestRule.onNodeWithTag("tab_expeditions").assertExists()
        composeTestRule.onNodeWithTag("tab_streak_vault").assertExists()
        composeTestRule.onNodeWithTag("tab_trophies").assertExists()

        // Default tab is Quests -> Quest cards should be rendered
        composeTestRule.onNodeWithTag("quest_card_quest_morning").assertExists()
        composeTestRule.onNodeWithTag("quest_card_quest_brisk").assertExists()

        // Switch to Hero tab
        composeTestRule.onNodeWithTag("tab_hero").performClick()
        composeTestRule.onNodeWithTag("hero_loadout_view").assertExists()

        // Switch to Expeditions tab
        composeTestRule.onNodeWithTag("tab_expeditions").performClick()
        composeTestRule.onNodeWithTag("expedition_card").assertExists()

        // Switch to Streak Vault tab
        composeTestRule.onNodeWithTag("tab_streak_vault").performClick()
        composeTestRule.onNodeWithTag("streak_vault_card").assertExists()

        // Switch to Trophies tab
        composeTestRule.onNodeWithTag("tab_trophies").performClick()
        composeTestRule.onNodeWithTag("trophy_cabinet_view").assertExists()
        composeTestRule.onNodeWithTag("trophy_item_goal_4k").assertExists()
    }

    @Test
    fun testCelebrationDialogRendersAndTriggersActions() {
        var shared = false
        var dismissed = false

        composeTestRule.setContent {
            StepTrackTheme {
                CelebrationDialog(
                    steps = 10000,
                    distanceKm = 7.62,
                    streak = 7,
                    onShare = { shared = true },
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("celebration_dialog").assertExists()
        composeTestRule.onNodeWithTag("celebration_share_button").assertExists().performClick()
        assertTrue(shared)

        composeTestRule.onNodeWithTag("celebration_dismiss_button").assertExists().performClick()
        assertTrue(dismissed)
    }
}
