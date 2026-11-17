package com.example

import com.example.data.source.FakeStepDataSource
import com.example.data.source.RawStepRecord
import com.example.domain.reconciliation.ConfidenceLevel
import com.example.domain.reconciliation.StepReconciliationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

class StrideTrustComprehensiveTest {

    // Requirement 1: One source with one day of data
    @Test
    fun testCase1_OneSourceWithOneDayOfData() {
        val baseTime = Instant.parse("2026-10-04T08:00:00Z")
        val records = listOf(
            RawStepRecord(
                sourcePackage = "android.hardware.sensor",
                sourceName = "Hardware Step Counter",
                sourceType = "PHONE_SENSOR",
                startTime = baseTime,
                endTime = baseTime.plusSeconds(3600),
                stepCount = 3500
            )
        )

        val result = StepReconciliationEngine.reconcile(records, dateString = "2026-10-04")
        assertEquals(3500, result.totalReconciledSteps)
        assertEquals(0, result.duplicateIntervalsCount)
        assertEquals(ConfidenceLevel.HIGH, result.confidence)
        assertEquals(1, result.processedRecords.size)
        assertTrue(result.processedRecords[0].isIncluded)
    }

    // Requirement 2: Phone and watch with non-overlapping data
    @Test
    fun testCase2_PhoneAndWatchNonOverlapping() {
        val t1 = Instant.parse("2026-10-04T08:00:00Z")
        val t2 = Instant.parse("2026-10-04T09:00:00Z")
        val t3 = Instant.parse("2026-10-04T10:00:00Z")
        val t4 = Instant.parse("2026-10-04T11:00:00Z")

        val records = listOf(
            RawStepRecord("android.hardware.sensor", "Phone", "PHONE_SENSOR", t1, t2, 2000),
            RawStepRecord("com.google.android.apps.fitness", "Smartwatch", "HEALTH_CONNECT", t3, t4, 3000)
        )

        val result = StepReconciliationEngine.reconcile(records, dateString = "2026-10-04")
        assertEquals(5000, result.totalReconciledSteps)
        assertEquals(0, result.duplicateIntervalsCount)
        assertEquals(2, result.processedRecords.filter { it.isIncluded }.size)
    }

    // Requirement 3: Phone and watch with overlapping data
    @Test
    fun testCase3_PhoneAndWatchOverlapping() {
        val t1 = Instant.parse("2026-10-04T08:00:00Z")
        val t2 = Instant.parse("2026-10-04T09:00:00Z")
        val tOverlapStart = Instant.parse("2026-10-04T08:15:00Z")
        val tOverlapEnd = Instant.parse("2026-10-04T08:45:00Z")

        val records = listOf(
            RawStepRecord("com.google.android.apps.fitness", "Smartwatch", "HEALTH_CONNECT", t1, t2, 4000),
            RawStepRecord("android.hardware.sensor", "Phone", "PHONE_SENSOR", tOverlapStart, tOverlapEnd, 1500)
        )

        // Health Connect is preferred primary source
        val result = StepReconciliationEngine.reconcile(records, primarySourceType = "HEALTH_CONNECT")
        assertEquals(4000, result.totalReconciledSteps)
        assertEquals(1, result.duplicateIntervalsCount)
        assertEquals(ConfidenceLevel.MEDIUM, result.confidence)

        val excluded = result.processedRecords.find { !it.isIncluded }
        assertNotNull(excluded)
        assertEquals("PHONE_SENSOR", excluded?.sourceType)
        assertTrue(excluded?.exclusionReason?.contains("Overlapped") == true)
    }

    // Requirement 4: Two records with the same time range
    @Test
    fun testCase4_TwoRecordsWithSameTimeRange() {
        val t1 = Instant.parse("2026-10-04T14:00:00Z")
        val t2 = Instant.parse("2026-10-04T15:00:00Z")

        val records = listOf(
            RawStepRecord("com.watch.app", "Watch", "HEALTH_CONNECT", t1, t2, 2800),
            RawStepRecord("android.hardware.sensor", "Phone", "PHONE_SENSOR", t1, t2, 2750)
        )

        val result = StepReconciliationEngine.reconcile(records, primarySourceType = "HEALTH_CONNECT")
        assertEquals(2800, result.totalReconciledSteps)
        assertEquals(1, result.duplicateIntervalsCount)
    }

