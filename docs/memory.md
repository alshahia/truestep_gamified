# Engineering Memory: TrueStep Architecture & System State

## 1. System Overview & Context
- **Application:** TrueStep (Android Pedometer & Habit Tracking)
- **Application ID:** `com.aistudio.steptrack.jigkjt`
- **Target Platform:** Android SDK 36 (VanillaIceCream), Min SDK 26 (Android 8.0)
- **Primary Tooling:** Kotlin 2.2, Jetpack Compose (BOM 2024.09), Room 2.7.0 (KSP), WorkManager 2.10, Health Connect 1.1.0-alpha07, DataStore Preferences 1.1.7.
- **Localization:** Multilingual (English default LTR, Arabic native RTL in `values-ar/strings.xml`).

---

## 2. Technical Invariants & Rules

### Data & Privacy
1. **Privacy-First Zero Telemetry:** No user data leaves the device. No cloud accounts, external trackers, or server-side analytics.
2. **No Silent Data Fabrication:** Fresh installs MUST start at 0 steps and 0 days of history. Sample data is restricted to explicit user action ("Load Sample Data" in Settings) and must NEVER be auto-injected on launch.
3. **Data Preservation:** User history, daily notes, and achievement unlocks must survive schema upgrades. `fallbackToDestructiveMigration` is strictly forbidden in production configurations.
4. **DataStore for App Preferences:** Theme choices, first-launch completion, and carrying mode belong in Jetpack DataStore (`androidx.datastore.preferences`), not in transient in-memory StateFlows or heavy database tables.

### Sensor & Battery Efficiency
1. **Low-Power Telemetry:** Background step tracking must use `SensorManager.SENSOR_DELAY_NORMAL` with hardware batching (`maxReportLatencyUs` = 10,000,000 µs / 10s) whenever hardware step counter is active. `SENSOR_DELAY_UI` must never be used in background services.
2. **Foreground Service Compliance:** `StepCounterForegroundService` specifies `android:foregroundServiceType="health"` with `FOREGROUND_SERVICE_HEALTH` and `ACTIVITY_RECOGNITION` permissions declared and granted prior to service launch.
3. **Sequential Permission Flow:** Never launch multiple Android runtime permission requests on the same frame. Chain requests sequentially: Activity Recognition -> Post Notifications -> Health Connect.

### Health Connect Contract
1. **Read & Merge:** When permissions are granted, Health Connect step records are queried for the active day and reconciled with hardware sensor records using time-interval overlap detection.
2. **No Dead Permissions:** If Health Connect permissions are declared, a functioning sync worker and user-facing sync trigger must exist.

### Settings & UI Architecture Invariants
1. **Isolated Settings State:** Settings UI components must NOT observe high-frequency step events or scrub hours. They observe a isolated, low-frequency preferences state (`SettingsUiState`).
2. **No Nested Scroll Containers:** Avoid placing lazy grids or scrollable lists inside a vertically scrolling parent Column with fixed height bounds. Use modal sheets or dialogs for large pickers (e.g. `ThemeSelectionSheet`).
3. **Accessibility & Touch Targets:** Every interactive row, switch, chip, and picker must conform to M3 minimum 48dp x 48dp touch targets and provide explicit TalkBack content descriptions.
4. **Test Tag Preservation:** All legacy test tags (`theme_selection_card`, `carrying_mode_card`, `adaptive_goals_switch`, `lock_goals_switch`, `recovery_mode_switch`, `recalculate_goal_button`, `sync_health_connect_button`, `export_csv_button`, `export_json_button`, `delete_local_data_button`, `confirm_delete_button`) must be maintained without regression.

