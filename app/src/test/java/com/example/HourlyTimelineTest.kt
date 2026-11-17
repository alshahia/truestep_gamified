package com.example

import com.example.ui.viewmodel.StepUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HourlyTimelineTest {

    @Test
    fun testFullDayDefaultMetrics() {
        val hourly = List(24) { 100 }
        val state = StepUiState(
            currentSteps = 2400,
            goalSteps = 10000,
            cumulativeKm = 1.83,
            caloriesKcal = 96,
            hourlySteps = hourly,
            selectedScrubHour = null
        )

        assertTrue(state.isFullDay)
        assertFalse(state.isScrubbing)
        assertEquals(2400, state.displaySteps)
        assertEquals(1.83, state.displayKm, 0.01)
        assertEquals(96, state.displayCalories)
        assertEquals(24, state.displayPercent)
    }

    @Test
    fun testStartOfDayHourZeroZeroSteps() {
        val hourly = MutableList(24) { 0 }
        hourly[8] = 500
        hourly[12] = 800

        val state = StepUiState(
            currentSteps = 1300,
            goalSteps = 10000,
            cumulativeKm = 0.99,
            caloriesKcal = 52,
            hourlySteps = hourly,
            selectedScrubHour = 0
        )

        assertTrue(state.isScrubbing)
        assertFalse(state.isFullDay)
        // User pulls to max left (start of day, hour 0) -> displays 0 when no steps taken in hour 0
        assertEquals(0, state.displaySteps)
        assertEquals(0.0, state.displayKm, 0.001)
        assertEquals(0, state.displayCalories)
        assertEquals(0, state.hourSteps)
    }

    @Test
    fun testStartOfDayHourZeroWithSteps() {
        val hourly = MutableList(24) { 0 }
        hourly[0] = 145
        hourly[9] = 1000

        val state = StepUiState(
            currentSteps = 1145,
            goalSteps = 10000,
            hourlySteps = hourly,
            selectedScrubHour = 0
        )

        assertTrue(state.isScrubbing)
        // If user took steps in hour 0, displays hour 0 steps
        assertEquals(145, state.displaySteps)
        assertEquals(145, state.hourSteps)
        assertEquals(0.11, state.displayKm, 0.01)
    }

    @Test
    fun testMidDayCumulativeProgression() {
        val hourly = List(24) { hour -> if (hour in 6..12) 500 else 0 }
        // Hours 6, 7, 8, 9, 10, 11, 12 each have 500 = 3500 total
        val state = StepUiState(
            currentSteps = 3500,
            goalSteps = 10000,
            hourlySteps = hourly,
            selectedScrubHour = 9
        )

        // Hours 0..9 has 4 active hours (6, 7, 8, 9) = 2000 steps
        assertEquals(2000, state.displaySteps)
        assertEquals(500, state.hourSteps)
        assertEquals(20, state.displayPercent)
    }

    @Test
    fun testEndOfDayHour23FullDay() {
        val hourly = List(24) { 200 }
        val state = StepUiState(
            currentSteps = 4800,
            goalSteps = 10000,
            cumulativeKm = 3.66,
            caloriesKcal = 192,
            hourlySteps = hourly,
            selectedScrubHour = 23
        )

        // Hour 23 (max right / end of day) displays full day progress
        assertEquals(4800, state.displaySteps)
        assertEquals(3.66, state.displayKm, 0.01)
        assertEquals(192, state.displayCalories)
        assertEquals(48, state.displayPercent)
    }

    @Test
    fun testCumulativeMonotonicityAcrossAll24Hours() {
        val hourly = List(24) { hour -> (hour * 50) } // varying steps per hour
        val totalSteps = hourly.sum()

        var previousCumulative = 0
        for (h in 0..23) {
            val state = StepUiState(
                currentSteps = totalSteps,
                goalSteps = 10000,
                hourlySteps = hourly,
                selectedScrubHour = h
            )
            assertTrue(
                "Cumulative steps at hour $h (${state.displaySteps}) should be >= previous ($previousCumulative)",
                state.displaySteps >= previousCumulative
            )
            previousCumulative = state.displaySteps
        }

        assertEquals(totalSteps, previousCumulative)
    }
}
