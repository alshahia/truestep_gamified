package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.DarkModeConfig
import com.example.ui.theme.ThemeChoice
import com.example.util.AppLanguage
import com.example.util.LocaleHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val SELECTED_THEME = stringPreferencesKey("selected_theme")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val CARRYING_MODE = stringPreferencesKey("carrying_mode")
        val DARK_MODE_CONFIG = stringPreferencesKey("dark_mode_config")
        val SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        val LIVE_NOTIFICATION_ENABLED = booleanPreferencesKey("live_notification_enabled")
        val HIGH_CONTRAST_ENABLED = booleanPreferencesKey("high_contrast_enabled")
        val BANKED_STREAK_FREEZES = intPreferencesKey("banked_streak_freezes")
        val LAST_FREEZE_USED_DATE = stringPreferencesKey("last_freeze_used_date")
        val COMPLETED_QUEST_IDS = stringSetPreferencesKey("completed_quest_ids")
        val QUEST_DATE = stringPreferencesKey("quest_date")
        val EQUIPPED_FOOTWEAR = stringPreferencesKey("equipped_footwear")
        val EQUIPPED_CORE = stringPreferencesKey("equipped_core")
        val EQUIPPED_EXOSUIT = stringPreferencesKey("equipped_exosuit")
        val EQUIPPED_CHRONO = stringPreferencesKey("equipped_chrono")
        val BANKED_SPARKS = intPreferencesKey("banked_sparks")
        val BANKED_CORES = intPreferencesKey("banked_cores")
        val BANKED_SHARDS = intPreferencesKey("banked_shards")
        val HUNTER_STRENGTH = intPreferencesKey("hunter_strength")
        val HUNTER_AGILITY = intPreferencesKey("hunter_agility")
        val HUNTER_VITALITY = intPreferencesKey("hunter_vitality")
        val HUNTER_SENSE = intPreferencesKey("hunter_sense")
        val AVAILABLE_STAT_POINTS = intPreferencesKey("available_stat_points")
        val HAS_RATED_APP = booleanPreferencesKey("has_rated_app")
        val LAST_REVIEW_PROMPT_TIMESTAMP = longPreferencesKey("last_review_prompt_timestamp")
        val REVIEW_PROMPT_DISMISS_COUNT = intPreferencesKey("review_prompt_dismiss_count")
        val IS_TRACKING_PAUSED_MANUALLY = booleanPreferencesKey("is_tracking_paused_manually")
    }

    val selectedTheme: Flow<ThemeChoice> = context.userDataStore.data.map { preferences ->
        val themeName = preferences[PreferencesKeys.SELECTED_THEME]
        if (themeName != null) {
            try {
                ThemeChoice.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                ThemeChoice.CYBERNETIC_STRIDE
            }
        } else {
            ThemeChoice.CYBERNETIC_STRIDE
        }
    }

    val darkModeConfig: Flow<DarkModeConfig> = context.userDataStore.data.map { preferences ->
        val modeKey = preferences[PreferencesKeys.DARK_MODE_CONFIG]
        DarkModeConfig.fromStorageKey(modeKey)
    }

    val selectedLanguage: Flow<AppLanguage> = context.userDataStore.data.map { preferences ->
        val langCode = preferences[PreferencesKeys.SELECTED_LANGUAGE]
        if (langCode != null) {
            AppLanguage.fromCode(langCode)
        } else {
            LocaleHelper.getPersistedLanguage(context)
        }
    }

    val hasCompletedOnboarding: Flow<Boolean> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
    }

    val isPreferencesLoaded: Flow<Boolean> = context.userDataStore.data.map { true }

    val carryingMode: Flow<String> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.CARRYING_MODE] ?: "Pocket"
    }

    val isLiveNotificationEnabled: Flow<Boolean> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.LIVE_NOTIFICATION_ENABLED] ?: true
    }

    val isHighContrastEnabled: Flow<Boolean> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.HIGH_CONTRAST_ENABLED] ?: false
    }

    val bankedStreakFreezes: Flow<Int> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.BANKED_STREAK_FREEZES] ?: 1 // Start with 1 complimentary safety freeze
    }

    val lastFreezeUsedDate: Flow<String> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_FREEZE_USED_DATE] ?: ""
    }

    val completedQuestIds: Flow<Set<String>> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.COMPLETED_QUEST_IDS] ?: emptySet()
    }

    val questDate: Flow<String> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.QUEST_DATE] ?: ""
    }

    val equippedFootwear: Flow<String> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.EQUIPPED_FOOTWEAR] ?: "gear_neon_phantom"
    }

    val equippedCore: Flow<String> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.EQUIPPED_CORE] ?: "gear_zephyr_engine"
    }

    val equippedExosuit: Flow<String> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.EQUIPPED_EXOSUIT] ?: "gear_nanoweave_vest"
    }

    val equippedChrono: Flow<String> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.EQUIPPED_CHRONO] ?: "gear_chrono_watch"
    }

    val bankedSparks: Flow<Int> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.BANKED_SPARKS] ?: 48
    }

    val bankedCores: Flow<Int> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.BANKED_CORES] ?: 6
    }

    val bankedShards: Flow<Int> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.BANKED_SHARDS] ?: 1420
    }

    val hunterStrength: Flow<Int> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.HUNTER_STRENGTH] ?: 45
    }

    val hunterAgility: Flow<Int> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.HUNTER_AGILITY] ?: 62
    }

    val hunterVitality: Flow<Int> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.HUNTER_VITALITY] ?: 38
    }

    val hunterSense: Flow<Int> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.HUNTER_SENSE] ?: 50
    }

    val availableStatPoints: Flow<Int> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.AVAILABLE_STAT_POINTS] ?: 3
    }

    suspend fun allocateStatPoint(statType: String): Boolean {
        var success = false
        context.userDataStore.edit { preferences ->
            val points = preferences[PreferencesKeys.AVAILABLE_STAT_POINTS] ?: 3
            if (points > 0) {
                preferences[PreferencesKeys.AVAILABLE_STAT_POINTS] = points - 1
                when (statType.uppercase()) {
                    "STR", "STRENGTH" -> {
                        val cur = preferences[PreferencesKeys.HUNTER_STRENGTH] ?: 45
                        preferences[PreferencesKeys.HUNTER_STRENGTH] = cur + 1
                    }
                    "AGI", "AGILITY" -> {
                        val cur = preferences[PreferencesKeys.HUNTER_AGILITY] ?: 62
                        preferences[PreferencesKeys.HUNTER_AGILITY] = cur + 1
                    }
                    "VIT", "VITALITY" -> {
                        val cur = preferences[PreferencesKeys.HUNTER_VITALITY] ?: 38
                        preferences[PreferencesKeys.HUNTER_VITALITY] = cur + 1
                    }
                    "SEN", "SENSE" -> {
                        val cur = preferences[PreferencesKeys.HUNTER_SENSE] ?: 50
                        preferences[PreferencesKeys.HUNTER_SENSE] = cur + 1
                    }
                }
                success = true
            }
        }
        return success
    }

    suspend fun addFreeStatPoints(count: Int) {
        context.userDataStore.edit { preferences ->
            val points = preferences[PreferencesKeys.AVAILABLE_STAT_POINTS] ?: 3
            preferences[PreferencesKeys.AVAILABLE_STAT_POINTS] = points + count
        }
    }

    suspend fun equipGear(slotType: String, gearId: String) {
        context.userDataStore.edit { preferences ->
            when (slotType) {
                "FOOTWEAR" -> preferences[PreferencesKeys.EQUIPPED_FOOTWEAR] = gearId
                "CORE" -> preferences[PreferencesKeys.EQUIPPED_CORE] = gearId
                "EXOSUIT" -> preferences[PreferencesKeys.EQUIPPED_EXOSUIT] = gearId
                "CHRONO" -> preferences[PreferencesKeys.EQUIPPED_CHRONO] = gearId
            }
        }
    }

    suspend fun addLootRewards(sparksDelta: Int, coresDelta: Int, shardsDelta: Int) {
        context.userDataStore.edit { preferences ->
            val curSparks = preferences[PreferencesKeys.BANKED_SPARKS] ?: 48
            val curCores = preferences[PreferencesKeys.BANKED_CORES] ?: 6
            val curShards = preferences[PreferencesKeys.BANKED_SHARDS] ?: 1420
            preferences[PreferencesKeys.BANKED_SPARKS] = curSparks + sparksDelta
            preferences[PreferencesKeys.BANKED_CORES] = curCores + coresDelta
            preferences[PreferencesKeys.BANKED_SHARDS] = curShards + shardsDelta
        }
    }

    suspend fun setSelectedTheme(theme: ThemeChoice) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_THEME] = theme.name
        }
    }

    suspend fun setDarkModeConfig(config: DarkModeConfig) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE_CONFIG] = config.storageKey
        }
    }

    suspend fun setSelectedLanguage(language: AppLanguage) {
        LocaleHelper.persistLanguage(context, language)
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_LANGUAGE] = language.code
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = completed
        }
    }

    suspend fun setCarryingMode(mode: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.CARRYING_MODE] = mode
        }
    }

    suspend fun setLiveNotificationEnabled(enabled: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.LIVE_NOTIFICATION_ENABLED] = enabled
        }
    }

    suspend fun setHighContrastEnabled(enabled: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.HIGH_CONTRAST_ENABLED] = enabled
        }
    }

    suspend fun setBankedStreakFreezes(count: Int) {
        val safeCount = count.coerceIn(0, 2)
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.BANKED_STREAK_FREEZES] = safeCount
        }
    }

    suspend fun recordFreezeUsed(date: String) {
        context.userDataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.BANKED_STREAK_FREEZES] ?: 1
            preferences[PreferencesKeys.BANKED_STREAK_FREEZES] = (current - 1).coerceAtLeast(0)
            preferences[PreferencesKeys.LAST_FREEZE_USED_DATE] = date
        }
    }

    suspend fun saveCompletedQuest(questId: String, todayDate: String) {
        context.userDataStore.edit { preferences ->
            val storedDate = preferences[PreferencesKeys.QUEST_DATE] ?: ""
            val currentSet = if (storedDate == todayDate) {
                preferences[PreferencesKeys.COMPLETED_QUEST_IDS] ?: emptySet()
            } else {
                emptySet()
            }
            preferences[PreferencesKeys.QUEST_DATE] = todayDate
            preferences[PreferencesKeys.COMPLETED_QUEST_IDS] = currentSet + questId
        }
    }

    val hasRatedApp: Flow<Boolean> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAS_RATED_APP] ?: false
    }

    val lastReviewPromptTimestamp: Flow<Long> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_REVIEW_PROMPT_TIMESTAMP] ?: 0L
    }

    val reviewPromptDismissCount: Flow<Int> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.REVIEW_PROMPT_DISMISS_COUNT] ?: 0
    }

    suspend fun setHasRatedApp(rated: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_RATED_APP] = rated
        }
    }

    suspend fun recordReviewPromptDismissed() {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_REVIEW_PROMPT_TIMESTAMP] = System.currentTimeMillis()
            val current = preferences[PreferencesKeys.REVIEW_PROMPT_DISMISS_COUNT] ?: 0
            preferences[PreferencesKeys.REVIEW_PROMPT_DISMISS_COUNT] = current + 1
        }
    }

    val isTrackingPausedManually: Flow<Boolean> = context.userDataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_TRACKING_PAUSED_MANUALLY] ?: false
    }

    suspend fun setTrackingPausedManually(paused: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_TRACKING_PAUSED_MANUALLY] = paused
        }
    }
}
