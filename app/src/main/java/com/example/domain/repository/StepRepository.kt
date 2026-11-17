package com.example.domain.repository

import android.content.Context
import com.example.data.local.AchievementUnlock
import com.example.data.local.DailySteps
import com.example.data.local.GoalStreakCounter
import com.example.data.local.HourlyStepRecord
import com.example.data.local.StepSourceRecord
import com.example.data.local.UserGoalSettings
import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun getTodaySteps(): Flow<DailySteps?>
    fun getTodayHourlyRecords(): Flow<List<HourlyStepRecord>>
    fun getStepByDate(date: String): Flow<DailySteps?>
    fun getHourlyRecordsForDate(date: String): Flow<List<HourlyStepRecord>>
    fun getAllHistory(): Flow<List<DailySteps>>
    fun getStreak(): Flow<GoalStreakCounter?>
    fun getUnlocks(): Flow<List<AchievementUnlock>>
    fun getSourceRecords(date: String): Flow<List<StepSourceRecord>>
    fun getUserGoalSettings(): Flow<UserGoalSettings?>
    fun getWalkingSessions(): Flow<List<com.example.data.local.WalkingSession>>

    suspend fun recordStepsDelta(delta: Int, rawSensorValue: Int, sourceName: String = "Hardware Sensor")
    suspend fun setTodayTotalSteps(total: Int, rawSensorValue: Int, sourceName: String = "Hardware Sensor")
    suspend fun updateGoal(newGoal: Int)
    suspend fun updateNotes(date: String, notes: String)
    suspend fun updateSettings(settings: UserGoalSettings)
    suspend fun recalculateAdaptiveGoal(): Int
    suspend fun checkAndRecordAchievements(todaySteps: Int, streak: Int, totalDays: Int, totalKm: Double)
    suspend fun pruneOldSourceRecords(olderThanMillis: Long): Int
    suspend fun syncHealthConnectSteps(context: Context): Int
    suspend fun evaluateStreakContinuity()
    suspend fun clearAllData()
    suspend fun saveWalkingSession(session: com.example.data.local.WalkingSession): Long
    suspend fun deleteWalkingSession(id: Long)
    suspend fun exportHistoryAsCsv(): String
    suspend fun exportHistoryAsJson(): String
    suspend fun simulateSampleDataIfEmpty()
}
