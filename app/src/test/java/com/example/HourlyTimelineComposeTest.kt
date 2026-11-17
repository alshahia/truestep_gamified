package com.example

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.ui.components.HomeKpiGrid
import com.example.ui.components.HourlyScrubRibbon
import com.example.ui.theme.StepTrackTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HourlyTimelineComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHourlyScrubRibbonAndKpiGridRender() {
        val hourly = List(24) { hour -> if (hour in 8..18) 400 else 0 }

        composeTestRule.setContent {
            StepTrackTheme {
                HourlyScrubRibbon(
                    hourlySteps = hourly,
                    selectedHour = null,
                    onScrubHour = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("hourly_scrub_card").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("scrub_track_gesture_box").assertExists()
    }

    @Test
    fun testHomeKpiGridRendersAllTestTagsInFullDayMode() {
        composeTestRule.setContent {
            StepTrackTheme {
                HomeKpiGrid(
                    stepsInHour = 7450,
                    distanceKm = 5.68,
                    caloriesKcal = 298,
                    shareOfDayPercent = 75,
                    isScrubbing = false,
                    isPaused = false
                )
            }
        }

        composeTestRule.onNodeWithTag("kpi_steps_hour").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("kpi_distance").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("kpi_calories").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithTag("kpi_share_of_day").assertExists().assertIsDisplayed()

        composeTestRule.onNodeWithText("7,450").assertExists()
        composeTestRule.onNodeWithText("5.68").assertExists()
        composeTestRule.onNodeWithText("298").assertExists()
        composeTestRule.onNodeWithText("75%").assertExists()
    }

    @Test
    fun testHourlyScrubSelectionAndClear() {
        var selectedHourState: Int? by mutableStateOf(null)
        val hourly = List(24) { 150 }

        composeTestRule.setContent {
            StepTrackTheme {
                HourlyScrubRibbon(
                    hourlySteps = hourly,
                    selectedHour = selectedHourState,
                    onScrubHour = { selectedHourState = it }
                )
            }
        }

        // Tap on hour 0 (start of day, visible at scroll 0)
        composeTestRule.onNodeWithTag("hourly_bar_0").performClick()
        assertEquals(0, selectedHourState)

        // Repeat tap on hour 0 unselects back to Full Day mode
        composeTestRule.onNodeWithTag("hourly_bar_0").performClick()
        assertNull(selectedHourState)
    }

    @Test
    fun testHomeKpiGridUpdatesInScrubbingMode() {
        composeTestRule.setContent {
            StepTrackTheme {
                HomeKpiGrid(
                    stepsInHour = 3200,
                    distanceKm = 2.44,
                    caloriesKcal = 128,
                    shareOfDayPercent = 32,
                    isScrubbing = true,
                    isPaused = false,
                    stepsDeltaInHour = 450
                )
            }
        }

        composeTestRule.onNodeWithTag("kpi_steps_hour").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithText("3,200").assertExists()
    }
}
