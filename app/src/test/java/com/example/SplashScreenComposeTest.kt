package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.domain.content.MotivationalQuotesRegistry
import com.example.ui.navigation.AppNavigation
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.StepTrackTheme
import com.example.ui.viewmodel.StepUiState
import com.example.ui.viewmodel.StepViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SplashScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSplashScreenRendersEmblemTitleAndQuote() {
        val testQuote = MotivationalQuotesRegistry.quotes[0]
        composeTestRule.setContent {
            StepTrackTheme {
                SplashScreen(
                    onSplashFinished = {},
                    minHoldTimeMillis = 5000L,
                    dailyQuote = testQuote
                )
            }
        }

        composeTestRule.onNodeWithTag("splash_screen_root").assertIsDisplayed()
        composeTestRule.onNodeWithTag("splash_logo").assertIsDisplayed()
        composeTestRule.onNodeWithTag("splash_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("splash_quote_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("splash_quote_text").assertIsDisplayed()
    }

    @Test
    fun testSplashScreenCallbackTriggersAfterDelay() {
        var finished = false
        composeTestRule.setContent {
            StepTrackTheme {
                SplashScreen(
                    onSplashFinished = { finished = true },
                    minHoldTimeMillis = 50L
                )
            }
        }

        composeTestRule.mainClock.advanceTimeBy(100L)
        composeTestRule.waitForIdle()

        assertTrue("Splash finish callback should be triggered", finished)
    }

    @Test
    fun testMotivationalQuotesRegistryRotation() {
        val quotes = MotivationalQuotesRegistry.quotes
        assertEquals(6, quotes.size)

        val quoteDay1 = MotivationalQuotesRegistry.getDailyQuote(1)
        val quoteDay2 = MotivationalQuotesRegistry.getDailyQuote(2)
        val quoteDay7 = MotivationalQuotesRegistry.getDailyQuote(7)

        assertNotNull(quoteDay1)
        assertNotNull(quoteDay2)
        assertEquals(quotes[1], quoteDay1)
        assertEquals(quotes[2], quoteDay2)
        assertEquals(quotes[1], quoteDay7) // 7 % 6 == 1
    }
}