### Hourly Timeline & KPI Invariants
1. **Fluid 24-Hour Horizon:** The hourly timeline must span all 24 hours (`00:00` to `23:00`) of the day.
2. **Scroll Physics Symmetry:** Horizontal movement must use physics-based scrolling (`Modifier.horizontalScroll(scrollState)`), ensuring pulling finger left advances time forward in LTR, matching the daily and weekly charts.
3. **Full Day Default State:** On app launch, resume, or when unselected, `selectedScrubHour` must be `null` and the 2×2 KPI grid must display the **full entire day progress** (total steps today, total km today, total calories today, goal %). Never resume into a stale past scrubbed hour.
4. **Cumulative Progression Semantics:** When scrubbed to hour $H$, metrics reflect cumulative progress from start of day through hour $H$. Max left (in LTR) reaches hour 0 (0 or first hour steps); max right reaches hour 23 (full day totals).
5. **Strict RTL & LTR Parity:** LTR starts at 00:00 on the left and advances rightward; RTL (Arabic) starts at 00:00 on the right and advances leftward. Directionality must adapt seamlessly using `LocalLayoutDirection`.

---

## 3. Findings Registry & Root Cause Matrix

| ID | Issue Description | Root Cause | Severity | Resolution Status |
| :--- | :--- | :--- | :--- | :--- |
| **BUG-01** | Dead Health Connect implementation | `HealthConnectHelper.readTodaySteps()` is uncalled; callback is empty `{}` | Critical | RESOLVED |
| **BUG-02** | Simultaneous permission dialog collision | 3 launchers invoked simultaneously in `LaunchedEffect(Unit)` in `MainActivity` | Critical | RESOLVED |
| **BUG-03** | Silent injection of fake records on install | `StepViewModel.init` called `repository.simulateSampleDataIfEmpty()` | High | RESOLVED |
| **BUG-04** | Broken streak reset logic | `updateStreakForDate` does not reset streak when yesterday's goal was missed | High | RESOLVED |
| **BUG-05** | Destructive Room migration | `StepDatabase` uses `.fallbackToDestructiveMigration(dropAllTables = true)` | Critical | RESOLVED |
| **BUG-06** | Midnight rollover stale date query | `getTodaySteps()` binds to static date string evaluated once at construction | High | RESOLVED |
| **BUG-07** | O(N) full-table SQLite query per step event | `checkAchievements()` queries `stepDao.getAllList()` on every step delta | High | RESOLVED |
| **BUG-08** | Unbounded growth of `step_source_records` | New row every 60s without pruning or retention policy | Medium | RESOLVED |
| **BUG-09** | Hardcoded dummy floats in Analytics screen | `AnalyticsScreen` draws `listOf(0.12f, 0.25f...)` instead of `HourlyStepDao` data | Medium | RESOLVED |
| **BUG-10** | Unreachable onboarding screen | `showOnboarding` hardcoded to `false` in `AppNavigation` | Medium | RESOLVED |
| **BUG-11** | Achievements disconnected from UI | Unlocks written to DB but never exposed in `StepViewModel` or `AnalyticsScreen` | Medium | RESOLVED |
| **BUG-12** | Excessive background battery drain | `StepSensorManager` registers listener at `SENSOR_DELAY_UI` (60Hz) | High | RESOLVED |
| **BUG-13** | In-memory theme loss | `_selectedTheme` in ViewModel is reset on process death | Medium | RESOLVED |
| **BUG-14** | Orphaned dead code files | 5 unused UI/domain files (`DestinationBadge`, `LinearStepProgress`, etc.) | Low | RESOLVED |
| **BUG-15** | Hardcoded strings breaking localization | English strings hardcoded in UI composables despite full Arabic support | Medium | RESOLVED |
| **BUG-16** | Recomposition thrashing in SettingsScreen | SettingsScreen observes entire `StepUiState`; recomposes on every footstep | High | RESOLVED |
| **BUG-17** | Nested scroll layout conflict | `LazyVerticalGrid` inside `verticalScroll` (`heightIn(280.dp)`) causes scroll fight | Medium | RESOLVED |
| **BUG-18** | Severe parameter drilling in Settings | 14+ callbacks passed to `SettingsScreen` without modular encapsulation | Medium | RESOLVED |
| **BUG-19** | Missing TrueStep MVP settings features | Notifications toggle and Accessibility options missing from Settings UI | Medium | RESOLVED |
| **BUG-20** | Hourly timeline gesture inverted vs daily/weekly charts | Fixed track using absolute coordinates instead of physics-based scroll container | High | RESOLVED |
| **BUG-21** | Incomplete 17-hour timeline span (06:00 to 22:00) | Hardcoded sub-range omitting early morning (00:00-05:00) and late night (23:00) | Medium | RESOLVED |
| **BUG-22** | Single-hour vs full-day KPI disconnect on startup/reopen | `selectedScrubHour == null` fell back to single hour `LocalTime.now().hour` | High | RESOLVED |
| **BUG-23** | Scrubber progression disconnect | Isolated hour statistics displayed instead of cumulative day progression | High | RESOLVED |
| **BUG-24** | Inconsistent RTL timeline start/end alignment | Arabic RTL layout requires 00:00 on right advancing leftward | Medium | RESOLVED |
| **BUG-25** | Hardcoded strings causing untranslated Arabic pages | Registries (Achievements, Destinations, Themes), Goals tiers, Walking sessions, and Trust Center contain raw English strings | High | RESOLVED |
| **BUG-26** | Duplicate Mipmap Launcher Icon Resources | Duplicate .png and .webp icons in res/mipmap-* broke packageDebugResources | High | RESOLVED |
| **BUG-27** | Accidental Tap-to-Pause Hazard on Hero Step Ring | Entire 200dp center circle clickable to toggle pause; triggers accidental pauses during scroll drags | Critical | RESOLVED |
| **BUG-28** | Design Philosophy Selection & Alignment | User approved `temp/02_gamified_athletic_tracker.jpg` (Gamified Athletic HUD) | High | RESOLVED (Milestone 13) |
| **BUG-29** | Essential Metric KPIs Above the Fold | Home KPI grid and Hunter Profile integrated directly above the fold | High | RESOLVED |
| **BUG-30** | Nested Clickable Touch Conflict on Trust Card | Decoupled into distinct accessible cards with dedicated touch targets | Medium | RESOLVED |
| **BUG-31** | Workout HUD Engagement | In-Stride Encounter Challenge and Cadence Supercharge Dial integrated | Medium | RESOLVED (Milestone 13) |
| **BUG-32** | Lingering Legacy Working Name in Artifacts | "StrideTrust" lingered in XML comments, ProGuard rules, test classes, and docs | Low | RESOLVED (Milestone 12) |
| **BUG-33** | Restrictive Pause/Resume Touch Surface in Step Ring | Pause/resume only clickable via small inner text pill rather than entire steps ring | Medium | RESOLVED (Milestone 16) |
| **BUG-34** | AAPT2 Mipmap PNG Byte Signature Corruption | Raster icons in `res/mipmap-*` contained `\xEF\xBF\xBD` UTF-8 replacement header instead of `\x89PNG` | Critical | RESOLVED (Milestone 17) |
| **BUG-35** | Potential Google Play Review Gating Policy Violation | Rating dialog filtered <4 stars away from Play Store review | High | RESOLVED (Milestone 17) |
| **BUG-36** | Potential ForegroundServiceStartNotAllowedException Crash | `StepCounterForegroundService.start` called without defensive exception handling on Android 12+/14+ | High | RESOLVED (Milestone 17) |

