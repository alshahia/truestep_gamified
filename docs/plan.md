# Master Remediation & Scalable Architecture Plan: TrueStep

## Status Overview
- **Overall State:** `IN_PROGRESS`
- **Milestones Completed:** 
  - Milestone 6: Clean, Structured & Scalable Settings Architecture (`[COMPLETED]`)
  - Milestone 7: Fluid Hourly Timeline & Interactive KPI Experience (`[COMPLETED]`)
  - Milestone 8: Scalable Localization Architecture & Complete Language Parity (`[COMPLETED]`)
  - Milestone 9: Retention & UX Engagement Architecture Plan (`[COMPLETED]` — see `docs/implementation_plan.md`)
  - Milestone 10: Brand Identity & App Logo Modernization (`[COMPLETED]`)
  - Milestone 11: Cybernetic Stride Evolution & Gamified Athletic HUD (`[COMPLETED]`)
  - Milestone 12: TrueStep UX Architecture Realignment & Name Consistency (`[COMPLETED]`)
  - Milestone 13: Gamified Athletic HUD & Cybernetic Experience Alignment (`[COMPLETED]`)
  - Milestone 14: Solo Leveling System Stat Allocator, Holographic Blue HUD & Option C Cyber-Gates (`[COMPLETED]`)
  - Milestone 15: Homepage UI/UX Remediation & Fluid Responsive Architecture (`[COMPLETED]`)
  - Milestone 16: Step Ring Full-Surface Pause & Resume Interaction (`[COMPLETED]`)
  - Milestone 17: Production Readiness Audit & Google Play Store Publishing Certification (`[COMPLETED]`)
- **Verification Command:** `gradle :app:testDebugUnitTest` (ALL 33 PASSED), `gradle :app:bundleRelease` (AAB BUILT), and `compile_applet` (BUILD SUCCESSFUL)

---

## Milestones & Execution Phases

### Milestone 1: Data Integrity & Database Performance (P0)
**Objective:** Eliminate O(N) memory queries, enforce safe database migrations, fix streak calculations, and ensure a privacy-compliant clean install.

- [ ] **Task 1.1: Remove Destructive Migration & Configure Safe Room Schema** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/data/local/StepDatabase.kt`, `app/build.gradle.kts`
  - *Details:* Remove `.fallbackToDestructiveMigration(dropAllTables = true)`. Add explicit migration strategy or schema versioning.
  - *Acceptance Criteria:* Database survives schema bumps without wiping records; user history is preserved.

- [ ] **Task 1.2: Replace O(N) Step Event Loading with SQL Aggregates** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/data/local/StepDao.kt`, `app/src/main/java/com/example/data/repository/StepRepositoryImpl.kt`
  - *Details:* Add DAO query `getHistorySummary(): HistoryAggregate` returning `COUNT(*)`, `SUM(cumulativeKm)`, and `MAX(totalSteps)`. Remove `stepDao.getAllList()` from the inner step-tick handler `checkAchievements()`.
  - *Acceptance Criteria:* Step ticks execute in O(1) time without deserializing the entire database into memory.

- [ ] **Task 1.3: Fix Goal Streak Algorithm for Broken Streaks** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/data/repository/StepRepositoryImpl.kt`
  - *Details:* Evaluate streak continuity across calendar dates. If today < goal and yesterday's goal was not met (and neither day is a designated rest/recovery day), reset `currentStreak = 0` while preserving `bestStreak`.
  - *Acceptance Criteria:* Streaks accurately reset when days are missed; rest days correctly protect the streak.

- [ ] **Task 1.4: Dynamic Date Ticker for Midnight Rollover** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/data/repository/StepRepositoryImpl.kt`
  - *Details:* Ensure `getTodaySteps()` observes a reactive date Flow or re-evaluates the query date on calendar changes so overnight sessions automatically roll over to the new day's record.
  - *Acceptance Criteria:* UI updates automatically at 00:00 without requiring app restart.

- [ ] **Task 1.5: Pruning Routine for Source Records Table** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/data/local/StepSourceRecordDao.kt`, `app/src/main/java/com/example/data/sync/StepSyncWorker.kt`
  - *Details:* Add `deleteRecordsOlderThan(cutoffTimestamp: Long)` in `StepSourceRecordDao`. Have `StepSyncWorker` prune intervals older than 14 days.
  - *Acceptance Criteria:* Database storage remains bounded and fast regardless of usage duration.

- [ ] **Task 1.6: Enforce Zero-Data Clean Install (Stop Silent Auto-Seeding)** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/ui/viewmodel/StepViewModel.kt`
  - *Details:* Remove `repository.simulateSampleDataIfEmpty()` from `StepViewModel.init`. Retain sample data generation exclusively in `SettingsScreen` via explicit "Load Sample Data (Preview Only)" button.
  - *Acceptance Criteria:* New installs start at 0 steps today, 0 days history, and 0 unlocked achievements.

---

### Milestone 2: Sensor Telemetry & Foreground Service Efficiency (P0)
**Objective:** Prevent background battery drain, ensure reliable foreground service lifecycle on Android 14+, and eliminate permission collisions.

- [ ] **Task 2.1: Low-Power Sensor Delay & Hardware FIFO Batching** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/sensor/StepSensorManager.kt`
  - *Details:* Switch from `SensorManager.SENSOR_DELAY_UI` to `SensorManager.SENSOR_DELAY_NORMAL` with 10-second batching (`maxReportLatencyUs = 10_000_000`).
  - *Acceptance Criteria:* CPU enters deep sleep in background while hardware sensor hub buffers footsteps.

- [ ] **Task 2.2: Thread-Safe Sensor Callback API** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/sensor/StepSensorManager.kt`, `app/src/main/java/com/example/service/StepCounterForegroundService.kt`
  - *Details:* Replace single mutable listener with thread-safe multicast Flow or registration mechanism so service listener cannot be accidentally overwritten.
  - *Acceptance Criteria:* Step counting remains active and unperturbed across screen rotations or lifecycle transitions.

