package com.example.data.sync

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object HealthConnectHelper {

    val REQUIRED_PERMISSIONS = setOf(
        HealthPermission.getReadPermission(StepsRecord::class)
    )

    fun isHealthConnectAvailable(context: Context): Boolean {
        return try {
            HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
        } catch (e: Exception) {
            false
        }
    }

    fun getClient(context: Context): HealthConnectClient? {
        return if (isHealthConnectAvailable(context)) {
            try {
                HealthConnectClient.getOrCreate(context)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    suspend fun hasPermissions(context: Context): Boolean {
        val client = getClient(context) ?: return false
        return try {
            val granted = client.permissionController.getGrantedPermissions()
            granted.containsAll(REQUIRED_PERMISSIONS)
        } catch (e: Exception) {
            false
        }
    }

    suspend fun readTodaySteps(context: Context): Long {
        val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
        val now = Instant.now()
        return readStepsInRange(context, startOfDay, now)
    }

    suspend fun readStepsInRange(context: Context, startTime: Instant, endTime: Instant): Long {
        val client = getClient(context) ?: return 0L
        return try {
            val response = client.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
            response.records.sumOf { it.count }
        } catch (e: Exception) {
            0L
        }
    }
}
