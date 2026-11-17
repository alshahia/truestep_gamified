package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.ui.components.HeroStepRing
import com.example.ui.components.PauseButton
import com.example.ui.screens.HomeScreen
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
class StepTrackComposeUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHeroStepRingRendersAndToggles() {
        var clicked = false
        composeTestRule.setContent {
            StepTrackTheme {
                HeroStepRing(
                    steps = 8432,
                    goal = 10000,
                    percent = 84,
                    isPaused = false,
                    onTogglePause = { clicked = true }
                )
            }
        }
        composeTestRule.onNodeWithTag("hero_ring_toggle").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ring_step_count_text", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("hero_ring_toggle").performClick()
        assertTrue(clicked)
    }

    @Test
    fun testHeroStepRingEntireDialTogglesPause() {
        var clicked = false
        composeTestRule.setContent {
            StepTrackTheme {
                HeroStepRing(
                    steps = 8432,
                    goal = 10000,
                    percent = 84,
                    isPaused = false,
                    onTogglePause = { clicked = true }
                )
            }
        }
        composeTestRule.onNodeWithTag("hero_ring_dial").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ring_pause_hint", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("hero_ring_dial").performClick()
        assertTrue(clicked)
    }

    @Test
    fun testPauseButtonToggles() {
        var clicked = false
        composeTestRule.setContent {
            StepTrackTheme {
                PauseButton(
                    isPaused = false,
                    onTogglePause = { clicked = true }
                )
            }
        }
        composeTestRule.onNodeWithTag("pause_toggle_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("pause_toggle_button").performClick()
        assertTrue(clicked)
    }

    @Test
    fun testHomeScreenRendersMetrics() {
        val testState = StepUiState(
            currentSteps = 8420,
            goalSteps = 10000,
            minGoal = 6000,
            stretchGoal = 12000,
            cumulativeKm = 6.41,
            activeMinutes = 84,
            caloriesKcal = 336,
            streak = 5
        )

        composeTestRule.setContent {
            StepTrackTheme {
                HomeScreen(
                    uiState = testState,
                    onTogglePause = {},
                    onUpdateGoal = {},
                    onScrubHour = {},
                    onSelectRange = {},
                    onNavigateToDestination = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("hero_step_ring_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ring_step_count_text", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onAllNodesWithText("8,420", useUnmergedTree = true)[0].assertIsDisplayed()
        composeTestRule.onNodeWithTag("today_trust_status_card").assertExists()
        composeTestRule.onNodeWithTag("hourly_scrub_card").assertExists()
    }

    @Test
    fun testHomeScreenTrustStatusCardTriggersOpenTrustCenter() {
        var trustOpened = false
        val testState = StepUiState(
            currentSteps = 3000,
            goalSteps = 8000
        )

        composeTestRule.setContent {
            StepTrackTheme {
                HomeScreen(
                    uiState = testState,
                    onTogglePause = {},
                    onUpdateGoal = {},
                    onScrubHour = {},
                    onSelectRange = {},
                    onNavigateToDestination = {},
                    onOpenTrustCenter = { trustOpened = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("today_trust_status_card").performScrollTo().performClick()
        assertTrue(trustOpened)
    }

    @Test
    fun testHomeScreenGoalTiersTriggersNavigateToGoals() {
        var goalsOpened = false
        val testState = StepUiState(
            currentSteps = 4500,
            goalSteps = 8000,
            minGoal = 5000,
            stretchGoal = 10000
        )

        composeTestRule.setContent {
            StepTrackTheme {
                HomeScreen(
                    uiState = testState,
                    onTogglePause = {},
                    onUpdateGoal = {},
                    onScrubHour = {},
                    onSelectRange = {},
                    onNavigateToGoals = { goalsOpened = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("home_goal_tiers_card").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_goal_tiers_row").performClick()
        assertTrue(goalsOpened)
    }

    @Test
    fun testHomeScreenElevatedKpiGridDisplaysMetrics() {
        val testState = StepUiState(
            currentSteps = 6500,
            goalSteps = 8000,
            cumulativeKm = 4.85,
            caloriesKcal = 260,
            activeMinutes = 55
        )

        composeTestRule.setContent {
            StepTrackTheme {
                HomeScreen(
                    uiState = testState,
                    onTogglePause = {},
                    onUpdateGoal = {},
                    onScrubHour = {},
                    onSelectRange = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("kpi_steps_hour").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("kpi_distance").assertIsDisplayed()
        composeTestRule.onNodeWithTag("kpi_calories").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("kpi_share_of_day").assertIsDisplayed()
    }

    @Test
    fun testHomeScreenPausedBannerDisplaysAtTopWhenPaused() {
        val testState = StepUiState(
            currentSteps = 1200,
            goalSteps = 8000,
            isPaused = true
        )

        composeTestRule.setContent {
            StepTrackTheme {
                HomeScreen(
                    uiState = testState,
                    onTogglePause = {},
                    onUpdateGoal = {},
                    onScrubHour = {},
                    onSelectRange = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("paused_status_banner").assertIsDisplayed()
    }

    @Test
    fun testHomeScreenScrubExitBannerRendersAndExits() {
        var scrubCleared = false
        val testState = StepUiState(
            currentSteps = 6000,
            goalSteps = 8000,
            selectedScrubHour = 14,
            hourlySteps = List(24) { 250 }
        )

        composeTestRule.setContent {
            StepTrackTheme {
                HomeScreen(
                    uiState = testState,
                    onTogglePause = {},
                    onUpdateGoal = {},
                    onScrubHour = { if (it == null) scrubCleared = true },
                    onSelectRange = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("scrub_exit_banner").assertIsDisplayed()
        composeTestRule.onNodeWithTag("scrub_exit_button").performClick()
        assertTrue(scrubCleared)
    }

    @Test
    fun testTrustCenterSheetRendersComponents() {
        var closed = false
        val testState = StepUiState(
            currentSteps = 5000,
            goalSteps = 10000,
            confidence = "HIGH",
            dataSource = "Phone Sensor"
        )

        composeTestRule.setContent {
            StepTrackTheme {
                com.example.ui.screens.TrustCenterSheet(
                    uiState = testState,
                    onDismissRequest = { closed = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("trust_center_sheet_modal").assertExists()
        composeTestRule.onNodeWithTag("trust_data_sources_card").assertExists()
        composeTestRule.onNodeWithTag("trust_overlap_card").assertExists()
        composeTestRule.onNodeWithTag("battery_settings_button").assertExists()
        composeTestRule.onNodeWithTag("close_trust_sheet_button").performClick()
        assertTrue(closed)
    }

    @Test
    fun testCelebrationDialogRendersMilestoneCardAndTriggersActions() {
        var shared = false
        var rated = false
        var dismissed = false

        composeTestRule.setContent {
            StepTrackTheme {
                com.example.ui.components.CelebrationDialog(
                    steps = 10450,
                    distanceKm = 7.96,
                    streak = 7,
                    onShare = { shared = true },
                    onDismiss = { dismissed = true },
                    onRateApp = { rated = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("celebration_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("shareable_milestone_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("celebration_share_button").performClick()
        assertTrue(shared)

        composeTestRule.onNodeWithTag("celebration_rate_button").performClick()
        assertTrue(rated)

        composeTestRule.onNodeWithTag("celebration_dismiss_button").performClick()
        assertTrue(dismissed)
    }

    @Test
    fun testRatingReviewDialogRendersAndHandlesStars() {
        var ratedStore = false
        var dismissed = false
        var neverAsk = false

        composeTestRule.setContent {
            StepTrackTheme {
                com.example.ui.components.RatingReviewDialog(
                    onRateOnStore = { ratedStore = true },
                    onSendFeedback = {},
                    onDismiss = { dismissed = true },
                    onNeverAskAgain = { neverAsk = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("rating_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("rating_star_5").assertIsDisplayed()
        composeTestRule.onNodeWithTag("rate_google_play_button").performClick()
        assertTrue(ratedStore)

        composeTestRule.onNodeWithTag("rating_maybe_later_button").performClick()
        assertTrue(dismissed)

        composeTestRule.onNodeWithTag("rating_never_ask_button").performClick()
        assertTrue(neverAsk)
    }

    @Test
    fun testOptionCExpeditionCardRendersAllSections() {
        val sRankDest = com.example.domain.content.DestinationRegistry.destinations.first { it.id == "walk_fuji" }
        composeTestRule.setContent {
            StepTrackTheme {
                com.example.ui.screens.journey.components.ExpeditionTrailCard(
                    allTimeKm = 45.0,
                    nextDestination = sRankDest
                )
            }
        }

        composeTestRule.onNodeWithTag("expedition_card").assertExists()
        composeTestRule.onNodeWithTag("cyber_gate_rank_badge").assertExists()
        composeTestRule.onNodeWithTag("cyber_gate_core_card").assertExists()
        composeTestRule.onNodeWithTag("cyber_gate_progress_bar").assertExists()
        composeTestRule.onNodeWithTag("cyber_gate_combo_banner").assertExists()
        composeTestRule.onNodeWithTag("cyber_gate_loot_grid").assertExists()
        composeTestRule.onNodeWithTag("cyber_gate_continue_btn").assertExists()
    }

    @Test
    fun testSystemHologramDialogRendersAndConfirms() {
        var confirmed = false
        var dismissed = false

        composeTestRule.setContent {
            StepTrackTheme {
                com.example.ui.components.SystemHologramDialog(
                    titleRes = R.string.system_level_up_title,
                    bodyText = "Level 15 Reached! +3 Stat Points awarded.",
                    eventType = "LEVEL_UP",
                    onConfirm = { confirmed = true },
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("system_hologram_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("system_hologram_confirm_btn").performClick()
        assertTrue(confirmed)
    }
}