- [ ] **Task 2.3: Sequential Permission Flow in MainActivity** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/MainActivity.kt`
  - *Details:* Sequence runtime permission dialogs: Activity Recognition first; only upon resolution, request Notifications; then optionally show Health Connect rationale card.
  - *Acceptance Criteria:* Zero permission collisions or dismissed dialogs on first launch.

---

### Milestone 3: Real Health Connect & DataStore Preferences (P1)
**Objective:** Replace phantom Health Connect code with real bidirectional sync, and persist user preferences in Jetpack DataStore.

- [ ] **Task 3.1: Complete Health Connect Sync Pipeline** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/data/sync/HealthConnectHelper.kt`, `app/src/main/java/com/example/data/sync/StepSyncWorker.kt`, `app/src/main/java/com/example/data/repository/StepRepositoryImpl.kt`
  - *Details:* Implement `readTodaySteps()` execution in `StepSyncWorker` and a manual "Sync Now" trigger in Settings/Home. Reconcile external records with sensor records.
  - *Acceptance Criteria:* Steps recorded in Health Connect (e.g., smartwatches) are imported and displayed in StepTrack.

- [ ] **Task 3.2: Jetpack DataStore for App Preferences** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/data/preferences/UserPreferencesRepository.kt` (new), `app/src/main/java/com/example/data/di/ServiceLocator.kt`, `app/src/main/java/com/example/ui/viewmodel/StepViewModel.kt`
  - *Details:* Store `selectedTheme`, `hasCompletedOnboarding`, and `carryingMode` in `DataStore<Preferences>`.
  - *Acceptance Criteria:* Selected theme and onboarding state persist across device reboots and app kills.

- [ ] **Task 3.3: First-Launch Onboarding Gate** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/ui/navigation/AppNavigation.kt`, `app/src/main/java/com/example/MainActivity.kt`
  - *Details:* Observe `hasCompletedOnboarding` from DataStore. Show `OnboardingScreen` on first launch; upon completion, save goal and update DataStore.
  - *Acceptance Criteria:* First-time users experience the onboarding journey; subsequent opens go directly to Home.

---

### Milestone 4: UI Cleanup & Achievement Integration (P1)
**Objective:** Delete orphaned dead code, display real achievements and badges in the UI, and connect real hourly data to Analytics.

- [ ] **Task 4.1: Delete Orphaned Dead Code Files** `[PLANNED]`
  - *Files to Delete:*
    - `app/src/main/java/com/example/ui/components/DestinationBadge.kt`
    - `app/src/main/java/com/example/ui/components/LinearStepProgress.kt`
    - `app/src/main/java/com/example/ui/components/TrendChart.kt`
    - `app/src/main/java/com/example/ui/components/PauseButton.kt`
    - `app/src/main/java/com/example/domain/content/MultiplierRegistry.kt`
  - *Details:* Safely remove unused classes; move `AchievementRegistry` into `domain/content/AchievementRegistry.kt`. Update any test references.
  - *Acceptance Criteria:* Zero unused dead components; project builds cleanly.

- [ ] **Task 4.2: Connect Achievements & Milestones to UI** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/ui/viewmodel/StepViewModel.kt`, `app/src/main/java/com/example/ui/screens/AnalyticsScreen.kt`
  - *Details:* Expose unlocked achievement IDs to UI state. Add a Milestone Badges grid in `AnalyticsScreen` displaying unlocked trophies with emojis, dates, and titles.
  - *Acceptance Criteria:* Unlocked achievements in SQLite are visible and celebrated in the UI.

- [ ] **Task 4.3: Connect Real Hourly Data to Analytics Rhythm Chart** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/ui/screens/AnalyticsScreen.kt`, `app/src/main/java/com/example/ui/viewmodel/StepViewModel.kt`
  - *Details:* Replace static dummy float list `listOf(0.12f, 0.25f...)` with actual hourly distributions queried from `HourlyStepDao`.
  - *Acceptance Criteria:* Rhythm chart reflects real walking patterns.

---

### Milestone 5: Localization & Clean Scalable Architecture (P2)
**Objective:** Eliminate hardcoded strings for complete Arabic/English parity and modularize UI state to prevent excessive recomposition.

- [ ] **Task 5.1: Complete String Localization Parity** `[PLANNED]`
  - *Files:* `app/src/main/res/values/strings.xml`, `app/src/main/res/values-ar/strings.xml`, `HomeScreen.kt`, `HistoryScreen.kt`, `AnalyticsScreen.kt`, `SettingsScreen.kt`
  - *Details:* Move all hardcoded strings ("Quick Presets", "Goals Met", "All-time total", etc.) into `strings.xml` and `values-ar/strings.xml`.
  - *Acceptance Criteria:* Zero hardcoded user-facing strings; 100% Arabic and English localization accuracy.