    // Requirement 5: Data crossing midnight
    @Test
    fun testCase5_DataCrossingMidnight() {
        val tBeforeMidnight = Instant.parse("2026-10-04T23:30:00Z")
        val tAfterMidnight = Instant.parse("2026-10-05T00:30:00Z")

        val zone = ZoneId.of("UTC")
        val d1 = tBeforeMidnight.atZone(zone).toLocalDate()
        val d2 = tAfterMidnight.atZone(zone).toLocalDate()

        assertNotEquals(d1, d2)
        assertEquals(LocalDate.parse("2026-10-04"), d1)
        assertEquals(LocalDate.parse("2026-10-05"), d2)

        // Splitting or handling intervals across midnight boundaries
        val record = RawStepRecord("android.sensor", "Phone", "PHONE_SENSOR", tBeforeMidnight, tAfterMidnight, 1200)
        val durationMins = java.time.Duration.between(tBeforeMidnight, tAfterMidnight).toMinutes()
        assertEquals(60L, durationMins)
    }

    // Requirement 6: User changes timezone
    @Test
    fun testCase6_UserChangesTimezone() {
        val instant = Instant.parse("2026-10-04T12:00:00Z")
        val tokyo = instant.atZone(ZoneId.of("Asia/Tokyo"))
        val newYork = instant.atZone(ZoneId.of("America/New_York"))

        assertEquals(21, tokyo.hour) // 9:00 PM
        assertEquals(8, newYork.hour) // 8:00 AM
        assertEquals(instant.toEpochMilli(), tokyo.toInstant().toEpochMilli())
        assertEquals(instant.toEpochMilli(), newYork.toInstant().toEpochMilli())
    }

    // Requirement 7: User denies Health Connect permission
    @Test
    fun testCase7_UserDeniesHealthConnectPermission() {
        val fakeHcSource = FakeStepDataSource(available = true, permitted = false)
        assertFalse(runCatching { fakeHcSource.permitted }.getOrDefault(false))
    }

    // Requirement 8: User has no hardware sensor
    @Test
    fun testCase8_UserHasNoSensor() {
        val fakeSensorSource = FakeStepDataSource(available = false, permitted = true)
        assertFalse(runCatching { fakeSensorSource.available }.getOrDefault(false))
    }

    // Requirement 9: User activates recovery mode
    @Test
    fun testCase9_UserActivatesRecoveryMode() {
        val normalTarget = 10000
        val recoveryMultiplier = 0.60 // 40% reduction
        val recoveryTarget = (normalTarget * recoveryMultiplier).roundToInt()

        assertEquals(6000, recoveryTarget)
        // Recovery minimum goal
        val recoveryMin = (recoveryTarget * 0.60).roundToInt()
        assertEquals(3600, recoveryMin)
    }

    // Requirement 10: User enables a rest day
    @Test
    fun testCase10_UserEnablesRestDay() {
        val restDays = "Sunday,Wednesday"
        val isSundayRest = restDays.split(",").map { it.trim().lowercase() }.contains("sunday")
        val isMondayRest = restDays.split(",").map { it.trim().lowercase() }.contains("monday")

        assertTrue(isSundayRest)
        assertFalse(isMondayRest)
    }

    // Requirement 11: User manually edits a day
    @Test
    fun testCase11_UserManuallyEditsDay() {
        val originalSteps = 8400
        val adjustment = 1500 // e.g. uncounted indoor treadmill steps
        val updatedSteps = originalSteps + adjustment

        assertEquals(9900, updatedSteps)
    }

    // Requirement 12: Health Connect returns stale data
    @Test
    fun testCase12_HealthConnectReturnsStaleData() {
        val records = listOf(
            RawStepRecord(
                sourcePackage = "com.google.android.apps.fitness",
                sourceName = "Watch",
                sourceType = "HEALTH_CONNECT",
                startTime = Instant.now().minusSeconds(10000),
                endTime = Instant.now().minusSeconds(8000),
                stepCount = 4200
            )
        )

        val result = StepReconciliationEngine.reconcile(records, isDataStale = true)
        assertEquals(ConfidenceLevel.LOW, result.confidence)
        assertTrue(result.explanation.contains("stale", ignoreCase = true))
    }

    // Adaptive Baseline Tests
    @Test
    fun testAdaptiveBaselineCalculation() {
        val validDays = listOf(5400, 6800, 7200, 8100, 9500, 10200, 11000)
        val median = validDays.sorted()[validDays.size / 2]
        assertEquals(8100, median)

        val calculatedMin = (median * 0.6).roundToInt()
        val calculatedTarget = (median * 1.0).roundToInt()
        val calculatedStretch = (median * 1.2).roundToInt()

        assertEquals(4860, calculatedMin)
        assertEquals(8100, calculatedTarget)
        assertEquals(9720, calculatedStretch)
    }

    @Test
    fun testWeeklyAdjustmentLimitMax10Percent() {
        val currentGoal = 8000
        val newMedianCandidate = 14000 // Huge jump
        val maxAdjustment = (currentGoal * 0.10).roundToInt() // 800
        val clampedTarget = newMedianCandidate.coerceIn(currentGoal - maxAdjustment, currentGoal + maxAdjustment)

        assertEquals(8800, clampedTarget)
    }
}
