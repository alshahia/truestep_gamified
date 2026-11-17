package com.example.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AchievementUnlock
import com.example.data.local.DailySteps
import com.example.data.local.GoalStreakCounter
import com.example.data.local.HourlyStepRecord
import com.example.data.local.StepSourceRecord
import com.example.data.local.UserGoalSettings
import com.example.data.local.WalkingSession
import com.example.data.preferences.UserPreferencesRepository
import com.example.domain.content.AchievementRegistry
import com.example.domain.content.DestinationRegistry
import com.example.domain.model.Destination
import com.example.domain.repository.StepRepository
import com.example.sensor.SensorMode
import com.example.sensor.StepSensorManager
import com.example.ui.model.ChartDataCalculator
import com.example.ui.model.ChartRangeData
import com.example.ui.model.HistoryChartRange
import com.example.ui.theme.DarkModeConfig
import com.example.ui.theme.ThemeChoice
import com.example.ui.screens.settings.model.SettingsUiState
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.R
import com.example.domain.content.QuestRegistry
import com.example.domain.model.DailyQuest
import com.example.domain.model.PaceZone
import com.example.domain.model.HunterProfile
import com.example.domain.model.GearItem
import com.example.domain.content.GearRegistry
import com.example.service.StepCounterForegroundService
import com.example.util.MetricFormatter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

data class StepUiState(
    val currentSteps: Int = 0,
    val goalSteps: Int = 10000,
    val minGoal: Int = 6000,
    val stretchGoal: Int = 12000,
    val cumulativeKm: Double = 0.0,
    val activeMinutes: Int = 0,
    val caloriesKcal: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val dataSource: String = "Hardware Step Counter",
    val confidence: String = "HIGH",
    val isPaused: Boolean = false,
    val isSensorAvailable: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis(),
    val nextDestination: Destination? = null,
    val allHistory: List<DailySteps> = emptyList(),
    val todaySourceRecords: List<StepSourceRecord> = emptyList(),
    val userGoalSettings: UserGoalSettings? = null,
    val selectedTheme: ThemeChoice = ThemeChoice.CYBERNETIC_STRIDE,
    val darkModeConfig: DarkModeConfig = DarkModeConfig.SYSTEM,
    val selectedLanguage: AppLanguage = AppLanguage.SYSTEM,
    val isRestDay: Boolean = false,
    val isRecoveryMode: Boolean = false,
    val overlappingRecordsCount: Int = 0,
    val activeSensorMode: SensorMode = SensorMode.HARDWARE_STEP_COUNTER,
    val hourlySteps: List<Int> = List(24) { 0 },
    val selectedScrubHour: Int? = null,
    val chartRange: HistoryChartRange = HistoryChartRange.DAILY,
    val chartRangeData: ChartRangeData? = null,
    val unlockedAchievementIds: Set<String> = emptySet(),
    val hasCompletedOnboarding: Boolean = false,
    val isPreferencesLoaded: Boolean = false,
    val isHealthConnectSyncing: Boolean = false,
    val isLiveNotificationEnabled: Boolean = true,
    val isHighContrastEnabled: Boolean = false,
    val walkingSessions: List<WalkingSession> = emptyList(),
    val bankedStreakFreezes: Int = 1,
    val isStreakProtectedToday: Boolean = false,
    val dailyQuests: List<DailyQuest> = emptyList(),
    val currentCadenceSpm: Int = 0,
    val activePaceZone: PaceZone = PaceZone.STROLL,
    val showCelebrationDialog: Boolean = false,
    val celebrationTitleRes: Int = R.string.celebration_title,
    val hunterProfile: HunterProfile = HunterProfile(),
    val allGearItems: List<GearItem> = GearRegistry.allGearItems,
    val showWorkoutSummary: Boolean = false,
    val lastCompletedSession: WalkingSession? = null,
    val showReviewPromptDialog: Boolean = false,
    val showSystemDialog: Boolean = false,
    val systemDialogTitleRes: Int = R.string.system_window_header,
    val systemDialogBody: String = "",
    val systemDialogEventType: String = "LEVEL_UP"
) {
    val completedQuestsCount: Int
        get() = dailyQuests.count { it.isCompleted }

    val totalQuestsCount: Int
        get() = dailyQuests.size.coerceAtLeast(1)

    val isScrubbing: Boolean
        get() = selectedScrubHour != null

    val isFullDay: Boolean
        get() = selectedScrubHour == null

    val displaySteps: Int
        get() {
            val scrub = selectedScrubHour ?: return currentSteps
            if (scrub <= 0) return hourlySteps.getOrElse(0) { 0 }
            if (scrub >= 23) return currentSteps.coerceAtLeast(hourlySteps.sum())
            val sum = hourlySteps.take(scrub + 1).sum()
            val totalSum = hourlySteps.sum()
            return if (totalSum > 0 && currentSteps > totalSum) {
                ((sum.toDouble() / totalSum) * currentSteps).roundToInt()
            } else {
                sum.coerceAtMost(currentSteps)
            }
        }

    val displayProgress: Float
        get() = if (goalSteps > 0) (displaySteps.toFloat() / goalSteps) else 0f

    val displayPercent: Int
        get() = (displayProgress * 100).toInt().coerceAtLeast(0)

    val displayKm: Double
        get() {
            if (isFullDay) return cumulativeKm
            if (displaySteps == 0) return 0.0
            return (displaySteps * 0.000762 * 100.0).roundToInt() / 100.0
        }

    val displayCalories: Int
        get() {
            if (isFullDay) return caloriesKcal
            if (displaySteps == 0) return 0
            return (displaySteps * 0.04).roundToInt()
        }

    val hourSteps: Int
        get() {
            val hour = selectedScrubHour ?: LocalTime.now().hour
            return hourlySteps.getOrElse(hour) { 0 }
        }

    val hourKm: Double
        get() = (hourSteps * 0.000762 * 100.0).roundToInt() / 100.0

    val hourCalories: Int
        get() = (hourSteps * 0.04).roundToInt()

    val hourSharePercent: Int
        get() {
            val total = currentSteps.coerceAtLeast(1)
            return ((hourSteps.toFloat() / total) * 100).roundToInt().coerceIn(0, 100)
        }
}

