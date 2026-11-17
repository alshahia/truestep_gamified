package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.data.local.WalkingSession
import com.example.domain.content.GearRegistry
import com.example.domain.model.HunterProfile
import com.example.ui.components.WorkoutSummarySheet
import com.example.ui.screens.journey.components.HeroLoadoutView
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
class HeroLoadoutComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testHeroLoadoutViewRendersHeaderAndCards() {
        val hunterProfile = HunterProfile(
            name = "Ren Amamiya",
            classTitle = "Wind Courier • Speedster",
            level = 14,
            combatPower = 2140
        )
        val allGear = GearRegistry.allGearItems

        composeTestRule.setContent {
            StepTrackTheme {
                HeroLoadoutView(
                    hunterProfile = hunterProfile,
                    allGearItems = allGear,
                    onEquipGear = { _, _ -> },
                    onTuneGear = {}
                )
            }
        }

        // Verify root container and hunter card
        composeTestRule.onNodeWithTag("hero_loadout_view").assertExists()
        composeTestRule.onNodeWithTag("hunter_profile_card").assertExists()
        composeTestRule.onNodeWithTag("cybernetic_forge_banner").assertExists()

        // Verify equipped items
        composeTestRule.onNodeWithTag("equipped_gear_gear_neon_phantom").assertExists()
        composeTestRule.onNodeWithTag("equipped_gear_gear_zephyr_engine").assertExists()
    }

    @Test
    fun testHeroLoadoutViewTuneActionTriggersCallback() {
        val hunterProfile = HunterProfile()
        val allGear = GearRegistry.allGearItems
        var tunedGearId: String? = null

        composeTestRule.setContent {
            StepTrackTheme {
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier.verticalScroll(
                        androidx.compose.foundation.rememberScrollState()
                    )
                ) {
                    HeroLoadoutView(
                        hunterProfile = hunterProfile,
                        allGearItems = allGear,
                        onEquipGear = { _, _ -> },
                        onTuneGear = { tunedGearId = it }
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("tune_button_gear_neon_phantom")
            .performScrollTo()
            .performClick()
        assertEquals("gear_neon_phantom", tunedGearId)
    }

    @Test
    fun testHunterAttributeAllocationTriggersCallback() {
        val hunterProfile = HunterProfile(availableStatPoints = 3)
        val allGear = GearRegistry.allGearItems
        var allocatedStat: String? = null

        composeTestRule.setContent {
            StepTrackTheme {
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier.verticalScroll(
                        androidx.compose.foundation.rememberScrollState()
                    )
                ) {
                    HeroLoadoutView(
                        hunterProfile = hunterProfile,
                        allGearItems = allGear,
                        onEquipGear = { _, _ -> },
                        onTuneGear = {},
                        onAllocateStatPoint = { allocatedStat = it }
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("hunter_attribute_matrix_card").assertExists()
        composeTestRule.onNodeWithTag("stat_points_badge").assertExists()
        composeTestRule.onNodeWithTag("allocate_stat_str").performScrollTo().performClick()
        assertEquals("STR", allocatedStat)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun testWorkoutSummarySheetRendersAndClaimsRewards() {
        val testSession = WalkingSession(
            id = 101L,
            sessionType = "Outdoor Walk",
            startTime = System.currentTimeMillis() - 1800_000L,
            endTime = System.currentTimeMillis(),
            durationSeconds = 1800,
            steps = 4250,
            distanceKm = 3.24,
            notes = "Morning Overdrive Sprint"
        )

        var claimedRewards = false
        var sparks = 0
        var cores = 0
        var shards = 0

        composeTestRule.setContent {
            StepTrackTheme {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                WorkoutSummarySheet(
                    sheetState = sheetState,
                    session = testSession,
                    hunterName = "Ren Amamiya",
                    hunterLevel = 14,
                    onClaimRewards = { s, c, sh ->
                        claimedRewards = true
                        sparks = s
                        cores = c
                        shards = sh
                    },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("workout_summary_sheet").assertExists()
        composeTestRule.onNodeWithTag("share_raid_card_button").assertExists()
        composeTestRule.onNodeWithTag("claim_rewards_btn").performScrollTo().performClick()

        assertTrue(claimedRewards)
        assertTrue(sparks > 0)
        assertTrue(cores > 0)
        assertTrue(shards > 0)
    }
}
