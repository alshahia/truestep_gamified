package com.example.data.repository

import android.content.Context
import com.example.data.local.AchievementDao
import com.example.data.local.AchievementUnlock
import com.example.data.local.DailySteps
import com.example.data.local.GoalStreakCounter
import com.example.data.local.GoalStreakDao
import com.example.data.local.HourlyStepDao
import com.example.data.local.HourlyStepRecord
import com.example.data.local.StepDao
import com.example.data.local.StepSourceRecord
import com.example.data.local.StepSourceRecordDao
import com.example.data.local.UserGoalSettings
import com.example.data.local.UserGoalSettingsDao
import com.example.data.local.WalkingSession
import com.example.data.local.WalkingSessionDao
import com.example.data.sync.HealthConnectHelper
import com.example.domain.content.AchievementRegistry
import com.example.domain.repository.StepRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class StepRepositoryImpl(
    private val stepDao: StepDao,
    private val hourlyStepDao: HourlyStepDao,
    private val goalStreakDao: GoalStreakDao,
    private val achievementDao: AchievementDao,
    private val stepSourceRecordDao: StepSourceRecordDao,
    private val userGoalSettingsDao: UserGoalSettingsDao,
    private val walkingSessionDao: WalkingSessionDao? = null
) : StepRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val stepMutex = Mutex()
    private val _liveTodaySteps = MutableStateFlow<DailySteps?>(null)
    private var lastSourceRecordTime: Long = 0L
    private var pendingSourceStepCount: Int = 0

    private fun getTodayDateString(): String = LocalDate.now().format(dateFormatter)

    // Dynamic date stream that emits whenever calendar date advances (e.g. at 00:00 midnight)
    private val currentDateFlow: Flow<String> = flow {
        while (currentCoroutineContext().isActive) {
            emit(getTodayDateString())
            delay(15_000L) // Check every 15 seconds
        }
    }.distinctUntilChanged()

    override fun getTodaySteps(): Flow<DailySteps?> {
        return currentDateFlow.flatMapLatest { todayStr ->
            combine(
                stepDao.observeByDate(todayStr),
                _liveTodaySteps
            ) { dbRecord, liveRecord ->
                when {
                    liveRecord != null && dbRecord != null -> {
                        if (liveRecord.date == todayStr && dbRecord.date == todayStr) {
                            if (liveRecord.totalSteps >= dbRecord.totalSteps) liveRecord else dbRecord
                        } else if (liveRecord.date == todayStr) {
                            liveRecord
                        } else {
                            dbRecord
                        }
                    }
                    liveRecord != null && liveRecord.date == todayStr -> liveRecord
                    else -> dbRecord
                }
            }
        }
    }

    override fun getTodayHourlyRecords(): Flow<List<HourlyStepRecord>> {
        return currentDateFlow.flatMapLatest { todayStr ->
            hourlyStepDao.observeHourlyForDate(todayStr)
        }
    }

    override fun getStepByDate(date: String): Flow<DailySteps?> {
        return stepDao.observeByDate(date)
    }

    override fun getHourlyRecordsForDate(date: String): Flow<List<HourlyStepRecord>> {
        return hourlyStepDao.observeHourlyForDate(date)
    }

    override fun getAllHistory(): Flow<List<DailySteps>> {
        return stepDao.observeAll()
    }

    override fun getStreak(): Flow<GoalStreakCounter?> {
        return goalStreakDao.observeStreak()
    }

    override fun getUnlocks(): Flow<List<AchievementUnlock>> {
        return achievementDao.observeAllUnlocks()
    }

    override fun getSourceRecords(date: String): Flow<List<StepSourceRecord>> {
        return stepSourceRecordDao.observeRecordsForDate(date)
    }

    override fun getUserGoalSettings(): Flow<UserGoalSettings?> {
        return userGoalSettingsDao.observeSettings()
    }

    override fun getWalkingSessions(): Flow<List<WalkingSession>> {
        return walkingSessionDao?.observeAll() ?: flow { emit(emptyList()) }
    }

    override suspend fun recordStepsDelta(
        delta: Int,
        rawSensorValue: Int,
        sourceName: String
    ) = withContext(Dispatchers.IO) {
        if (delta <= 0) return@withContext
        stepMutex.withLock {
            val today = getTodayDateString()
            val current = stepDao.getByDate(today)
            val settings = userGoalSettingsDao.getSettings() ?: UserGoalSettings()

            val baseSteps = maxOf(
                current?.totalSteps ?: 0,
                _liveTodaySteps.value?.takeIf { it.date == today }?.totalSteps ?: 0
            )
            val newTotal = baseSteps + delta
            val newKm = (newTotal * 0.000762 * 100.0).roundToInt() / 100.0
            val newActiveMins = (newTotal / 100)
            val newCalories = (newTotal * 0.04).roundToInt()

            val updated = DailySteps(
                date = today,
                totalSteps = newTotal,
                goalSteps = current?.goalSteps ?: settings.targetGoal,
                minGoal = current?.minGoal ?: settings.minimumGoal,
                stretchGoal = current?.stretchGoal ?: settings.stretchGoal,
                lastSensorValue = rawSensorValue,
                cumulativeKm = newKm,
                activeMinutes = newActiveMins,
                caloriesKcal = newCalories,
                isRestDay = current?.isRestDay ?: (LocalDate.now().dayOfWeek.name.equals(settings.restDays, ignoreCase = true)),
                isRecoveryDay = current?.isRecoveryDay ?: settings.recoveryModeEnabled,
                dataSource = sourceName,
                confidence = if (current?.dataSource != null && current.dataSource != sourceName) "MEDIUM" else "HIGH",
                notes = current?.notes ?: "",
                lastUpdated = System.currentTimeMillis()
            )

            _liveTodaySteps.value = updated
            stepDao.insertOrUpdate(updated)

            // Record into current hourly bucket
            val currentHour = LocalTime.now().hour
            val existingHour = hourlyStepDao.getHourlyRecord(today, currentHour)
            val updatedHourSteps = (existingHour?.stepsCount ?: 0) + delta
            hourlyStepDao.insertOrUpdate(
                HourlyStepRecord(
                    date = today,
                    hourOfDay = currentHour,
                    stepsCount = updatedHourSteps,
                    lastUpdated = System.currentTimeMillis()
                )
            )

            // Record source interval record
            pendingSourceStepCount += delta
            val now = System.currentTimeMillis()
            if (now - lastSourceRecordTime >= 60_000L || lastSourceRecordTime == 0L) {
                lastSourceRecordTime = now
                val recordedCount = pendingSourceStepCount
                pendingSourceStepCount = 0
                stepSourceRecordDao.insertRecords(
                    listOf(
                        StepSourceRecord(
                            date = today,
                            sourcePackage = "com.example.sensor",
                            sourceName = sourceName,
                            sourceType = if (sourceName.contains("Health Connect", ignoreCase = true)) "HEALTH_CONNECT" else "SENSOR",
                            startTime = now - 60_000L,
                            endTime = now,
                            stepCount = recordedCount,
                            isOverlapping = false,
                            isIncluded = true,
                            synchronizedAt = now
                        )
                    )
                )
            }

            updateStreakForDate(today, newTotal, updated.goalSteps)
            checkAchievements(newTotal)
        }
    }

    override suspend fun setTodayTotalSteps(
        total: Int,
        rawSensorValue: Int,
        sourceName: String
    ) = withContext(Dispatchers.IO) {
        stepMutex.withLock {
            val today = getTodayDateString()
            val current = stepDao.getByDate(today)
            val settings = userGoalSettingsDao.getSettings() ?: UserGoalSettings()

            val newKm = (total * 0.000762 * 100.0).roundToInt() / 100.0
            val newActiveMins = (total / 100)
            val newCalories = (total * 0.04).roundToInt()

            val updated = DailySteps(
                date = today,
                totalSteps = total,
                goalSteps = current?.goalSteps ?: settings.targetGoal,
                minGoal = current?.minGoal ?: settings.minimumGoal,
                stretchGoal = current?.stretchGoal ?: settings.stretchGoal,
                lastSensorValue = rawSensorValue,
                cumulativeKm = newKm,
                activeMinutes = newActiveMins,
                caloriesKcal = newCalories,
                isRestDay = current?.isRestDay ?: false,
                isRecoveryDay = current?.isRecoveryDay ?: settings.recoveryModeEnabled,
                dataSource = sourceName,
                confidence = "HIGH",
                notes = current?.notes ?: "",
                lastUpdated = System.currentTimeMillis()
            )

            _liveTodaySteps.value = updated
            stepDao.insertOrUpdate(updated)

            val currentHour = LocalTime.now().hour
            val existingHour = hourlyStepDao.getHourlyRecord(today, currentHour)
            val delta = maxOf(0, total - (current?.totalSteps ?: 0))
            val updatedHourSteps = (existingHour?.stepsCount ?: 0) + delta
            hourlyStepDao.insertOrUpdate(
                HourlyStepRecord(
                    date = today,
                    hourOfDay = currentHour,
                    stepsCount = updatedHourSteps,
                    lastUpdated = System.currentTimeMillis()
                )
            )

            updateStreakForDate(today, total, updated.goalSteps)
            checkAchievements(total)
        }
    }

    override suspend fun updateGoal(newGoal: Int) = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val current = stepDao.getByDate(today)
        if (current != null) {
            val min = (newGoal * 0.6).roundToInt()
            val stretch = (newGoal * 1.2).roundToInt()
            stepDao.insertOrUpdate(
                current.copy(
                    goalSteps = newGoal,
                    minGoal = min,
                    stretchGoal = stretch,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
        val settings = userGoalSettingsDao.getSettings() ?: UserGoalSettings()
        userGoalSettingsDao.insertOrUpdate(
            settings.copy(
                targetGoal = newGoal,
                minimumGoal = (newGoal * 0.6).roundToInt(),
                stretchGoal = (newGoal * 1.2).roundToInt()
            )
        )
    }

    override suspend fun updateNotes(date: String, notes: String) = withContext(Dispatchers.IO) {
        stepDao.updateNotes(date, notes)
    }

    override suspend fun updateSettings(settings: UserGoalSettings) = withContext(Dispatchers.IO) {
        userGoalSettingsDao.insertOrUpdate(settings)
    }

    override suspend fun recalculateAdaptiveGoal(): Int = withContext(Dispatchers.IO) {
        val recentDays = stepDao.getRecentDays(14).filter { it.totalSteps > 500 }
        if (recentDays.size < 3) {
            return@withContext 10000
        }
        val sortedTotals = recentDays.map { it.totalSteps }.sorted()
        val median = if (sortedTotals.size % 2 == 0) {
            (sortedTotals[sortedTotals.size / 2 - 1] + sortedTotals[sortedTotals.size / 2]) / 2
        } else {
            sortedTotals[sortedTotals.size / 2]
        }

        val settings = userGoalSettingsDao.getSettings() ?: UserGoalSettings()
        if (settings.lockedGoalEnabled) {
            return@withContext settings.targetGoal
        }

        val currentGoal = settings.targetGoal
        val diff = median - currentGoal
        val maxAdjustment = (currentGoal * 0.08).roundToInt()
        val clampedAdjustment = diff.coerceIn(-maxAdjustment, maxAdjustment)
        val newTarget = ((currentGoal + clampedAdjustment) / 100) * 100

        updateGoal(newTarget)
        return@withContext newTarget
    }

    private suspend fun updateStreakForDate(today: String, todaySteps: Int, goal: Int) {
        val streakRecord = goalStreakDao.getStreak() ?: GoalStreakCounter()
        val metGoal = todaySteps >= goal
        val todayDate = LocalDate.parse(today, dateFormatter)
        val yesterdayDate = todayDate.minusDays(1)
        val yesterdayStr = yesterdayDate.format(dateFormatter)

        val updatedStreak = if (metGoal) {
            when (streakRecord.lastAchievedDate) {
                today -> streakRecord
                yesterdayStr -> {
                    val newCount = streakRecord.currentStreak + 1
                    streakRecord.copy(
                        currentStreak = newCount,
                        bestStreak = maxOf(newCount, streakRecord.bestStreak),
                        lastAchievedDate = today,
                        updatedAt = System.currentTimeMillis()
                    )
                }
                else -> {
                    // Check if days between lastAchievedDate and today were rest days
                    val isContinuedThroughRestDays = if (streakRecord.lastAchievedDate.isNotBlank()) {
                        val lastDate = LocalDate.parse(streakRecord.lastAchievedDate, dateFormatter)
                        val daysBetween = ChronoUnit.DAYS.between(lastDate, todayDate)
                        if (daysBetween in 2..7) {
                            val settings = userGoalSettingsDao.getSettings() ?: UserGoalSettings()
                            val missingDays = (1 until daysBetween).map { lastDate.plusDays(it) }
                            missingDays.all { d ->
                                d.dayOfWeek.name.equals(settings.restDays, ignoreCase = true) || settings.recoveryModeEnabled
                            }
                        } else false
                    } else false

                    val newCount = if (isContinuedThroughRestDays) streakRecord.currentStreak + 1 else 1
                    streakRecord.copy(
                        currentStreak = newCount,
                        bestStreak = maxOf(newCount, streakRecord.bestStreak),
                        lastAchievedDate = today,
                        updatedAt = System.currentTimeMillis()
                    )
                }
            }
        } else {
            // Did not meet goal today: check if streak was broken yesterday
            if (streakRecord.lastAchievedDate.isNotBlank()) {
                val lastDate = LocalDate.parse(streakRecord.lastAchievedDate, dateFormatter)
                val daysSinceLast = ChronoUnit.DAYS.between(lastDate, todayDate)
                if (daysSinceLast > 1) {
                    val settings = userGoalSettingsDao.getSettings() ?: UserGoalSettings()
                    val yesterdayDay = yesterdayDate.dayOfWeek.name
                    val wasRestDay = yesterdayDay.equals(settings.restDays, ignoreCase = true) || settings.recoveryModeEnabled
                    if (!wasRestDay && daysSinceLast > 2) {
                        // Broken streak
                        streakRecord.copy(currentStreak = 0, updatedAt = System.currentTimeMillis())
                    } else {
                        streakRecord
                    }
                } else {
                    streakRecord
                }
            } else {
                streakRecord
            }
        }

        goalStreakDao.insertOrUpdate(updatedStreak)
    }

    override suspend fun evaluateStreakContinuity() = withContext(Dispatchers.IO) {
        val streakRecord = goalStreakDao.getStreak() ?: return@withContext
        if (streakRecord.currentStreak == 0 || streakRecord.lastAchievedDate.isBlank()) return@withContext

        val todayDate = LocalDate.now()
        val lastDate = LocalDate.parse(streakRecord.lastAchievedDate, dateFormatter)
        val daysSinceLast = ChronoUnit.DAYS.between(lastDate, todayDate)

        if (daysSinceLast > 1) {
            val settings = userGoalSettingsDao.getSettings() ?: UserGoalSettings()
            val missedDays = (1 until daysSinceLast).map { lastDate.plusDays(it) }
            val allMissedWereRest = missedDays.all { d ->
                d.dayOfWeek.name.equals(settings.restDays, ignoreCase = true) || settings.recoveryModeEnabled
            }
            if (!allMissedWereRest && daysSinceLast >= 2) {
                // Streak broken
                goalStreakDao.insertOrUpdate(
                    streakRecord.copy(
                        currentStreak = 0,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    // O(1) aggregate check instead of O(N) memory query
    private suspend fun checkAchievements(todaySteps: Int) {
        val aggregate = stepDao.getHistoryAggregate()
        val streak = goalStreakDao.getStreak()?.currentStreak ?: 0
        checkAndRecordAchievements(todaySteps, streak, aggregate.totalDays, aggregate.totalDistanceKm)
    }

    override suspend fun checkAndRecordAchievements(
        todaySteps: Int,
        streak: Int,
        totalDays: Int,
        totalKm: Double
    ) = withContext(Dispatchers.IO) {
        if (todaySteps >= 4000) unlock("goal_4k")
        if (todaySteps >= 6000) unlock("goal_6k")
        if (todaySteps >= 8000) unlock("goal_8k")
        if (todaySteps >= 10000) unlock("goal_10k")
        if (todaySteps >= 12000) unlock("goal_12k")
        if (todaySteps >= 15000) unlock("goal_15k")
        if (todaySteps >= 20000) unlock("goal_20k")
        if (todaySteps >= 25000) unlock("goal_25k")

        if (totalDays >= 1) unlock("days_1")
        if (totalDays >= 3) unlock("days_3")
        if (totalDays >= 7) unlock("days_7")
        if (totalDays >= 14) unlock("days_14")
        if (totalDays >= 30) unlock("days_30")
        if (totalDays >= 60) unlock("days_60")
        if (totalDays >= 90) unlock("days_90")
        if (totalDays >= 180) unlock("days_180")
    }

    private suspend fun unlock(achievementId: String) {
        achievementDao.unlock(AchievementUnlock(achievementId = achievementId))
    }

    override suspend fun pruneOldSourceRecords(olderThanMillis: Long): Int = withContext(Dispatchers.IO) {
        val cutoff = System.currentTimeMillis() - olderThanMillis
        stepSourceRecordDao.pruneRecordsOlderThan(cutoff)
    }

    override suspend fun syncHealthConnectSteps(context: Context): Int = withContext(Dispatchers.IO) {
        if (!HealthConnectHelper.isHealthConnectAvailable(context)) return@withContext 0
        if (!HealthConnectHelper.hasPermissions(context)) return@withContext 0

        val healthSteps = HealthConnectHelper.readTodaySteps(context).toInt()
        if (healthSteps <= 0) return@withContext 0

        val today = getTodayDateString()
        val current = stepDao.getByDate(today)
        val currentSteps = current?.totalSteps ?: 0

        if (healthSteps > currentSteps) {
            val delta = healthSteps - currentSteps
            recordStepsDelta(delta, healthSteps, "Health Connect")
            return@withContext delta
        }
        return@withContext 0
    }

    override suspend fun clearAllData() = withContext(Dispatchers.IO) {
        _liveTodaySteps.value = null
        stepDao.deleteAll()
        hourlyStepDao.deleteAll()
        stepSourceRecordDao.deleteAll()
        achievementDao.deleteAll()
        walkingSessionDao?.deleteAll()
        goalStreakDao.insertOrUpdate(GoalStreakCounter(id = 1, currentStreak = 0, bestStreak = 0, lastAchievedDate = ""))
    }

    override suspend fun saveWalkingSession(session: WalkingSession): Long = withContext(Dispatchers.IO) {
        walkingSessionDao?.insertSession(session) ?: 0L
    }

    override suspend fun deleteWalkingSession(id: Long) = withContext(Dispatchers.IO) {
        walkingSessionDao?.deleteSession(id) ?: Unit
    }

    override suspend fun exportHistoryAsCsv(): String = withContext(Dispatchers.IO) {
        val days = stepDao.getAllList()
        val sb = StringBuilder()
        sb.append("Date,TotalSteps,GoalSteps,DistanceKm,ActiveMinutes,CaloriesKcal,DataSource,Confidence,Notes\n")
        days.forEach { d ->
            sb.append("${d.date},${d.totalSteps},${d.goalSteps},${d.cumulativeKm},${d.activeMinutes},${d.caloriesKcal},\"${d.dataSource}\",\"${d.confidence}\",\"${d.notes}\"\n")
        }
        return@withContext sb.toString()
    }

    override suspend fun exportHistoryAsJson(): String = withContext(Dispatchers.IO) {
        val days = stepDao.getAllList()
        val sb = StringBuilder()
        sb.append("[\n")
        days.forEachIndexed { index, d ->
            sb.append("  {\n")
            sb.append("    \"date\": \"${d.date}\",\n")
            sb.append("    \"totalSteps\": ${d.totalSteps},\n")
            sb.append("    \"goalSteps\": ${d.goalSteps},\n")
            sb.append("    \"distanceKm\": ${d.cumulativeKm},\n")
            sb.append("    \"activeMinutes\": ${d.activeMinutes},\n")
            sb.append("    \"caloriesKcal\": ${d.caloriesKcal},\n")
            sb.append("    \"dataSource\": \"${d.dataSource}\",\n")
            sb.append("    \"confidence\": \"${d.confidence}\",\n")
            sb.append("    \"notes\": \"${d.notes}\"\n")
            sb.append("  }${if (index < days.size - 1) "," else ""}\n")
        }
        sb.append("]\n")
        return@withContext sb.toString()
    }

    override suspend fun simulateSampleDataIfEmpty() = withContext(Dispatchers.IO) {
        val existing = stepDao.getAllList()
        if (existing.isNotEmpty()) return@withContext

        val today = LocalDate.now()
        val sampleDays = listOf(
            Pair(-13, 6210),
            Pair(-12, 8840),
            Pair(-11, 10240),
            Pair(-10, 7310),
            Pair(-9, 5120),
            Pair(-8, 9480),
            Pair(-7, 11020),
            Pair(-6, 8230),
            Pair(-5, 6890),
            Pair(-4, 10410),
            Pair(-3, 7650),
            Pair(-2, 9120),
            Pair(-1, 11860),
            Pair(0, 8432)
        )

        sampleDays.forEach { (offset, steps) ->
            val dateStr = today.plusDays(offset.toLong()).format(dateFormatter)
            val km = (steps * 0.000762 * 100.0).roundToInt() / 100.0
            val mins = steps / 100
            val kcal = (steps * 0.04).roundToInt()
            val dayRecord = DailySteps(
                date = dateStr,
                totalSteps = steps,
                goalSteps = 10000,
                minGoal = 6000,
                stretchGoal = 12000,
                lastSensorValue = steps,
                cumulativeKm = km,
                activeMinutes = mins,
                caloriesKcal = kcal,
                isRestDay = offset == -4,
                isRecoveryDay = false,
                dataSource = if (offset == 0) "Phone Hardware Sensor" else "Health Connect",
                confidence = "HIGH",
                notes = if (offset == -5) "Evening park walk with family" else "",
                lastUpdated = System.currentTimeMillis()
            )
            stepDao.insertOrUpdate(dayRecord)

            stepSourceRecordDao.insertRecords(
                listOf(
                    StepSourceRecord(
                        date = dateStr,
                        sourcePackage = "com.google.android.apps.healthdata",
                        sourceName = if (offset == 0) "Phone Hardware Sensor" else "Health Connect",
                        sourceType = if (offset == 0) "SENSOR" else "HEALTH_CONNECT",
                        startTime = System.currentTimeMillis() - 86400000L * (-offset),
                        endTime = System.currentTimeMillis() - 86400000L * (-offset) + 3600000L,
                        stepCount = steps,
                        isOverlapping = false,
                        isIncluded = true
                    )
                )
            )
        }

        val todayStr = today.format(dateFormatter)
        val hourlyDistribution = mapOf(
            6 to 92,
            7 to 504,
            8 to 1056,
            9 to 408,
            10 to 240,
            11 to 312,
            12 to 660,
            13 to 456,
            14 to 264,
            15 to 360,
            16 to 576,
            17 to 912,
            18 to 1152,
            19 to 744,
            20 to 408,
            21 to 216,
            22 to 72
        )
        val hourlyRecords = (0..23).map { h ->
            HourlyStepRecord(
                date = todayStr,
                hourOfDay = h,
                stepsCount = hourlyDistribution[h] ?: 0,
                lastUpdated = System.currentTimeMillis()
            )
        }
        hourlyStepDao.insertRecords(hourlyRecords)

        goalStreakDao.insertOrUpdate(
            GoalStreakCounter(
                id = 1,
                currentStreak = 4,
                bestStreak = 12,
                lastAchievedDate = today.minusDays(1).format(dateFormatter)
            )
        )

        userGoalSettingsDao.insertOrUpdate(
            UserGoalSettings(
                id = 1,
                minimumGoal = 6000,
                targetGoal = 10000,
                stretchGoal = 12000,
                adaptiveGoalsEnabled = true,
                lockedGoalEnabled = false,
                restDays = "Sunday",
                recoveryModeEnabled = false
            )
        )

        unlock("days_1")
        unlock("days_3")
        unlock("days_7")
        unlock("goal_4k")
        unlock("goal_6k")
        unlock("goal_8k")
        unlock("goal_10k")
    }
}