class StepViewModel(
    private val repository: StepRepository,
    private val sensorManager: StepSensorManager,
    private val preferencesRepository: UserPreferencesRepository? = null
) : ViewModel() {

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _fallbackTheme = MutableStateFlow(ThemeChoice.CYBERNETIC_STRIDE)
    private val _selectedScrubHour = MutableStateFlow<Int?>(null)
    val selectedScrubHour: StateFlow<Int?> = _selectedScrubHour.asStateFlow()

    private val _chartRange = MutableStateFlow(HistoryChartRange.DAILY)
    val chartRange: StateFlow<HistoryChartRange> = _chartRange.asStateFlow()

    private val _isSyncingHealthConnect = MutableStateFlow(false)
    val isSyncingHealthConnect: StateFlow<Boolean> = _isSyncingHealthConnect.asStateFlow()

    private data class RepoSnapshot(
        val todayRecord: DailySteps?,
        val streakCounter: GoalStreakCounter?,
        val history: List<DailySteps>,
        val settings: UserGoalSettings?,
        val hourlyRecords: List<HourlyStepRecord>,
        val unlocks: List<AchievementUnlock>,
        val sessions: List<WalkingSession>
    )

    private data class UiControls(
        val isPaused: Boolean,
        val theme: ThemeChoice,
        val darkMode: DarkModeConfig,
        val language: AppLanguage,
        val scrubHour: Int?,
        val range: HistoryChartRange,
        val completedOnboarding: Boolean,
        val isSyncingHC: Boolean,
        val isPreferencesLoaded: Boolean
    )

    private val _fallbackDarkMode = MutableStateFlow(DarkModeConfig.SYSTEM)
    private val _fallbackLanguage = MutableStateFlow(AppLanguage.SYSTEM)

    private val themeFlow = preferencesRepository?.selectedTheme ?: _fallbackTheme
    private val darkModeFlow = preferencesRepository?.darkModeConfig ?: _fallbackDarkMode
    private val languageFlow = preferencesRepository?.selectedLanguage ?: _fallbackLanguage
    private val onboardingFlow = preferencesRepository?.hasCompletedOnboarding ?: flowOf(false)
    private val isLoadedFlow = preferencesRepository?.isPreferencesLoaded ?: flowOf(true)

    private val repoFlow = combine(
        repository.getTodaySteps(),
        repository.getStreak(),
        repository.getAllHistory(),
        repository.getUserGoalSettings(),
        combine(
            repository.getTodayHourlyRecords(),
            repository.getUnlocks(),
            repository.getWalkingSessions()
        ) { h, u, w -> Triple(h, u, w) }
    ) { todayRecord, streakCounter, history, settings, (hourlyRecords, unlocks, sessions) ->
        RepoSnapshot(todayRecord, streakCounter, history, settings, hourlyRecords, unlocks, sessions)
    }

    private val displayPrefsFlow = combine(
        themeFlow,
        darkModeFlow,
        languageFlow
    ) { theme, darkMode, lang -> Triple(theme, darkMode, lang) }

    private val liveNotificationFlow = preferencesRepository?.isLiveNotificationEnabled ?: flowOf(true)
    private val highContrastFlow = preferencesRepository?.isHighContrastEnabled ?: flowOf(false)

    private val _currentCadenceSpm = MutableStateFlow(0)
    private val _activePaceZone = MutableStateFlow(PaceZone.STROLL)
    private val _showCelebrationDialog = MutableStateFlow(false)
    private val _hasCelebratedToday = MutableStateFlow(false)
    private val _showWorkoutSummary = MutableStateFlow(false)
    private val _lastCompletedSession = MutableStateFlow<WalkingSession?>(null)
    private val _showReviewPromptDialog = MutableStateFlow(false)
    private val _showSystemDialog = MutableStateFlow(false)
    private val _systemDialogTitle = MutableStateFlow(R.string.system_window_header)
    private val _systemDialogBody = MutableStateFlow("")
    private val _systemDialogEventType = MutableStateFlow("LEVEL_UP")
    private val _lastKnownLevel = MutableStateFlow(14)

    private val bankedFreezesFlow = preferencesRepository?.bankedStreakFreezes ?: flowOf(1)
    private val lastFreezeUsedDateFlow = preferencesRepository?.lastFreezeUsedDate ?: flowOf("")
    private val completedQuestsFlow = preferencesRepository?.completedQuestIds ?: flowOf(emptySet())
    private val questDateFlow = preferencesRepository?.questDate ?: flowOf("")

    private val equippedFootwearFlow = preferencesRepository?.equippedFootwear ?: flowOf("gear_neon_phantom")
    private val equippedCoreFlow = preferencesRepository?.equippedCore ?: flowOf("gear_zephyr_engine")
    private val equippedExosuitFlow = preferencesRepository?.equippedExosuit ?: flowOf("gear_nanoweave_vest")
    private val equippedChronoFlow = preferencesRepository?.equippedChrono ?: flowOf("gear_chrono_watch")
    private val sparksFlow = preferencesRepository?.bankedSparks ?: flowOf(48)
    private val coresFlow = preferencesRepository?.bankedCores ?: flowOf(6)
    private val shardsFlow = preferencesRepository?.bankedShards ?: flowOf(1420)

    private val hunterStrengthFlow = preferencesRepository?.hunterStrength ?: flowOf(45)
    private val hunterAgilityFlow = preferencesRepository?.hunterAgility ?: flowOf(62)
    private val hunterVitalityFlow = preferencesRepository?.hunterVitality ?: flowOf(38)
    private val hunterSenseFlow = preferencesRepository?.hunterSense ?: flowOf(50)
    private val availableStatPointsFlow = preferencesRepository?.availableStatPoints ?: flowOf(3)

    private data class JourneySnapshot(
        val bankedFreezes: Int,
        val lastFreezeDate: String,
        val completedQuests: Set<String>,
        val questDate: String,
        val cadenceSpm: Int,
        val paceZone: PaceZone,
        val showCelebration: Boolean,
        val footwearId: String,
        val coreId: String,
        val exosuitId: String,
        val chronoId: String,
        val sparks: Int,
        val cores: Int,
        val shards: Int,
        val showWorkoutSummary: Boolean,
        val lastSession: WalkingSession?,
        val showReviewPrompt: Boolean,
        val strength: Int,
        val agility: Int,
        val vitality: Int,
        val sense: Int,
        val availableStatPoints: Int,
        val showSystemDialog: Boolean,
        val systemDialogTitle: Int,
        val systemDialogBody: String,
        val systemDialogEventType: String
    )

    private val hunterStatsFlow = combine(
        hunterStrengthFlow,
        hunterAgilityFlow,
        hunterVitalityFlow,
        hunterSenseFlow,
        availableStatPointsFlow
    ) { str, agi, vit, sen, pts ->
        listOf(str, agi, vit, sen, pts)
    }

    private val hunterPrefsFlow = combine(
        combine(equippedFootwearFlow, equippedCoreFlow, equippedExosuitFlow, equippedChronoFlow) { f, c, e, ch ->
            listOf(f, c, e, ch)
        },
        combine(sparksFlow, coresFlow, shardsFlow) { s, co, sh ->
            Triple(s, co, sh)
        },
        hunterStatsFlow
    ) { gearList, (sparks, cores, shards), stats ->
        listOf(
            gearList[0], gearList[1], gearList[2], gearList[3],
            sparks, cores, shards,
            stats[0], stats[1], stats[2], stats[3], stats[4]
        )
    }

    private val systemDialogFlow = combine(
        _showSystemDialog,
        _systemDialogTitle,
        _systemDialogBody,
        _systemDialogEventType
    ) { show, title, body, type ->
        listOf(show, title, body, type)
    }

    private val journeyFlow = combine(
        combine(bankedFreezesFlow, lastFreezeUsedDateFlow, completedQuestsFlow, questDateFlow) { bf, lfd, cq, qd ->
            listOf(bf, lfd, cq, qd)
        },
        combine(
            combine(_currentCadenceSpm, _activePaceZone, _showCelebrationDialog) { c, p, s -> Triple(c, p, s) },
            combine(_showWorkoutSummary, _lastCompletedSession, _showReviewPromptDialog) { ws, ls, rp -> Triple(ws, ls, rp) },
            systemDialogFlow
        ) { (c, p, s), (ws, ls, rp), sys ->
            listOf(c, p, s, ws, ls, rp, sys[0], sys[1], sys[2], sys[3])
        },
        hunterPrefsFlow
    ) { prefs, sessionStates, hunterPrefs ->
        @Suppress("UNCHECKED_CAST")
        JourneySnapshot(
            bankedFreezes = prefs[0] as Int,
            lastFreezeDate = prefs[1] as String,
            completedQuests = prefs[2] as Set<String>,
            questDate = prefs[3] as String,
            cadenceSpm = sessionStates[0] as Int,
            paceZone = sessionStates[1] as PaceZone,
            showCelebration = sessionStates[2] as Boolean,
            showWorkoutSummary = sessionStates[3] as Boolean,
            lastSession = sessionStates[4] as WalkingSession?,
            showReviewPrompt = sessionStates[5] as Boolean,
            showSystemDialog = sessionStates[6] as Boolean,
            systemDialogTitle = sessionStates[7] as Int,
            systemDialogBody = sessionStates[8] as String,
            systemDialogEventType = sessionStates[9] as String,
            footwearId = hunterPrefs[0] as String,
            coreId = hunterPrefs[1] as String,
            exosuitId = hunterPrefs[2] as String,
            chronoId = hunterPrefs[3] as String,
            sparks = hunterPrefs[4] as Int,
            cores = hunterPrefs[5] as Int,
            shards = hunterPrefs[6] as Int,
            strength = hunterPrefs[7] as Int,
            agility = hunterPrefs[8] as Int,
            vitality = hunterPrefs[9] as Int,
            sense = hunterPrefs[10] as Int,
            availableStatPoints = hunterPrefs[11] as Int
        )
    }

    val settingsUiState: StateFlow<SettingsUiState> = combine(
        themeFlow,
        darkModeFlow,
        languageFlow,
        repository.getUserGoalSettings(),
        combine(
            _isSyncingHealthConnect,
            liveNotificationFlow,
            highContrastFlow
        ) { syncing, liveNotif, highContrast -> Triple(syncing, liveNotif, highContrast) }
    ) { theme, darkMode, lang, userGoals, (syncing, liveNotif, highContrast) ->
        SettingsUiState(
            selectedTheme = theme,
            darkModeConfig = darkMode,
            selectedLanguage = lang,
            userGoalSettings = userGoals,
            activeSensorMode = sensorManager.activeMode,
            isSensorAvailable = sensorManager.isSupported(),
            isHealthConnectSyncing = syncing,
            isLiveNotificationEnabled = liveNotif,
            isHighContrastEnabled = highContrast
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    private val uiControlsFlow = combine(
        _isPaused,
        displayPrefsFlow,
        _selectedScrubHour,
        _chartRange,
        combine(onboardingFlow, _isSyncingHealthConnect, isLoadedFlow) { o, s, l -> Triple(o, s, l) }
    ) { paused, (theme, darkMode, lang), scrubHour, range, (completedOnboarding, isSyncingHC, isLoaded) ->
        UiControls(
            isPaused = paused,
            theme = theme,
            darkMode = darkMode,
            language = lang,
            scrubHour = scrubHour,
            range = range,
            completedOnboarding = completedOnboarding,
            isSyncingHC = isSyncingHC,
            isPreferencesLoaded = isLoaded
        )
    }

    val uiState: StateFlow<StepUiState> = combine(
        repoFlow,
        uiControlsFlow,
        journeyFlow
    ) { snapshot, controls, journey ->
        val todayRecord = snapshot.todayRecord
        val streakCounter = snapshot.streakCounter
        val history = snapshot.history
        val settings = snapshot.settings
        val hourlyRecords = snapshot.hourlyRecords
        val unlocks = snapshot.unlocks

        val paused = controls.isPaused
        val theme = controls.theme
        val scrubHour = controls.scrubHour
        val range = controls.range
        val completedOnboarding = controls.completedOnboarding
        val isSyncingHC = controls.isSyncingHC

        val totalSteps = todayRecord?.totalSteps ?: 0
        val targetGoal = todayRecord?.goalSteps ?: (settings?.targetGoal ?: 10000)
        val km = todayRecord?.cumulativeKm ?: 0.0
        val allTimeKm = history.sumOf { it.cumulativeKm }

        // Build 24-hour step list
        val hourlyList = MutableList(24) { 0 }
        hourlyRecords.forEach { record ->
            if (record.hourOfDay in 0..23) {
                hourlyList[record.hourOfDay] = record.stepsCount
            }
        }

        // Build chart range data
        val chartData = ChartDataCalculator.buildChartRangeData(
            range = range,
            history = history,
            todayRecord = todayRecord,
            targetDailyGoal = targetGoal
        )

        val unlockedIds = unlocks.map { it.achievementId }.toSet()

        val todayDateStr = LocalDate.now().toString()
        val validCompletedQuests = if (journey.questDate == todayDateStr) journey.completedQuests else emptySet()
        val currentHour = LocalTime.now().hour
        val quests = QuestRegistry.generateDailyQuests(
            currentSteps = totalSteps,
            goalSteps = targetGoal,
            activeMinutes = todayRecord?.activeMinutes ?: 0,
            hourlySteps = hourlyList,
            completedIds = validCompletedQuests,
            currentHour = currentHour
        )

        // Automatically save any newly completed quests
        quests.forEach { quest ->
            if (quest.isCompleted && !validCompletedQuests.contains(quest.id)) {
                viewModelScope.launch {
                    preferencesRepository?.saveCompletedQuest(quest.id, todayDateStr)
                }
            }
        }

        // Trigger goal celebration when target achieved
        if (totalSteps >= targetGoal && targetGoal > 0 && !_hasCelebratedToday.value) {
            _hasCelebratedToday.value = true
            _showCelebrationDialog.value = true
        }

        val isProtectedToday = journey.lastFreezeDate == todayDateStr

        val hunterLevel = (14 + (totalSteps / 5000)).coerceIn(14, 50)
        if (hunterLevel > _lastKnownLevel.value && _lastKnownLevel.value > 0) {
            val levelDelta = hunterLevel - _lastKnownLevel.value
            _lastKnownLevel.value = hunterLevel
            viewModelScope.launch {
                preferencesRepository?.addFreeStatPoints(levelDelta * 3)
            }
            _systemDialogTitle.value = R.string.system_level_up_title
            _systemDialogBody.value = "Hunter Level advanced to LV.$hunterLevel! +${levelDelta * 3} Unassigned Stat Points awarded to your dossier."
            _systemDialogEventType.value = "LEVEL_UP"
            _showSystemDialog.value = true
        }

        val expToNext = 2500
        val curExp = (1850 + (totalSteps * 0.4).toInt()) % expToNext
        val equippedList = GearRegistry.getEquippedItems(
            profileFootwearId = journey.footwearId,
            profileCoreId = journey.coreId,
            profileExosuitId = journey.exosuitId,
            profileChronoId = journey.chronoId
        )
        val cp = GearRegistry.calculateCombatPower(
            level = hunterLevel,
            streakDays = streakCounter?.currentStreak ?: 7,
            equippedItems = equippedList,
            strength = journey.strength,
            agility = journey.agility,
            vitality = journey.vitality,
            sense = journey.sense
        )
        val overdrivePercent = ((totalSteps % 5000) / 5000.0 * 100).toInt()
        val hunter = HunterProfile(
            name = "Jin-Woo Sung",
            classTitle = "Shadow Monarch • Apex Speedster",
            level = hunterLevel,
            currentExp = curExp,
            expToNextLevel = expToNext,
            rankTitle = HunterProfile.computeRankTitle(hunterLevel),
            combatPower = cp,
            sparks = journey.sparks + (totalSteps / 1000),
            cores = journey.cores,
            shards = journey.shards,
            manaOverdrivePercent = overdrivePercent,
            isAuraMultiplierActive = true,
            auraMultiplier = 1.25f,
            equippedFootwearId = journey.footwearId,
            equippedCoreId = journey.coreId,
            equippedExosuitId = journey.exosuitId,
            equippedChronoId = journey.chronoId,
            strength = journey.strength,
            agility = journey.agility,
            vitality = journey.vitality,
            sense = journey.sense,
            availableStatPoints = journey.availableStatPoints
        )

        StepUiState(
            currentSteps = totalSteps,
            goalSteps = targetGoal,
            minGoal = todayRecord?.minGoal ?: (settings?.minimumGoal ?: 6000),
            stretchGoal = todayRecord?.stretchGoal ?: (settings?.stretchGoal ?: 12000),
            cumulativeKm = km,
            activeMinutes = todayRecord?.activeMinutes ?: 0,
            caloriesKcal = todayRecord?.caloriesKcal ?: 0,
            streak = streakCounter?.currentStreak ?: 0,
            bestStreak = streakCounter?.bestStreak ?: 0,
            dataSource = todayRecord?.dataSource ?: "Hardware Sensor",
            confidence = todayRecord?.confidence ?: "HIGH",
            isPaused = paused,
            isSensorAvailable = sensorManager.isSupported(),
            lastUpdated = todayRecord?.lastUpdated ?: System.currentTimeMillis(),
            nextDestination = DestinationRegistry.getNextDestination(allTimeKm),
            allHistory = history,
            userGoalSettings = settings,
            selectedTheme = theme,
            darkModeConfig = controls.darkMode,
            selectedLanguage = controls.language,
            isRestDay = todayRecord?.isRestDay ?: false,
            isRecoveryMode = settings?.recoveryModeEnabled ?: false,
            activeSensorMode = sensorManager.activeMode,
            hourlySteps = hourlyList,
            selectedScrubHour = scrubHour,
            chartRange = range,
            chartRangeData = chartData,
            unlockedAchievementIds = unlockedIds,
            hasCompletedOnboarding = completedOnboarding,
            isPreferencesLoaded = controls.isPreferencesLoaded,
            isHealthConnectSyncing = isSyncingHC,
            walkingSessions = snapshot.sessions,
            bankedStreakFreezes = journey.bankedFreezes,
            isStreakProtectedToday = isProtectedToday,
            dailyQuests = quests,
            currentCadenceSpm = journey.cadenceSpm,
            activePaceZone = journey.paceZone,
            showCelebrationDialog = journey.showCelebration,
            celebrationTitleRes = R.string.celebration_title,
            hunterProfile = hunter,
            allGearItems = GearRegistry.allGearItems,
            showWorkoutSummary = journey.showWorkoutSummary,
            lastCompletedSession = journey.lastSession,
            showReviewPromptDialog = journey.showReviewPrompt,
            showSystemDialog = journey.showSystemDialog,
            systemDialogTitleRes = journey.systemDialogTitle,
            systemDialogBody = journey.systemDialogBody,
            systemDialogEventType = journey.systemDialogEventType
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StepUiState()
    )

    init {
        // Evaluate streak integrity on startup (without fabricating fake data)
        viewModelScope.launch {
            repository.evaluateStreakContinuity()
        }
        // Synchronize paused state with persistent DataStore
        preferencesRepository?.let { prefs ->
            viewModelScope.launch {
                prefs.isTrackingPausedManually.collect { isPaused ->
                    _isPaused.value = isPaused
                    if (isPaused) {
                        sensorManager.pause()
                    } else {
                        sensorManager.resume()
                    }
                }
            }
        }
    }

    fun togglePause(context: Context? = null) {
        val next = !_isPaused.value
        _isPaused.value = next
        if (next) {
            sensorManager.pause()
            if (context != null) {
                StepCounterForegroundService.pause(context)
            }
        } else {
            sensorManager.resume()
            if (context != null) {
                StepCounterForegroundService.resume(context)
            }
        }
        preferencesRepository?.let { prefs ->
            viewModelScope.launch {
                prefs.setTrackingPausedManually(next)
            }
        }
    }

    fun setScrubHour(hour: Int?) {
        _selectedScrubHour.value = hour?.coerceIn(0, 23)
    }

    fun clearScrub() {
        _selectedScrubHour.value = null
    }

    fun setChartRange(range: HistoryChartRange) {
        _chartRange.value = range
    }

    fun updateGoal(newGoal: Int) {
        viewModelScope.launch {
            repository.updateGoal(newGoal)
        }
    }

    fun updateNotes(date: String, notes: String) {
        viewModelScope.launch {
            repository.updateNotes(date, notes)
        }
    }

    fun setTheme(theme: ThemeChoice) {
        viewModelScope.launch {
            preferencesRepository?.setSelectedTheme(theme)
                ?: run { _fallbackTheme.value = theme }
        }
    }

    fun setDarkModeConfig(config: DarkModeConfig) {
        viewModelScope.launch {
            preferencesRepository?.setDarkModeConfig(config)
                ?: run { _fallbackDarkMode.value = config }
        }
    }

    fun setSelectedLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesRepository?.setSelectedLanguage(language)
                ?: run { _fallbackLanguage.value = language }
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            preferencesRepository?.setOnboardingCompleted(completed)
        }
    }

    fun updateCarryingMode(mode: String) {
        sensorManager.setCarryingModeSensitivity(mode)
        viewModelScope.launch {
            preferencesRepository?.setCarryingMode(mode)
            val current = uiState.value.userGoalSettings ?: UserGoalSettings()
            repository.updateSettings(current.copy(carryingMode = mode))
        }
    }

    fun toggleAdaptiveGoals(enabled: Boolean) {
        viewModelScope.launch {
            val current = uiState.value.userGoalSettings ?: UserGoalSettings()
            repository.updateSettings(current.copy(adaptiveGoalsEnabled = enabled))
        }
    }

    fun toggleLockGoals(locked: Boolean) {
        viewModelScope.launch {
            val current = uiState.value.userGoalSettings ?: UserGoalSettings()
            repository.updateSettings(current.copy(lockedGoalEnabled = locked))
        }
    }

    fun toggleRecoveryMode(enabled: Boolean) {
        viewModelScope.launch {
            val current = uiState.value.userGoalSettings ?: UserGoalSettings()
            repository.updateSettings(current.copy(recoveryModeEnabled = enabled))
        }
    }

    fun toggleLiveNotification(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository?.setLiveNotificationEnabled(enabled)
        }
    }

    fun toggleHighContrast(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository?.setHighContrastEnabled(enabled)
        }
    }

    fun recalculateAdaptiveGoal() {
        viewModelScope.launch {
            repository.recalculateAdaptiveGoal()
        }
    }

    fun syncHealthConnect(context: Context) {
        viewModelScope.launch {
            _isSyncingHealthConnect.value = true
            repository.syncHealthConnectSteps(context)
            _isSyncingHealthConnect.value = false
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    fun loadDemoData() {
        viewModelScope.launch {
            repository.simulateSampleDataIfEmpty()
        }
    }

    fun updateDetailedGoals(minGoal: Int, targetGoal: Int, stretchGoal: Int) {
        viewModelScope.launch {
            val currentSettings = uiState.value.userGoalSettings ?: UserGoalSettings()
            repository.updateSettings(
                currentSettings.copy(
                    minimumGoal = minGoal,
                    targetGoal = targetGoal,
                    stretchGoal = stretchGoal
                )
            )
            repository.updateGoal(targetGoal)
        }
    }

    fun updateGoalSettings(settings: UserGoalSettings) {
        viewModelScope.launch {
            repository.updateSettings(settings)
        }
    }

    fun saveWalkingSession(session: WalkingSession) {
        viewModelScope.launch {
            repository.saveWalkingSession(session)
            _lastCompletedSession.value = session
            _showWorkoutSummary.value = true
        }
    }

    fun equipGear(slotType: String, gearId: String) {
        viewModelScope.launch {
            preferencesRepository?.equipGear(slotType, gearId)
        }
    }

    fun tuneGear(gearId: String) {
        viewModelScope.launch {
            preferencesRepository?.addLootRewards(sparksDelta = -1, coresDelta = 0, shardsDelta = 5)
        }
    }

    fun claimRaidRewards(sparksDelta: Int = 5, coresDelta: Int = 1, shardsDelta: Int = 50) {
        viewModelScope.launch {
            preferencesRepository?.addLootRewards(sparksDelta, coresDelta, shardsDelta)
            _showWorkoutSummary.value = false
        }
    }

    fun dismissWorkoutSummary() {
        _showWorkoutSummary.value = false
    }

    fun deleteWalkingSession(id: Long) {
        viewModelScope.launch {
            repository.deleteWalkingSession(id)
        }
    }

    fun shareExportData(context: Context, isCsv: Boolean) {
        viewModelScope.launch {
            val exportString = if (isCsv) repository.exportHistoryAsCsv() else repository.exportHistoryAsJson()
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = if (isCsv) "text/csv" else "application/json"
                putExtra(Intent.EXTRA_SUBJECT, "TrueStep Export")
                putExtra(Intent.EXTRA_TEXT, exportString)
            }
            val chooser = Intent.createChooser(shareIntent, "Share TrueStep Data").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }
    }

    fun dismissCelebration() {
        _showCelebrationDialog.value = false
        checkAndTriggerReviewPrompt()
    }

    fun triggerReviewPrompt() {
        _showReviewPromptDialog.value = true
    }

    fun dismissReviewPrompt(neverAskAgain: Boolean = false) {
        _showReviewPromptDialog.value = false
        viewModelScope.launch {
            if (neverAskAgain) {
                preferencesRepository?.setHasRatedApp(true)
            } else {
                preferencesRepository?.recordReviewPromptDismissed()
            }
        }
    }

    fun onRateOnPlayStore(context: Context) {
        _showReviewPromptDialog.value = false
        viewModelScope.launch {
            preferencesRepository?.setHasRatedApp(true)
        }
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(marketIntent)
        } catch (e: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    fun onSendConstructiveFeedback(context: Context) {
        _showReviewPromptDialog.value = false
        viewModelScope.launch {
            preferencesRepository?.recordReviewPromptDismissed()
        }
        try {
            val feedbackIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_SUBJECT, "TrueStep Feedback")
                putExtra(Intent.EXTRA_TEXT, "Hello TrueStep team,\n\nHere is my feedback on the app:\n")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(feedbackIntent)
        } catch (e: Exception) {
            // Ignore if no email client
        }
    }

    fun checkAndTriggerReviewPrompt() {
        viewModelScope.launch {
            val hasRated = preferencesRepository?.hasRatedApp?.firstOrNull() ?: false
            if (hasRated) return@launch
            val lastTimestamp = preferencesRepository?.lastReviewPromptTimestamp?.firstOrNull() ?: 0L
            val dismissCount = preferencesRepository?.reviewPromptDismissCount?.firstOrNull() ?: 0
            val sevenDaysMillis = 7 * 24 * 60 * 60 * 1000L
            val now = System.currentTimeMillis()
            if (dismissCount < 3 && (now - lastTimestamp > sevenDaysMillis)) {
                _showReviewPromptDialog.value = true
            }
        }
    }

    fun triggerCelebration() {
        _showCelebrationDialog.value = true
    }

    fun updateCadence(spm: Int) {
        _currentCadenceSpm.value = spm
        _activePaceZone.value = PaceZone.fromSpm(spm)
    }

    fun useStreakFreeze() {
        viewModelScope.launch {
            val todayStr = LocalDate.now().toString()
            preferencesRepository?.recordFreezeUsed(todayStr)
        }
    }

    fun completeQuest(questId: String) {
        viewModelScope.launch {
            val todayStr = LocalDate.now().toString()
            preferencesRepository?.saveCompletedQuest(questId, todayStr)
        }
    }

    fun shareProgress(context: Context) {
        val current = uiState.value
        val text = context.getString(
            R.string.share_card_text_format,
            MetricFormatter.formatSteps(current.currentSteps),
            current.cumulativeKm,
            current.streak
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_card_title))
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(shareIntent, context.getString(R.string.share_sheet_chooser_title)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun allocateStatPoint(statType: String) {
        viewModelScope.launch {
            preferencesRepository?.allocateStatPoint(statType)
        }
    }

    fun dismissSystemNotification() {
        _showSystemDialog.value = false
    }

    fun openQuestDetail(quest: DailyQuest) {
        _systemDialogTitle.value = quest.titleRes
        val pct = (quest.progressPercent * 100).toInt()
        _systemDialogBody.value = "${quest.iconEmoji} OBJECTIVE STATUS: ${MetricFormatter.formatSteps(quest.currentValue)} / ${MetricFormatter.formatSteps(quest.targetValue)} ($pct%)\n" +
            "REWARD CACHE: +${quest.rewardXp} EXP • +10 Sparks 💎\n\n" +
            "SYSTEM DIRECTIVE: Complete target quota to satisfy daily physical threshold and avoid Penalty Protocol."
        _systemDialogEventType.value = "DAILY_QUEST"
        _showSystemDialog.value = true
    }

    fun summonDailyQuestSystemPopup() {
        val target = uiState.value.dailyQuests.firstOrNull { !it.isCompleted } ?: uiState.value.dailyQuests.firstOrNull()
        if (target != null) {
            openQuestDetail(target)
        } else {
            triggerSystemNotification(
                titleRes = R.string.system_quest_arrival_title,
                body = "All current system directives cleared! Standby for tomorrow's dimensional reset.",
                eventType = "DAILY_QUEST"
            )
        }
    }

    fun triggerSystemNotification(titleRes: Int, body: String, eventType: String = "INFO") {
        _systemDialogTitle.value = titleRes
        _systemDialogBody.value = body
        _systemDialogEventType.value = eventType
        _showSystemDialog.value = true
    }
}