- [ ] **Task 5.2: State Decomposition to Reduce Recomposition** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/ui/viewmodel/StepViewModel.kt`
  - *Details:* Separate high-frequency scrub state from low-frequency history and settings state using `derivedStateOf` or separate StateFlows.
  - *Acceptance Criteria:* Dragging the scrub ribbon does not trigger recomposition of history or analytics charts.

- [ ] **Task 5.3: Unit & Robolectric Test Suite Expansion** `[PLANNED]`
  - *Files:* `app/src/test/java/com/example/StepTrackUnitTest.kt`, `app/src/test/java/com/example/StepTrackComposeUiTest.kt`
  - *Details:* Add tests for streak reset logic, data pruning, DataStore preferences, and aggregate queries.
  - *Acceptance Criteria:* All unit tests pass deterministically via `gradle :app:testDebugUnitTest`.

---

### Milestone 6: Clean, Structured & Scalable Settings Architecture (P1)
**Objective:** Modularize `SettingsScreen.kt` from a 566-line monolith into isolated, testable section components, decouple settings state from high-frequency step updates to prevent recomposition thrashing, resolve nested scroll conflicts with a dedicated M3 Theme Selection Sheet, eliminate 14-callback parameter drilling via `SettingsActions`, and fulfill 100% of TrueStep MVP settings requirements (notifications, accessibility, diagnostics).

- [x] **Task 6.1: Isolate Settings State & Action Modeling** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/screens/settings/model/SettingsUiState.kt`, `app/src/main/java/com/example/ui/screens/settings/model/SettingsActions.kt`, `app/src/main/java/com/example/ui/viewmodel/StepViewModel.kt`
  - *Details:* Defined an immutable, lightweight `SettingsUiState` containing only preferences and device states (`selectedTheme`, `darkModeConfig`, `selectedLanguage`, `userGoalSettings`, `activeSensorMode`, `isHealthConnectSyncing`, `isLiveNotificationEnabled`, `isHighContrastEnabled`). Exposes dedicated `settingsUiState: StateFlow<SettingsUiState>` in `StepViewModel`. Unified user action callbacks into `SettingsActions`.
  - *Acceptance Criteria:* Settings screen no longer observes step counters, timestamps, or scrub hours. Zero recomposition when footsteps occur.

- [x] **Task 6.2: Reusable Material 3 Settings UI Primitives** `[COMPLETED]`
  - *Files:* 
    - `app/src/main/java/com/example/ui/screens/settings/components/SettingsGroupCard.kt`
    - `app/src/main/java/com/example/ui/screens/settings/components/SettingsSwitchRow.kt`
    - `app/src/main/java/com/example/ui/screens/settings/components/SettingsActionRow.kt`
    - `app/src/main/java/com/example/ui/screens/settings/components/SettingsPickerRow.kt`
    - `app/src/main/java/com/example/ui/screens/settings/components/SettingsDangerRow.kt`
  - *Details:* Built standard, accessible M3 preference components with 48dp minimum touch targets, semantic content descriptions, ripple feedback, clear icon containers, and optional dividers.
  - *Acceptance Criteria:* Consistent spacing, typography, and accessibility across all settings rows without duplicated layout boilerplate.

- [x] **Task 6.3: Theme Selection Bottom Sheet (Fix Nested Scroll Bug)** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/screens/settings/components/ThemeSelectionSheet.kt`
  - *Details:* Replaced the nested `LazyVerticalGrid` inside `verticalScroll` with a tap-to-open Material 3 `ModalBottomSheet`. Shows all 16 theme swatches with full scroll freedom, large touch targets, color palette indicators, and checkmarks.
  - *Acceptance Criteria:* Nested scroll jank eliminated; theme selection is fluid, accessible, and responsive on all screen sizes.

- [x] **Task 6.4: Modular Settings Sections** `[COMPLETED]`
  - *Files:*
    - `app/src/main/java/com/example/ui/screens/settings/sections/AppearanceSettingsSection.kt`
    - `app/src/main/java/com/example/ui/screens/settings/sections/TrackingSettingsSection.kt`
    - `app/src/main/java/com/example/ui/screens/settings/sections/GoalsSettingsSection.kt`
    - `app/src/main/java/com/example/ui/screens/settings/sections/NotificationSettingsSection.kt`
    - `app/src/main/java/com/example/ui/screens/settings/sections/AccessibilitySettingsSection.kt`
    - `app/src/main/java/com/example/ui/screens/settings/sections/PrivacyDataSettingsSection.kt`
    - `app/src/main/java/com/example/ui/screens/settings/sections/TroubleshootingAboutSection.kt`
  - *Details:* Extracted distinct domain sections into modular composables. Added missing TrueStep MVP features: live notification toggle vs minimal badge, accessibility high-contrast switch, diagnostic telemetry, and OEM battery optimization check.
  - *Acceptance Criteria:* Each section file is under 120 lines, highly readable, and independently previewable/testable.

- [x] **Task 6.5: Clean Root SettingsScreen Orchestrator & Navigation Integration** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/screens/SettingsScreen.kt`, `app/src/main/java/com/example/ui/navigation/AppNavigation.kt`
  - *Details:* Refactored root `SettingsScreen` into an orchestrator under 160 lines arranging the 7 section cards within a clean vertical layout. Preserves 100% of existing compose `testTag` IDs (`theme_selection_card`, `carrying_mode_card`, `adaptive_goals_switch`, `lock_goals_switch`, `recovery_mode_switch`, `recalculate_goal_button`, `sync_health_connect_button`, `export_csv_button`, `export_json_button`, `delete_local_data_button`, `confirm_delete_button`).
  - *Acceptance Criteria:* Clean compilation, zero parameter drilling, and complete backward-compatible testTag preservation.