---

### Gamified Athletic HUD Architecture & Invariants (APPROVED DESIGN DIRECTION)
1. **Gamified Athletic Identity:** User-approved adoption of `temp/02_gamified_athletic_tracker.jpg`. Delivers a cyberpunk anime runner athletic tracker combining offline telemetry with Hunter Level, Combat Power (CP), Mana Overdrive, daily quest XP progression, and in-stride encounters.
2. **Step Ring Touch & Pause Interaction:** The primary step display (`HeroStepRing`) features full-surface tap interaction across the entire 196dp circular dial (`hero_ring_dial`) for seamless pause/resume toggling anywhere within the ring, complemented by the dedicated top-end icon toggle (`hero_ring_toggle`).
3. **Above-the-Fold Glanceability:** Primary daily activity metrics and Hunter CP status are glanceable immediately upon opening the app.
4. **Clean Accessibility Trees:** All interactive targets maintain independent, unobstructed 48dp touch boundaries.
5. **Primary Navigation Hub:** `Screen.Journey` is a first-class tab in `Screen.navItems` (`Home`, `Journey`, `History`, `Goals`, `Settings`), hosting Quests, Hero Loadout, Expeditions, Streak Vault, and Trophies.

---

### Zero-Hardcoding Architecture Rules (MANDATORY INVARIANTS)
1. **Zero Hardcoded Strings:** Every piece of user-facing text (titles, subtitles, button labels, descriptions, badge labels, format strings) MUST be defined in `res/values/strings.xml` and translated in `res/values-ar/strings.xml`.
2. **Unified UiText Sealed Class:** ViewModels and domain models MUST communicate text using `UiText` (`UiText.StringResource`, `UiText.PluralResource`, `UiText.DynamicString`), never by passing localized strings directly or holding Android `Context` references.
3. **Domain Content Res Coupling:** Content registries and enums (`Achievement`, `Destination`, `MultiplierTier`, `ThemeChoice`, `SensorMode`, `WalkMode`) MUST store `@StringRes` identifiers rather than hardcoded English strings.
4. **Universal Design Tokens (No Raw DP):** All paddings, margins, and layout gaps MUST consume `MaterialTheme.spacing.*` (`AppSpacing`). All component heights, touch targets, icon sizes, and elevations MUST consume `MaterialTheme.dimens.*` (`AppDimensions`). Never write raw `.dp` literals in composables.
5. **Universal Shapes:** All card, dialog, sheet, chip, and button shapes MUST consume `MaterialTheme.appShapes.*` (`AppShapes`). Never write raw `RoundedCornerShape(X.dp)` in composables.
6. **Universal Metric Formatting:** All numbers, steps, distances, calories, durations, and percentages MUST be formatted via `MetricFormatter`. Never hardcode `%,d`, `%.2f km`, or `"kcal"` in composables.
7. **Extensibility & Expandability:** Adding a new language in the future only requires adding `res/values-<lang>/strings.xml` with translated strings. No Kotlin code modifications should be necessary.
8. **Dynamic Compose Locale Switch:** When language is switched in Settings, Compose immediately re-renders using the updated `Configuration` and `Context` via `CompositionLocalProvider(LocalContext, LocalConfiguration, LocalLayoutDirection)`.

