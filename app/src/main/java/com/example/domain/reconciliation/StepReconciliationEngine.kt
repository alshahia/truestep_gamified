package com.example.domain.reconciliation

import com.example.data.local.StepSourceRecord
import com.example.data.source.RawStepRecord

enum class ConfidenceLevel {
    HIGH,
    MEDIUM,
    LOW
}

data class ReconciliationResult(
    val processedRecords: List<StepSourceRecord>,
    val totalReconciledSteps: Int,
    val duplicateIntervalsCount: Int,
    val confidence: ConfidenceLevel,
    val explanation: String
)

object StepReconciliationEngine {

    /**
     * Reconciles raw step records from multiple sources (e.g. phone sensor and smartwatch via Health Connect).
     * Conservative algorithm:
     * 1. Sort records by start time.
     * 2. Detect overlapping intervals.
     * 3. Detect duplicate records (same time window or subset).
     * 4. Prefer the primary source (e.g., dedicated Health Connect watch or preferred source).
     * 5. Avoid double counting.
     * 6. Preserve original records and tag whether included or excluded with clear reason.
     * 7. Calculate confidence level.
     */
    fun reconcile(
        records: List<RawStepRecord>,
        primarySourceType: String = "HEALTH_CONNECT",
        dateString: String = "",
        isDataStale: Boolean = false
    ): ReconciliationResult {
        if (records.isEmpty()) {
            return ReconciliationResult(
                processedRecords = emptyList(),
                totalReconciledSteps = 0,
                duplicateIntervalsCount = 0,
                confidence = if (isDataStale) ConfidenceLevel.LOW else ConfidenceLevel.HIGH,
                explanation = "No step records available."
            )
        }

        // 1. Sort by start time, and then by priority of source
        val sorted = records.sortedWith(
            compareBy<RawStepRecord> { it.startTime.toEpochMilli() }
                .thenByDescending { it.sourceType == primarySourceType }
        )

        val processed = mutableListOf<StepSourceRecord>()
        var duplicateCount = 0
        var totalSteps = 0

        // Keep track of accepted intervals (start, end)
        val acceptedIntervals = mutableListOf<Pair<Long, Long>>()

        for (raw in sorted) {
            val startMs = raw.startTime.toEpochMilli()
            val endMs = raw.endTime.toEpochMilli()
            val nowMs = System.currentTimeMillis()

            // Check if this record overlaps significantly with an already accepted record
            val overlap = acceptedIntervals.find { (accStart, accEnd) ->
                // Overlap exists if max(start, accStart) < min(end, accEnd)
                val overlapStart = maxOf(startMs, accStart)
                val overlapEnd = minOf(endMs, accEnd)
                overlapStart < overlapEnd && (overlapEnd - overlapStart) > 10_000L // > 10 seconds
            }

            if (overlap != null) {
                // Overlap detected!
                duplicateCount++
                processed.add(
                    StepSourceRecord(
                        date = dateString,
                        sourcePackage = raw.sourcePackage,
                        sourceName = raw.sourceName,
                        sourceType = raw.sourceType,
                        startTime = startMs,
                        endTime = endMs,
                        stepCount = raw.stepCount,
                        isOverlapping = true,
                        isIncluded = false,
                        exclusionReason = "Overlapped with existing accepted record (${overlap.first} - ${overlap.second})",
                        synchronizedAt = nowMs
                    )
                )
            } else {
                // No conflict: accept record
                acceptedIntervals.add(Pair(startMs, endMs))
                totalSteps += raw.stepCount
                processed.add(
                    StepSourceRecord(
                        date = dateString,
                        sourcePackage = raw.sourcePackage,
                        sourceName = raw.sourceName,
                        sourceType = raw.sourceType,
                        startTime = startMs,
                        endTime = endMs,
                        stepCount = raw.stepCount,
                        isOverlapping = false,
                        isIncluded = true,
                        exclusionReason = null,
                        synchronizedAt = nowMs
                    )
                )
            }
        }

        // Distinct sources check
        val distinctSources = records.map { it.sourceType }.distinct()

        val confidence = when {
            isDataStale -> ConfidenceLevel.LOW
            duplicateCount > 0 && distinctSources.size > 1 -> ConfidenceLevel.MEDIUM
            distinctSources.size == 1 -> ConfidenceLevel.HIGH
            else -> ConfidenceLevel.MEDIUM
        }

        val explanation = when {
            isDataStale -> "Data is stale or synchronization delayed."
            duplicateCount > 0 -> "Reconciled $duplicateCount overlapping intervals between ${distinctSources.joinToString()}."
            distinctSources.size > 1 -> "Combined non-overlapping activity across ${distinctSources.joinToString()}."
            else -> "Single trusted data feed active."
        }

        return ReconciliationResult(
            processedRecords = processed,
            totalReconciledSteps = totalSteps,
            duplicateIntervalsCount = duplicateCount,
            confidence = confidence,
            explanation = explanation
        )
    }
}