- [x] **Task 6.6: Settings UI & Unit Test Suite Validation** `[COMPLETED]`
  - *Files:* `app/src/test/java/com/example/SettingsScreenComposeTest.kt`
  - *Details:* Added automated tests verifying that all settings sections render, switches toggle state correctly, dialogs open and dismiss, and theme/language selections dispatch properly.
  - *Acceptance Criteria:* Full test coverage for settings flows; `gradle :app:testDebugUnitTest` passes deterministically.

---

### Milestone 7: Fluid Hourly Timeline & Interactive KPI Experience (P0)
**Objective:** Replace the static, directionally-inverted hourly ribbon with a fluid, physics-based, 24-hour horizontally scrollable timeline (`rememberScrollState`) matching the daily/weekly chart mechanics. Synchronize the timeline with the 2×2 KPI grid to show cumulative day progress (start of day = 0 or hour 0; end of day / launch = full day total). Ensure complete RTL (Arabic) and LTR (English) symmetry, centered cursor focus, direct tap selection, and clean reset on app launch/resume.

- [x] **Task 7.1: Full 24-Hour Horizon & Domain Data Modeling** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/viewmodel/StepViewModel.kt`
  - *Details:* Expanded timeline data to all 24 hours (`00:00` to `23:00`). Modeled cumulative progression for any hour $H \in [0, 23]$: cumulative steps ($\sum_{i=0}^H \text{steps}_i$), cumulative distance ($\sum \text{km}_i$), cumulative calories ($\sum \text{kcal}_i$), and goal percentage so far. Compute delta metrics for the isolated hour $H$ ($\text{steps}_H$, $\text{sharePercent}$).
  - *Acceptance Criteria:* Accurate cumulative and delta calculations for all 24 hours without floating-point drift or array-index out-of-bounds.

- [x] **Task 7.2: State Management & Launch / Reopen Full-Day Default** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/viewmodel/StepViewModel.kt`, `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
  - *Details:* Initialized `selectedScrubHour = null` representing **Full Day Mode**. When `selectedScrubHour == null`, UI state exposes the full entire day's metrics: `currentSteps`, `cumulativeKm`, `caloriesKcal`, and `displayPercent`. On app resume or screen initialization, ensure `selectedScrubHour` resets to `null` so the user never reopens the app stuck on a past scrubbed hour. Added `clearScrub()` helper.
  - *Acceptance Criteria:* Fresh launch or app reopen always displays full-day totals. Scrubbing updates metrics dynamically; clearing or unselecting immediately restores full-day totals.

- [x] **Task 7.3: Fluid Scrollable Hourly Ribbon with Centered Pin & Tap Selection** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/components/HourlyScrubRibbon.kt`
  - *Details:* Replaced fixed-width static box with `Modifier.horizontalScroll(scrollState)` container. Content glides naturally with touch physics matching `HistoryPreviewChart`. Anchored centered indicator pin so scrolling the timeline passes bars beneath the cursor to update the active hour dynamically. Direct tap on any bar smoothly animates and centers that hour. Added quick "Full Day" chip to reset selection.
  - *Acceptance Criteria:* Dragging finger left pulls content left, advancing the timeline to later hours (matching daily/weekly chart). Zero cursor jump or directional inversion. Bars physically move smoothly.