---

## 4. Component Inventory & Retention Decisions

- `MainActivity.kt`: KEEP & EDIT (Sequential permissions, DataStore onboarding observation).
- `StepTrackApplication.kt`: KEEP (Preserve clean install setup).
- `StepCounterForegroundService.kt`: KEEP & EDIT (Low-power batching, safe start).
- `StepSensorManager.kt`: KEEP & EDIT (`SENSOR_DELAY_NORMAL`, 10s batching, safe listener API).
- `HealthConnectHelper.kt`: KEEP & EDIT (Integrate with repository & sync worker).
- `StepSyncWorker.kt`: KEEP & EDIT (Sync Health Connect + prune records > 14 days).
- `StepDatabase.kt`: KEEP & EDIT (Remove destructive migration, export schema).
- `StepRepository.kt` / `StepRepositoryImpl.kt`: KEEP & EDIT (Aggregated queries, fix streaks, dynamic date).
- `HourlyStepDao.kt`: KEEP (Connect to Analytics rhythm chart).
- `AchievementDao.kt`: KEEP (Connect to UI state).
- `DestinationRegistry.kt`: KEEP (Virtual walks progress).
- `OnboardingScreen.kt`: KEEP & ACTIVATE (Connect to DataStore first-launch flow).
- `HomeScreen.kt`: KEEP & EDIT (Extract hardcoded strings).
- `HistoryScreen.kt`: KEEP & EDIT (Extract hardcoded strings).
- `AnalyticsScreen.kt`: KEEP & EDIT (Add Badges grid, connect real hourly rhythm, extract strings).
- `SettingsScreen.kt`: KEEP & REFACTOR (Refactor from 566-line monolith into modular orchestrator < 150 lines).
- `ui/screens/settings/model/SettingsUiState.kt`: **NEW** (Dedicated settings-only state slice to eliminate step-tick recomposition).
- `ui/screens/settings/model/SettingsActions.kt`: **NEW** (Unified user action/intent dispatcher).
- `ui/screens/settings/components/*`: **NEW** (Reusable M3 settings primitives: `SettingsGroupCard`, `SettingsSwitchRow`, `SettingsActionRow`, `SettingsPickerRow`, `SettingsDangerRow`, `ThemeSelectionSheet`).
- `ui/screens/settings/sections/*`: **NEW** (7 domain sections: Appearance, Tracking, Goals, Notifications, Accessibility, Privacy, Troubleshooting).
- `ThemeRegistry.kt` / `AppThemeSpec.kt` / `Theme.kt`: KEEP.
- `TrustCenterScreen.kt`: KEEP & ENHANCE (Added `TrustCenterSheet` M3 ModalBottomSheet for on-demand inspection from Settings and Home).
- `Screen.kt` / `AppNavigation.kt`: REFACTOR (Bottom navigation items updated to Today, Journey, Analytics, History, Settings. Trust Center accessible via Home & Settings sheets).
- `DestinationBadge.kt`: **DELETE** (Dead code).
- `LinearStepProgress.kt`: **DELETE** (Dead code).
- `TrendChart.kt`: **DELETE** (Dead code).
- `PauseButton.kt`: **DELETE** (Dead code).
- `MultiplierRegistry.kt`: **DELETE** (Dead code; retain `AchievementRegistry` in clean file).
- `JourneyScreen.kt`: **NEW** (Primary Journey & Quests navigation tab hosting Quests, Expeditions, Streak Vault, Trophies).
- `ui/screens/journey/components/*`: **NEW** (`DailyQuestCard`, `StreakVaultCard`, `ExpeditionTrailCard`, `TrophyCabinetView`).
- `ui/components/CelebrationDialog.kt`: **NEW** (Milestone celebration dialog with share intent dispatch).
- `domain/content/QuestRegistry.kt`: **NEW** (5 daily mini-quests engine with localized content).
- `domain/model/DailyQuest.kt`: **NEW** (Daily quest data model with progress metrics).
- `domain/model/PaceZone.kt`: **NEW** (Cadence SPM aerobic zone model).
- `WalkingSessionSheet.kt`: ENHANCED (Live cadence SPM, color-coded PaceZone pill, brisk minutes summary).
- `HomeScreen.kt`: ENHANCED (Morning/afternoon/evening daily briefing banner, quests glance, celebration dialog).
- `test/java/com/example/JourneyRetentionUnitTest.kt`: **NEW** (Unit test suite for quests, thresholds, pace zones).
- `test/java/com/example/JourneyScreenComposeTest.kt`: **NEW** (Robolectric Compose tests for Journey tabs and celebration actions).
- `app/src/main/res/drawable/img_app_logo_winged.jpg`: **NEW** (Approved Prototype 3 high-res brand logo asset).
- `app/src/main/res/drawable/ic_launcher_foreground.xml`: UPDATED (66dp safe-zone centered layer-list).
- `app/src/main/res/drawable/ic_launcher_background.xml`: UPDATED (Pinned dark obsidian `#0B1324`).
- `app/src/main/res/mipmap-*/`: UPDATED (Full multi-density PNG mipmap suite + circular masked `ic_launcher_round.png`).
- `SplashScreen.kt`: UPDATED (Emblem card presents the high-res winged logo).

