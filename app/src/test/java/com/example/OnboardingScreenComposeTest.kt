package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.StepTrackTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OnboardingScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testOnboardingScreenRendersWelcomeStep() {
        var finished = false
        composeTestRule.setContent {
            StepTrackTheme {
                OnboardingScreen(
                    onFinish = { _, _ -> finished = true }
                )
            }
        }

        // Welcome step elements
        composeTestRule.onNodeWithTag("onboarding_skip_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("onboarding_primary_button").assertExists()
    }

    @Test
    fun testOnboardingScreenSkipButtonTriggersFinish() {
        var finishedGoal = -1
        var finishedMode = ""
        composeTestRule.setContent {
            StepTrackTheme {
                OnboardingScreen(
                    onFinish = { goal, mode ->
                        finishedGoal = goal
                        finishedMode = mode
                    }
                )
            }
        }

        composeTestRule.onNodeWithTag("onboarding_skip_button").performClick()
        assertEquals(10000, finishedGoal)
        assertEquals("Pocket", finishedMode)
    }

    @Test
    fun testOnboardingScreenStepsProgression() {
        var finishedGoal = -1
        var finishedMode = ""
        composeTestRule.setContent {
            StepTrackTheme {
                OnboardingScreen(
                    onFinish = { goal, mode ->
                        finishedGoal = goal
                        finishedMode = mode
                    }
                )
            }
        }

        // Step 0 -> Step 1
        composeTestRule.onNodeWithTag("onboarding_primary_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Step 1 -> Step 2
        composeTestRule.onNodeWithTag("onboarding_primary_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Step 2 -> Step 3
        composeTestRule.onNodeWithTag("onboarding_primary_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Step 3 -> Finish
        composeTestRule.onNodeWithTag("onboarding_primary_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        assertTrue(finishedGoal > 0)
        assertTrue(finishedMode.isNotEmpty())
    }

    @Test
    fun testOnboardingScreenStep1HasPermissionCards() {
        composeTestRule.setContent {
            StepTrackTheme {
                OnboardingScreen(onFinish = { _, _ -> })
            }
        }
        // Advance to Step 1
        composeTestRule.onNodeWithTag("onboarding_primary_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("onboarding_sensor_perm_card").assertExists()
    }
}
