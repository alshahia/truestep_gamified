package com.example.data.source

import android.content.Context
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.data.sync.HealthConnectHelper
import com.example.sensor.StepSensorManager
import java.time.Instant

data class RawStepRecord(
    val sourcePackage: String,
    val sourceName: String,
    val sourceType: String, // "HEALTH_CONNECT", "PHONE_SENSOR", "MANUAL"
    val startTime: Instant,
    val endTime: Instant,
    val stepCount: Int
)

interface StepDataSource {
    val sourceIdentifier: String
    suspend fun isAvailable(): Boolean
    suspend fun hasPermission(): Boolean
    suspend fun readStepRecords(startTime: Instant, endTime: Instant): List<RawStepRecord>
}

class HealthConnectStepDataSource(
    private val context: Context
) : StepDataSource {
    override val sourceIdentifier: String = "Health Connect"

    override suspend fun isAvailable(): Boolean {
        return HealthConnectHelper.isHealthConnectAvailable(context)
    }

    override suspend fun hasPermission(): Boolean {
        return HealthConnectHelper.hasPermissions(context)
    }

    override suspend fun readStepRecords(startTime: Instant, endTime: Instant): List<RawStepRecord> {
        val client = HealthConnectHelper.getClient(context) ?: return emptyList()
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.map { record ->
                RawStepRecord(
                    sourcePackage = record.metadata.dataOrigin.packageName,
                    sourceName = "Health Connect (${record.metadata.dataOrigin.packageName.substringAfterLast('.')})",
                    sourceType = "HEALTH_CONNECT",
                    startTime = record.startTime,
                    endTime = record.endTime,
                    stepCount = record.count.toInt()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

class PhoneSensorStepDataSource(
    private val sensorManager: StepSensorManager
) : StepDataSource {
    override val sourceIdentifier: String = "Phone Hardware Sensor"

    override suspend fun isAvailable(): Boolean {
        return sensorManager.isSupported()
    }

    override suspend fun hasPermission(): Boolean {
        return true
    }

    override suspend fun readStepRecords(startTime: Instant, endTime: Instant): List<RawStepRecord> {
        // Fallback phone hardware sensor accumulator
        val currentSteps = sensorManager.latestRawSensorValue.value
        return if (currentSteps > 0) {
            listOf(
                RawStepRecord(
                    sourcePackage = "android.hardware.sensor",
                    sourceName = sensorManager.activeMode.displayName,
                    sourceType = "PHONE_SENSOR",
                    startTime = startTime,
                    endTime = endTime,
                    stepCount = currentSteps
                )
            )
        } else {
            emptyList()
        }
    }
}

class FakeStepDataSource(
    override val sourceIdentifier: String = "Fake Source",
    var available: Boolean = true,
    var permitted: Boolean = true,
    var records: List<RawStepRecord> = emptyList()
) : StepDataSource {
    override suspend fun isAvailable(): Boolean = available
    override suspend fun hasPermission(): Boolean = permitted
    override suspend fun readStepRecords(startTime: Instant, endTime: Instant): List<RawStepRecord> {
        return records.filter { it.startTime >= startTime && it.endTime <= endTime }
    }
}