---

## 5. Verification Standards
- Run `gradle :app:testDebugUnitTest` to verify all unit and Robolectric tests pass (33 tasks, 0 failures).
- Verify compilation with `compile_applet` (clean build, 0 warnings).
- Verify zero regression in RTL/LTR rendering and Material 3 theming.

---

## 6. Milestone 11: Cybernetic Stride Architecture & RPG Telemetry Rules
1. **Local-Only RPG Mechanics:** Combat Power (CP), Hunter Level, Spark tokens, Cores, and Gear slots are computed and persisted 100% on-device in DataStore preferences. No cloud accounts, ads, or network telemetry.
2. **Combat Power (CP) Formula:** `CP = (HunterLevel * 250) + (StreakDays * 120) + (EquippedGearBonus)`. Provides positive reinforcement for walking consistency.
3. **Mana Overdrive & Spark Generation:** Every 1,000 steps generates +1 Spark Token. Overdrive reaches 100% charge at 5,000 steps or during brisk pace sessions (>110 SPM), providing an XP bonus multiplier.
4. **Tactile Safety Workout Controls:** The walking session cockpit enforces a "Hold to Complete" gesture (800ms hold with animated progress fill) to prevent accidental dismissal during vigorous movement.
5. **Post-Workout Victory Grade Calculation:** Grade S for pace >= 115 SPM and duration >= 15 min; Grade A for pace >= 100 SPM; Grade B for completed goals; Grade C for short strolls.
6. **Zero-Hardcoding Compliance:** All gear names, perks, rarity titles, currencies, and HUD telemetry labels reference Android string resources with 100% Arabic and English parity.