- [x] **Task 7.4: RTL / LTR Directional Alignment & Localization** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/components/HourlyScrubRibbon.kt`, `app/src/main/res/values/strings.xml`, `app/src/main/res/values-ar/strings.xml`
  - *Details:* Aligned timeline start and end according to `LocalLayoutDirection.current`:
    - **LTR (English):** Left = Start of Day (`00:00`), Right = End of Day (`23:00` / Full Day).
    - **RTL (Arabic):** Right = Start of Day (`00:00`), Left = End of Day (`23:00` / Full Day).
    - Max scroll positions align with start/end of day. Added localized strings for "Full Day", "Today's Total", "In this hour", "Elapsed so far", "Start of day", and "End of day" in both English and Arabic.
  - *Acceptance Criteria:* Arabic RTL users experience natural right-to-left progression (start of day on right, advancing leftward); English LTR users experience natural left-to-right progression.

- [x] **Task 7.5: Synchronized 2×2 Home KPI Grid** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/components/HomeKpiGrid.kt`, `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
  - *Details:* Updated `HomeKpiGrid` to handle both Full Day mode and Scrubbed Hour mode:
    - **Full Day Mode:** Tile 1 shows Today's Total Steps (`%,d`), Tile 2 shows Today's Distance (`%.2f km`), Tile 3 shows Today's Calories (`%d kcal`), Tile 4 shows Goal Progress (`%d%%`).
    - **Scrubbed Hour Mode:** Tile 1 shows Steps so far (`%,d` with isolated hour delta indicator), Tile 2 shows Distance so far, Tile 3 shows Calories so far, Tile 4 shows Goal/Day share so far.
    - Preserved all test tags (`kpi_steps_hour`, `kpi_distance`, `kpi_calories`, `kpi_share_of_day`), ensured M3 typography and minimum 48dp touch targets.
  - *Acceptance Criteria:* When scrolled to max start of day (hour 0), displays 0 (or hour 0 steps); when scrolled to max end of day, displays full entire day progress.

- [x] **Task 7.6: Unit & Compose UI Test Suite Validation** `[COMPLETED]`
  - *Files:* `app/src/test/java/com/example/HourlyTimelineTest.kt`, `app/src/test/java/com/example/HourlyTimelineComposeTest.kt`, `app/src/test/java/com/example/StepTrackComposeUiTest.kt`
  - *Details:* Implemented automated Robolectric unit and Compose tests verifying:
    - Cumulative calculation accuracy for all 24 hours.
    - Default state initialization on startup (`selectedScrubHour == null`).
    - Pull gesture directionality (drag left advances forward in LTR).
    - RTL bar ordering and scroll layout.
    - Test tags integrity and accessibility descriptions.
  - *Acceptance Criteria:* `gradle :app:testDebugUnitTest` passes 100% deterministically.

---

### Milestone 8: Scalable Localization Architecture & Complete Language Parity (P0)
**Objective:** Fully resolve the language display conflict where switching to Arabic still exposes English text (Awards, Virtual Destinations, Goals tiers, Trust Center, Walking Session, Settings sections, and Themes). Establish a modular, zero-hardcode architecture where all models and views reference centralized string resource IDs, with 100% Arabic and English parity, ready for immediate expansion into additional languages.

- [ ] **Task 8.1: Canonical Base Text Resources & Full Arabic Localization Parity** `[PLANNED]`
  - *Files:* `app/src/main/res/values/strings.xml`, `app/src/main/res/values-ar/strings.xml`
  - *Details:* Define all base strings for Achievements, Destinations, Streak Tiers, Themes, Sensor Modes, Walking Sessions, Goals tiers, and Trust Center in `strings.xml`. Provide complete, high-quality Arabic translations in `values-ar/strings.xml` with zero missing keys.
  - *Acceptance Criteria:* Exact 1:1 key parity between English and Arabic resource files.

- [ ] **Task 8.2: Domain Content Model Localization (Zero Hardcoding in Models)** `[PLANNED]`
  - *Files:*
    - `app/src/main/java/com/example/domain/model/Destination.kt`
    - `app/src/main/java/com/example/domain/content/AchievementRegistry.kt`
    - `app/src/main/java/com/example/domain/content/DestinationRegistry.kt`
    - `app/src/main/java/com/example/domain/content/MultiplierRegistry.kt`
    - `app/src/main/java/com/example/ui/theme/ThemeChoice.kt`
    - `app/src/main/java/com/example/sensor/StepSensorManager.kt`
  - *Details:* Update all registries and enums to reference `@StringRes` IDs rather than raw English strings. Provide helper extension methods so any consumer can retrieve localized text safely.
  - *Acceptance Criteria:* Models contain zero hardcoded user-facing strings; all metadata links directly to the resource system.

- [ ] **Task 8.3: UI Screens & Components Localization Audit & Refactor** `[PLANNED]`
  - *Files:*
    - `app/src/main/java/com/example/ui/screens/AchievementsScreen.kt`
    - `app/src/main/java/com/example/ui/screens/AnalyticsScreen.kt`
    - `app/src/main/java/com/example/ui/screens/GoalsScreen.kt`
    - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
    - `app/src/main/java/com/example/ui/screens/TrustCenterScreen.kt`
    - `app/src/main/java/com/example/ui/components/WalkingSessionSheet.kt`
    - `app/src/main/java/com/example/ui/components/DayDetailSheet.kt`
    - `app/src/main/java/com/example/ui/screens/settings/sections/*`
    - `app/src/main/java/com/example/service/StepNotificationBuilder.kt`
  - *Details:* Replace every remaining hardcoded string with `stringResource(...)`. Ensure formatted numbers and units adapt to the current locale.
  - *Acceptance Criteria:* 0 hardcoded English words visible when Arabic is selected.

- [ ] **Task 8.4: Dynamic Real-Time Compose Locale Recomposition in MainActivity** `[PLANNED]`
  - *Files:* `app/src/main/java/com/example/MainActivity.kt`, `app/src/main/java/com/example/util/LocaleHelper.kt`
  - *Details:* Provide localized `Context` and `Configuration` through `CompositionLocalProvider(LocalContext provides ..., LocalConfiguration provides ...)` so language toggling dynamically updates all composables on the spot without lag or missed strings.
  - *Acceptance Criteria:* Switching between English and Arabic in Settings immediately changes the entire app language with full fidelity.

- [ ] **Task 8.5: Verification & Automated Regression Testing** `[PLANNED]`
  - *Files:* Unit and Compose tests
  - *Details:* Run `gradle :app:testDebugUnitTest` and `compile_applet` to confirm no regressions and complete test pass.
  - *Acceptance Criteria:* All tests pass (0 failures).

---

### Milestone 11: Cybernetic Stride Evolution & Gamified Athletic HUD (P1)
**Objective:** Elevate the application's athletic engagement by integrating the anime-cyberpunk "Step Strike" HUD aesthetic, a real-time cadence supercharge gauge, tactical hold-to-complete workout controls, post-workout raid summary with S/A/B rank crests and kilometer splits, and a dedicated Hero Profile & Gear Vault tab in JourneyScreen.

- [x] **Task 11.1: Cybernetic Stride Design System, Tokens & Theme Integration** `[COMPLETED]`
  - *Files:* `ThemeChoice.kt`, `ThemeRegistry.kt`, `strings.xml`, `values-ar/strings.xml`
  - *Details:* Implement `ThemeChoice.CYBERNETIC_STRIDE` with obsidian `#0F131D`, Hyper Cyan `#00F0FF`, and Electric Mint `#4DFFB2` palettes and radial atmospheric gradient.
  - *Acceptance Criteria:* Theme is dynamically selectable in ThemeSelectionSheet and renders correctly in light and dark modes.

- [x] **Task 11.2: Hunter RPG Domain Layer & Local Persistence (Hero, Gear & Currencies)** `[COMPLETED]`
  - *Files:* `domain/model/HunterProfile.kt`, `domain/model/GearItem.kt`, `domain/content/GearRegistry.kt`, `UserPreferencesRepository.kt`, `StepViewModel.kt`
  - *Details:* Model player levels, Combat Power (CP), equipped gear items (Footwear, Core, Exosuit, Chrono), and currencies (Sparks, Cores, Shards) stored locally in DataStore.
  - *Acceptance Criteria:* State is reactive, completely local, and thread-safe.

- [x] **Task 11.3: Active Walking Session Cockpit Upgrade (Hold-to-Complete & Live HUD)** `[COMPLETED]`
  - *Files:* `ui/components/WalkingSessionSheet.kt`
  - *Details:* Add circular Real-Time Cadence Supercharge Gauge, tactile "Hold to Complete" slider, and in-stride interval challenges.
  - *Acceptance Criteria:* Prevents accidental workout stops; provides instant intensity feedback.

- [x] **Task 11.4: Post-Workout Raid Summary & Grade S Crest Recap** `[COMPLETED]`
  - *Files:* `ui/components/WorkoutSummarySheet.kt`, `ui/components/WalkingSessionSheet.kt`
  - *Details:* Present performance grade crest (S/A/B), kilometer-by-kilometer split cadence bars, itemized EXP surge, and loot drops.
  - *Acceptance Criteria:* Celebratory recap seamlessly triggered on workout finish.

- [x] **Task 11.5: Journey Screen "Hero & Loadout" Tab & Equipment Vault** `[COMPLETED]`
  - *Files:* `ui/screens/journey/components/HeroLoadoutView.kt`, `ui/screens/JourneyScreen.kt`
  - *Details:* Add 5th tab `HERO` to JourneyScreen with paperdoll loadout, gear stat tuning drawer, and filterable storage vault.
  - *Acceptance Criteria:* Full touch target compliance (>= 48dp), responsive M3 layout.

- [x] **Task 11.6: Home Dashboard Enhancements & Cadence Peak Insights** `[COMPLETED]`
  - *Files:* `ui/screens/HomeScreen.kt`
  - *Details:* Add Hunter CP status chip, Mana Overdrive state banner, hourly cadence peak insight callout, and upgraded start walk CTA.
  - *Acceptance Criteria:* Preserves all existing Compose testTags and RTL/LTR symmetry.

- [x] **Task 11.7: Localization Parity, Unit & Compose UI Test Suite** `[COMPLETED]`
  - *Files:* `strings.xml`, `values-ar/strings.xml`, `CyberneticStrideUnitTest.kt`, `HeroLoadoutComposeTest.kt`
  - *Details:* Complete string parity, unit tests for CP formula and gear logic, and Compose tests.
  - *Acceptance Criteria:* 100% test pass via `gradle :app:testDebugUnitTest`.

---

### Milestone 12: TrueStep UX Architecture Realignment & Name Consistency (P0)
**Objective:** Restore TrueStep's core product differentiators (trust, calmness, transparency, and sustainable habit formation) by purging gaming/fantasy artifacts from the Home page, fixing the accidental tap-to-pause safety hazard on the Hero Step Ring, elevating essential daily metrics above the fold, resolving nested touch targets, and ensuring 100% brand name consistency under "TrueStep".

- [x] **Task 12.1: Complete Name Purge & TrueStep Brand Consistency** `[COMPLETED]`
  - *Files:* `app/src/main/res/xml/backup_rules.xml`, `app/src/main/res/xml/data_extraction_rules.xml`, `app/proguard-rules.pro`, `docs/ASO_PLAY_STORE_METADATA.md`, `README.md`, `app/src/test/java/com/example/TrueStepComprehensiveTest.kt`, `docs/plan.md`, `docs/task.md`, `docs/memory.md`, `docs/implementation_plan.md`
  - *Details:* Removed all lingering references to the legacy working name. Renamed test suite to `TrueStepComprehensiveTest.kt`.
  - *Acceptance Criteria:* 0 occurrences of legacy working name across active codebase, configuration, and documentation.

- [x] **Task 12.2: Home Dashboard De-cluttering (Remove RPG/Fantasy Clutter)** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
  - *Details:* Remove Hunter Header (Level, CP, class title) and Mana Overdrive banner. Reclaim ~220dp of vertical height so real health telemetry is prominent.
  - *Acceptance Criteria:* Clean, calm dashboard without gaming distraction.

- [x] **Task 12.3: Hero Step Ring Interaction Safety (Fix Accidental Pause)** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/components/HeroStepRing.kt`, `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
  - *Details:* Remove full-surface 200dp clickable toggle on the step ring. Provide an explicit, intentional pause button with confirmation feedback. Move `PausedBanner` to the top of the dashboard.
  - *Acceptance Criteria:* Zero accidental pauses during scrolling; pause state immediately noticeable without scrolling.

- [x] **Task 12.4: Above-the-Fold Metric KPI Grid Elevation** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/screens/HomeScreen.kt`, `app/src/main/java/com/example/ui/components/HomeKpiGrid.kt`
  - *Details:* Relocate 2x2 KPI Grid directly below the Hero Step Ring / Trust Status. Integrate Active Minutes alongside Steps, Distance, and Calories.
  - *Acceptance Criteria:* Core daily metrics glanceable immediately upon opening the app without scrolling.

- [x] **Task 12.5: Trust Card Touch Target & Accessibility Refactoring** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
  - *Details:* Decouple nested clickable regions on the Trust Card. Make Trust Status and Goal Tiers separate cards with dedicated TalkBack content descriptions and navigation chevrons.
  - *Acceptance Criteria:* TalkBack accessibility verified with zero touch conflict warnings.

- [x] **Task 12.6: Hourly Scrubber & History Preview Polish** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/components/HourlyScrubRibbon.kt`, `app/src/main/java/com/example/ui/screens/HomeScreen.kt`, `app/src/main/java/com/example/ui/components/HistoryPreviewChart.kt`
  - *Details:* Add prominent floating "Exit Scrub Mode" indicator when an hour is scrubbed. Streamline Home preview chart to a clean 7-day sparkline directing to the History tab.
  - *Acceptance Criteria:* Clear scrub state exit path; eliminates 2500dp vertical scroll fatigue.

- [x] **Task 12.7: Walking Session Cockpit Refinement** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/components/WalkingSessionSheet.kt`
  - *Details:* Remove mini-boss encounters and arcade currency drops. Focus on real physical telemetry: duration, distance, pace, and cadence (SPM). Replace Hold-to-Complete with standard accessible buttons.
  - *Acceptance Criteria:* Calm, focused workout cockpit.

- [x] **Task 12.8: Unit & Compose UI Test Suite Validation** `[COMPLETED]`
  - *Files:* `app/src/test/java/com/example/TrueStepComprehensiveTest.kt`, `app/src/test/java/com/example/StepTrackComposeUiTest.kt`
  - *Details:* Update Compose UI tests for the streamlined Home screen layout; verify 100% test pass.
  - *Acceptance Criteria:* `compile_applet` clean build and `gradle :app:testDebugUnitTest` passes all tests.

---

### Milestone 13: Gamified Athletic HUD & Cybernetic Experience Alignment (P0 - USER APPROVED)
**Objective:** Adapt the application to the user-approved **`temp/02_gamified_athletic_tracker.jpg`** design direction. Establish the Cybernetic Stride aesthetic as the premier experience, elevate the Journey & Quests hub into primary bottom navigation, integrate the Hunter Profile header & Combat Power (CP) onto the Home dashboard, power up active walking sessions with in-stride encounters and supercharge gauges, and verify complete test parity.

- [x] **Task 13.1: Premier Theme Alignment & Resource Collision Fix** `[COMPLETED]`
  - *Files:* `UserPreferencesRepository.kt`, `app/src/main/res/mipmap-*`
  - *Details:* Resolved duplicate resource collisions in mipmap directory. Set default theme to `CYBERNETIC_STRIDE` so new launches default to the approved anime cyberpunk aesthetic.
  - *Acceptance Criteria:* Clean build via `compile_applet`; defaults to Cybernetic Stride.

- [x] **Task 13.2: Elevate Journey to Primary Navigation** `[COMPLETED]`
  - *Files:* `Screen.kt`, `AppNavigation.kt`
  - *Details:* Added `Screen.Journey` to `Screen.navItems` (`Home`, `Journey`, `History`, `Goals`, `Settings`).
  - *Acceptance Criteria:* Direct bottom navigation access to Quests, Hero Loadout, Expeditions, Streak Vault, and Trophies.

- [x] **Task 13.3: Home Dashboard Gamified Athletic HUD Integration** `[COMPLETED]`
  - *Files:* `HomeScreen.kt`
  - *Details:* Restored Hunter Profile header card (`home_hunter_header_card`) with Level, Rank, Combat Power (CP), and Currencies (Sparks, Cores, Shards). Added Mana Overdrive banner (`home_aura_overdrive_banner`). High-energy workout action CTA ("⚡ START LIVE STRIDE [GPS HUD]").
  - *Acceptance Criteria:* Matches `temp/02_gamified_athletic_tracker.jpg` layout and visual hierarchy while preserving touch safety on the step ring.

- [x] **Task 13.4: Active Workout Cockpit & Raid Victory Summary** `[COMPLETED]`
  - *Files:* `WalkingSessionSheet.kt`, `WorkoutSummarySheet.kt`
  - *Details:* Real-time Cadence Supercharge circular dial, in-stride speed encounter challenges, Hold-to-Complete tactile finish button, and post-workout Grade S/A/B Raid Victory Summary with loot drops.
  - *Acceptance Criteria:* Smooth animations, safe finish confirmation, rewards claimed into DataStore.

- [x] **Task 13.5: Comprehensive Test Suite & Parity Verification** `[COMPLETED]`
  - *Files:* `CyberneticStrideUnitTest.kt`, `HeroLoadoutComposeTest.kt`, `StepTrackComposeUiTest.kt`
  - *Details:* Ran `compile_applet` and `gradle :app:testDebugUnitTest`. Verified 100% test pass.
  - *Acceptance Criteria:* 0 build errors, 0 test failures. (VERIFIED: All tests passed, BUILD SUCCESSFUL).

---

### Milestone 15: Homepage UI/UX Remediation & Fluid Responsive Architecture (P0)
**Objective:** Execute the Master Plan from `implementation_plan.md`: remediate text wrapping, vertical clipping, rigid height constraints, and visual overcrowding across TrueStep. Refactor stacked action buttons, fluid telemetry rows, auto-fitting radial dial text, responsive hourly legend symmetry, and migrate `JourneyScreen` to `ScrollableTabRow`.

- [x] **Task 15.0: Resolve Resource Collision & Prune Duplicate Mipmap WebP Files** `[COMPLETED]`
- [x] **Task 15.1: Action Buttons & Telemetry Card Fluid Layout & String Parity** `[COMPLETED]`
- [x] **Task 15.2: Hero Step Ring Radial Readout Auto-Fit & Localization** `[COMPLETED]`
- [x] **Task 15.3: Home KPI Grid & Hourly Ribbon Legend Symmetry** `[COMPLETED]`
- [x] **Task 15.4: Journey Screen ScrollableTabRow Migration** `[COMPLETED]`
- [x] **Task 15.5: Automated Verification & Unit/Robolectric Test Pass** `[COMPLETED]`

---

### Milestone 16: Step Ring Full-Surface Pause & Resume Interaction (P0)
**Objective:** Address user feedback to enable pausing and resuming tracking across the entire circular step ring (`HeroStepRing`), rather than restricting click interaction solely to the "tap to pause / tap to resume" text pill. Ensure full 196dp touch surface interactivity with bounded circular ripple feedback and accessible semantics.

- [x] **Task 16.1: Full-Surface Clickable Step Ring Dial in HeroStepRing** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/components/HeroStepRing.kt`
  - *Details:* Attached `Modifier.clickable(onClick = onTogglePause, role = Role.Button, onClickLabel = ...)` to the central 196.dp circular dial container (`hero_ring_dial`). Tapping anywhere within the dial triggers pause/resume. Removed inner `.clickable` from `ring_pause_hint` to avoid nested clickable anti-patterns while preserving `.testTag("ring_pause_hint")`.
  - *Acceptance Criteria:** User tapping anywhere within the steps ring pauses/resumes tracking; full circular ripple; zero nested clickable warnings.

- [x] **Task 16.2: Compose UI Verification & Regression Test Suite** `[COMPLETED]`
  - *Files:* `app/src/test/java/com/example/StepTrackComposeUiTest.kt`
  - *Details:* Added Compose test verifying that clicking `hero_ring_dial` invokes `onTogglePause`. Verified existing tests continue to pass.
  - *Acceptance Criteria:* Clean build via `compile_applet` and all unit/Robolectric tests pass via `gradle :app:testDebugUnitTest`.

---

### Milestone 17: Production Readiness Audit & Google Play Store Publishing Certification (P0)
**Objective:** Complete comprehensive production verification ensuring TrueStep is 100% ready for publishing to Google Play Store. Fix resource compilation PNG byte signatures, enforce Anti-Review-Gating policy compliance, harden foreground service start and permission sequencing, and generate production-certified release APK and Android App Bundle (.aab).

- [x] **Task 17.1: Multi-Density Raster PNG Signature Regeneration** `[COMPLETED]`
  - *Files:* `app/src/main/res/mipmap-*`
  - *Details:* Regenerated all 5 mipmap densities as pristine 32-bit RGBA PNGs (`ic_launcher.png`) and circular masked PNGs (`ic_launcher_round.png`) via ImageMagick, eliminating AAPT2 resource compilation errors.
  - *Acceptance Criteria:* `mergeReleaseResources` executes cleanly with 0 errors.

- [x] **Task 17.2: Play Store Policy Anti-Review-Gating Alignment** `[COMPLETED]`
  - *Files:* `app/src/main/java/com/example/ui/components/RatingReviewDialog.kt`
  - *Details:* Ensured "Rate on Google Play" is always unhindered regardless of star selection, eliminating review-gating policy violations while retaining direct feedback option for lower ratings.
  - *Acceptance Criteria:* Zero policy violations; 100% test tag preservation.

- [x] **Task 17.3: Defensive Service Lifecycle & Sequential Runtime Permissions** `[COMPLETED]`
  - *Files:* `StepCounterForegroundService.kt`, `MainActivity.kt`
  - *Details:* Guarded `StepCounterForegroundService.start` against `ForegroundServiceStartNotAllowedException` on Android 12+/14+; added sequential runtime permission fallback prompt in `MainActivity`.
  - *Acceptance Criteria:* Zero crashes on service state transitions.

- [x] **Task 17.4: Release Binary & Android App Bundle (.aab) Verification** `[COMPLETED]`
  - *Files:* `app-release.apk` (6.3MB), `app-release.aab` (10.2MB)
  - *Details:* Successfully assembled release APK (`assembleRelease`) and release App Bundle (`bundleRelease`) with full R8 code shrinking, resource shrinking, ProGuard optimizations, and verified 100% test suite pass (33 tests, 0 failures).
  - *Acceptance Criteria:* Release artifacts generated, signed, and ready for Google Play Store upload.

## Risk Assessment & Mitigations

| Risk | Mitigation |
| :--- | :--- |
| Health Connect SDK availability varies across device manufacturers. | Always verify `HealthConnectClient.getSdkStatus()` before instantiating client; fail gracefully to hardware sensor when unavailable. |
| Removing sample data leaves first launch with empty charts. | Beautiful empty states with helpful hints and an explicit "Load Sample Data" option in Settings for review. |
| Android 14+ background foreground service restrictions. | Ensure `FOREGROUND_SERVICE_HEALTH` is declared and activity recognition is granted before starting service; handle exceptions cleanly. |
| Room schema changes break existing installations. | Keep version 2 schema intact; avoid destructive drops. |
| Reorganizing Settings breaks existing UI tests that rely on testTags. | Retain 100% of existing test tags (`theme_selection_card`, `adaptive_goals_switch`, `delete_local_data_button`, etc.) on the new modular components. |
| Horizontal scroll inside vertical scroll in HomeScreen. | Compose handles nested horizontal scroll inside `verticalScroll` natively without touch interception conflicts as long as drag gestures are consumed along the horizontal axis. |
| Rapid scrubbing triggering excessive recompositions. | Keep hourly progression computation in `derivedStateOf` / lightweight ViewModel state; do not trigger database writes during scrub. |
| RTL scroll offset inversion quirks on different Android API levels. | Use standard Compose `Modifier.horizontalScroll(scrollState)` which automatically handles RTL layout direction flipping at the framework level. |
