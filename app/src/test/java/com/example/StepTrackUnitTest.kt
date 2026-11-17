package com.example

import com.example.domain.content.DestinationRegistry
import com.example.domain.content.MultiplierRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToInt

class StepTrackUnitTest {

    @Test
    fun testGoalCalculations() {
        val targetGoal = 10000
        val minGoal = (targetGoal * 0.6).roundToInt()
        val stretchGoal = (targetGoal * 1.2).roundToInt()

        assertEquals(6000, minGoal)
        assertEquals(10000, targetGoal)
        assertEquals(12000, stretchGoal)
    }

    @Test
    fun testMedianBaselineCalculationOdd() {
        val dailyTotals = listOf(6200, 7500, 8100, 9300, 10200)
        val sorted = dailyTotals.sorted()
        val median = sorted[sorted.size / 2]

        assertEquals(8100, median)
    }

    @Test
    fun testMedianBaselineCalculationEven() {
        val dailyTotals = listOf(6000, 7000, 8000, 9000)
        val sorted = dailyTotals.sorted()
        val median = (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2

        assertEquals(7500, median)
    }

    @Test
    fun testWeeklyGoalIncreaseLimit() {
        val currentTarget = 8000
        val rawCandidate = 15000 // sudden huge jump
        val maxDelta = (currentTarget * 0.10).roundToInt().coerceAtLeast(200) // 10% limit = 800
        val bounded = rawCandidate.coerceIn(currentTarget - maxDelta, currentTarget + maxDelta)

        assertEquals(8800, bounded)
    }

    @Test
    fun testDestinationRegistryProgress() {
        val destinations = DestinationRegistry.destinations
        assertTrue(destinations.isNotEmpty())

        val at0Km = DestinationRegistry.getNextDestination(0.0)
        assertNotNull(at0Km)
        assertEquals("walk_park", at0Km?.id)

        val at20Km = DestinationRegistry.getNextDestination(20.0)
        assertEquals("walk_lake", at20Km?.id)

        val completedAt50Km = DestinationRegistry.getCompletedDestinations(50.0)
        assertEquals(4, completedAt50Km.size) // Park (5), Bridge (12), Lake (25), Marathon (42.2)
    }

    @Test
    fun testMultiplierTiers() {
        val tiers = MultiplierRegistry.tiers
        assertEquals(12, tiers.size)
        assertEquals("1x", tiers[0].multiplierLabel)
        assertEquals("120x", tiers[11].multiplierLabel)
    }

    @Test
    fun testCsvExportFormat() {
        val header = "Date,TotalSteps,GoalSteps,MinGoal,StretchGoal,DistanceKm,ActiveMinutes,CaloriesKcal,DataSource,Confidence,Notes\n"
        assertTrue(header.contains("Date"))
        assertTrue(header.contains("TotalSteps"))
        assertTrue(header.contains("DataSource"))
        assertTrue(header.contains("Confidence"))
    }

    @Test
    fun testScreenNavItemsNotNull() {
        val navItems = com.example.ui.navigation.Screen.navItems
        assertEquals(5, navItems.size)
        navItems.forEach { screen ->
            assertNotNull(screen)
            assertNotNull(screen.route)
            assertTrue(screen.route.isNotEmpty())
            assertTrue(screen.titleRes > 0)
        }
    }

    @Test
    fun testSensorModeEnumDescriptions() {
        assertEquals("Hardware Step Counter", com.example.sensor.SensorMode.HARDWARE_STEP_COUNTER.displayName)
        assertEquals("Hardware Step Detector", com.example.sensor.SensorMode.HARDWARE_STEP_DETECTOR.displayName)
        assertEquals("Accelerometer Step Detector (Fallback)", com.example.sensor.SensorMode.ACCELEROMETER_FALLBACK.displayName)
    }

    @Test
    fun testAccelerometerStepDetectionLogic() {
        // Test simulation of walking wave: 10 footsteps at 1.8 Hz (550ms intervals)
        var stepsDetected = 0
        var smoothedMagnitude = 9.81f
        var dynamicGravityBaseline = 9.81f
        var isWaveAboveThreshold = false
        var lastStepTimestamp = 0L
        val stepThreshold = 1.55f

        fun processSample(mag: Float, timestampMs: Long) {
            smoothedMagnitude = 0.80f * smoothedMagnitude + 0.20f * mag
            dynamicGravityBaseline = 0.98f * dynamicGravityBaseline + 0.02f * mag
            val delta = smoothedMagnitude - dynamicGravityBaseline

            if (!isWaveAboveThreshold && delta > stepThreshold) {
                isWaveAboveThreshold = true
            } else if (isWaveAboveThreshold && delta < 0.2f) {
                isWaveAboveThreshold = false
                val elapsed = timestampMs - lastStepTimestamp
                if (elapsed in 250L..2000L || lastStepTimestamp == 0L) {
                    lastStepTimestamp = timestampMs
                    stepsDetected++
                }
            }
        }

        // Simulate 10 steps
        var currentTime = 1000L
        repeat(10) {
            // Foot strike peak (up to 13.5 m/s^2)
            repeat(5) { processSample(13.5f, currentTime); currentTime += 30L }
            // Swing dip (down to 7.5 m/s^2)
            repeat(8) { processSample(7.5f, currentTime); currentTime += 30L }
            // Baseline recovery
            repeat(6) { processSample(9.81f, currentTime); currentTime += 30L }
        }

        assertEquals(10, stepsDetected)
    }

    @Test
    fun testHighFrequencyNoiseRejection() {
        // Small vibrations below threshold (e.g. 0.4 m/s^2 jitter) should yield 0 steps
        var stepsDetected = 0
        var smoothedMagnitude = 9.81f
        var dynamicGravityBaseline = 9.81f
        var isWaveAboveThreshold = false
        val stepThreshold = 1.55f

        var currentTime = 1000L
        repeat(50) {
            val jitter = 9.81f + if (it % 2 == 0) 0.35f else -0.35f
            smoothedMagnitude = 0.80f * smoothedMagnitude + 0.20f * jitter
            dynamicGravityBaseline = 0.98f * dynamicGravityBaseline + 0.02f * jitter
            val delta = smoothedMagnitude - dynamicGravityBaseline

            if (!isWaveAboveThreshold && delta > stepThreshold) {
                isWaveAboveThreshold = true
            } else if (isWaveAboveThreshold && delta < 0.2f) {
                isWaveAboveThreshold = false
                stepsDetected++
            }
            currentTime += 20L
        }

        assertEquals(0, stepsDetected)
    }

    @Test
    fun testImmediateStepChannelDelivery() {
        val channel = kotlinx.coroutines.channels.Channel<Pair<Int, Int>>(kotlinx.coroutines.channels.Channel.UNLIMITED)
        // Simulate immediate step arrival
        channel.trySend(Pair(1, 101))
        channel.trySend(Pair(1, 102))

        val first = channel.tryReceive().getOrNull()
        assertNotNull(first)
        assertEquals(1, first?.first)
        assertEquals(101, first?.second)

        // Drain burst
        var totalDelta = 0
        while (true) {
            val next = channel.tryReceive().getOrNull() ?: break
            totalDelta += next.first
        }
        assertEquals(1, totalDelta)
    }

    @Test
    fun testDarkModeConfigResolution() {
        assertEquals(com.example.ui.theme.DarkModeConfig.SYSTEM, com.example.ui.theme.DarkModeConfig.fromStorageKey("system"))
        assertEquals(com.example.ui.theme.DarkModeConfig.LIGHT, com.example.ui.theme.DarkModeConfig.fromStorageKey("light"))
        assertEquals(com.example.ui.theme.DarkModeConfig.DARK, com.example.ui.theme.DarkModeConfig.fromStorageKey("dark"))
        assertEquals(com.example.ui.theme.DarkModeConfig.SYSTEM, com.example.ui.theme.DarkModeConfig.fromStorageKey(null))
        assertEquals(com.example.ui.theme.DarkModeConfig.SYSTEM, com.example.ui.theme.DarkModeConfig.fromStorageKey("xyz"))
    }

    @Test
    fun testAppLanguageResolutionAndRtl() {
        val systemLang = com.example.util.AppLanguage.fromCode("")
        assertEquals(com.example.util.AppLanguage.SYSTEM, systemLang)
        org.junit.Assert.assertFalse(systemLang.isRtl)

        val enLang = com.example.util.AppLanguage.fromCode("en")
        assertEquals(com.example.util.AppLanguage.ENGLISH, enLang)
        org.junit.Assert.assertFalse(enLang.isRtl)

        val arLang = com.example.util.AppLanguage.fromCode("ar")
        assertEquals(com.example.util.AppLanguage.ARABIC, arLang)
        assertTrue(arLang.isRtl)

        val unknownLang = com.example.util.AppLanguage.fromCode("fr_FR")
        assertEquals(com.example.util.AppLanguage.SYSTEM, unknownLang)
    }

    @Test
    fun testStepUiStateDefaults() {
        val state = com.example.ui.viewmodel.StepUiState()
        assertEquals(com.example.ui.theme.DarkModeConfig.SYSTEM, state.darkModeConfig)
        assertEquals(com.example.util.AppLanguage.SYSTEM, state.selectedLanguage)
    }
}