---

## 7. Milestone 14: Solo Leveling System Stat Allocator, Holographic HUD & Option C Cyber-Gates
1. **Interactive Stat Point Allocation:** Unassigned Stat Points (`availableStatPoints`) are granted on level-ups (+3 per level) and preserved in DataStore. The user allocates them across Strength (STR), Agility (AGI), Vitality (VIT), and Sense (SEN).
2. **Dynamic Combat Power (CP) Formula:** `CP = (HunterLevel * 250) + (StreakDays * 120) + (EquippedGearBonus) + (STR + AGI + VIT + SEN) * 15`. Every allocated stat point dynamically boosts Combat Power (+15 CP) in real-time.
3. **Option C Cyber-Gate Expeditions:** Reflects `temp/option_C_hybrid_arcade_cyber_gates.jpg` with S-Rank Cyber-Gate 04 portal graphics, 4 Chamber depths, arcade combo multiplier badges, and 4-tier loot cache drops (Legendary Core, Arcane Data Chip, Mystic Dust, Cybernetic Fragment).
4. **Holographic System Windows:** Translucent electric-blue Solo Leveling dialogs (`SystemHologramDialog.kt`) appear for major events (Level Up, Gate breach, Quest Arrival) with chamfered borders, dark obsidian backdrops, and action buttons.

---

## 8. Milestone 15: Homepage UI/UX Remediation & Fluid Responsive Architecture
1. **Full-Width Stacked Action CTA:** Eliminated rigid 48dp side-by-side row that caused "⚡ INITIATE STRIDE RAID" to wrap vertically into 4 lines. Primary CTA is full-width (52dp) placed above secondary "EDIT GOAL" button (44dp).
2. **Radial Dial Readout Auto-Fitting:** Hero step count numerals auto-scale (32sp / 28sp / 24sp) based on digit magnitude with single-line constraints. Inner chord pause hint pill enforces `maxLines = 1`, `softWrap = false`, `overflow = TextOverflow.Ellipsis`.
3. **Fluid Two-Row Telemetry Architecture:** `today_trust_status_card` and `home_hunter_header_card` enforce horizontal flex weights (`Modifier.weight(1f, fill = false)`) so hardware status, confidence chips, avatar names, and Combat Power chips never collide or clip.
4. **Hourly Ribbon Legend Symmetry:** Timeline start/end labels enforce `weight(1f, fill = false)`, `maxLines = 1`, and `TextOverflow.Ellipsis`, guaranteeing zero vertical line breaks across all screen sizes and RTL (Arabic) / LTR (English) layouts.
5. **ScrollableTabRow Usability Migration:** Replaced rigid fixed `PrimaryTabRow` with `ScrollableTabRow(edgePadding = 16.dp)` in `JourneyScreen.kt`, eliminating vertical text wrapping and squished tabs across all 5 navigation destinations.
6. **Zero-Hardcoding & Mipmap Sanitization:** Pruned duplicate `.webp` files from mipmap resource packages, and achieved 100% Arabic and English string parity across all telemetry, badges, and button labels.


