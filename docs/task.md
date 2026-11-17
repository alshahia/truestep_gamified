# Milestone 8 Task Execution Tracker: Scalable Localization Architecture & Complete Language Parity

## Overview
- **Milestone:** Milestone 8 (Scalable Localization Architecture & Complete Language Parity)
- **Status:** `COMPLETED`
- **Target Verification:** `compile_applet` and `gradle :app:testDebugUnitTest`

---

## Detailed Task Matrix

### Task 8.1: Canonical Base Text Resources & Full Arabic Localization Parity
- **Status:** `COMPLETED`
- **Dependencies:** None
- **Files Affected:**
  - `app/src/main/res/values/strings.xml`
  - `app/src/main/res/values-ar/strings.xml`
- **Implementation Scope:**
  1. Defined 100% of strings for Achievements (16 items), Streak Tiers (12 items), Destinations (12 items), Themes (16 items), Sensors, Walking Sessions, Goals tiers, Settings sections, and Trust Center in `strings.xml`.
  2. Provided matching Arabic translations in `values-ar/strings.xml`.
  3. Ensured exact 1:1 key parity between English and Arabic (430 keys in each file, 0 diffs).
- **Acceptance Criteria:** Exact 1:1 key parity verified by XML comparison tool.

---

### Task 8.2: Domain Content Model Localization (Zero Hardcoding in Models)
- **Status:** `COMPLETED`
- **Dependencies:** Task 8.1
- **Files Affected:**
  - `app/src/main/java/com/example/domain/model/Destination.kt`
  - `app/src/main/java/com/example/domain/content/AchievementRegistry.kt`
  - `app/src/main/java/com/example/domain/content/DestinationRegistry.kt`
  - `app/src/main/java/com/example/domain/content/MultiplierRegistry.kt`
  - `app/src/main/java/com/example/ui/theme/ThemeChoice.kt`
  - `app/src/main/java/com/example/sensor/StepSensorManager.kt`
  - `app/src/main/java/com/example/util/LocalizationUtil.kt`
- **Implementation Scope:**
  1. All models (`Achievement`, `Destination`, `MultiplierTier`, `ThemeChoice`, `SensorMode`) store `@StringRes` identifiers.
  2. `LocalizationUtil` centralized composable and non-composable helpers to resolve localized text cleanly.
- **Acceptance Criteria:** Domain models decoupled from any language; 100% resolvable via Android resources.

---

### Task 8.3: UI Screens & Components Localization Audit & Refactor
- **Status:** `COMPLETED`
- **Dependencies:** Task 8.1, Task 8.2
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/AchievementsScreen.kt`
  - `app/src/main/java/com/example/ui/screens/AnalyticsScreen.kt`
  - `app/src/main/java/com/example/ui/screens/GoalsScreen.kt`
  - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
  - `app/src/main/java/com/example/ui/screens/TrustCenterScreen.kt`
  - `app/src/main/java/com/example/ui/components/WalkingSessionSheet.kt`
  - `app/src/main/java/com/example/ui/components/DayDetailSheet.kt`
  - `app/src/main/java/com/example/ui/screens/settings/sections/*`
  - `app/src/main/java/com/example/ui/screens/settings/components/ThemeSelectionSheet.kt`
  - `app/src/main/java/com/example/ui/screens/HistoryScreen.kt`
  - `app/src/main/java/com/example/ui/screens/OnboardingScreen.kt`
  - `app/src/main/java/com/example/service/StepNotificationBuilder.kt`
- **Implementation Scope:**
  1. Replaced all raw English strings with `stringResource(...)`.
  2. Automated AST regex audit reports 0 hardcoded strings across all Compose UI files.
  3. Preserved 100% of Compose test tags.
- **Acceptance Criteria:** Every screen, row, card, dialog, sheet, and notification string is localized.

---

### Task 8.4: Dynamic Real-Time Compose Locale Recomposition in MainActivity
- **Status:** `COMPLETED`
- **Dependencies:** Task 8.3
- **Files Affected:**
  - `app/src/main/java/com/example/MainActivity.kt`
  - `app/src/main/java/com/example/util/LocaleHelper.kt`
- **Implementation Scope:**
  1. `LocaleHelper.wrap` explicitly resolves and sets target locale and layout direction for SYSTEM, English, and Arabic.
  2. `MainActivity.kt` provides `LocalContext`, `LocalConfiguration`, and `LocalLayoutDirection` via `CompositionLocalProvider`.
  3. Dynamic real-time recomposition updates all UI text on language switch without requiring app restart.
- **Acceptance Criteria:** Changing language in Settings immediately updates all screens in real-time.

---

### Task 8.5: Verification & Automated Regression Testing
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 8.1 – 8.4
- **Files Affected:**
  - `app/src/test/java/com/example/LocalizationParityTest.kt`
- **Implementation Scope:**
  1. Added `LocalizationParityTest` with 10 comprehensive Robolectric test cases covering registries, tiers, themes, sensors, wrapping, directionality, tokens, UiText, and MetricFormatter.
  2. Deleted duplicate mipmap launcher PNGs resolving packageDebugResources resource collision.
- **Acceptance Criteria:** Build compiles cleanly, all tests pass.

---

### Task 8.6: Universal Design Tokens & Zero-Hardcoding Architecture
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 8.1 – 8.5
- **Files Affected:**
  - `app/src/main/java/com/example/ui/theme/tokens/AppSpacing.kt`
  - `app/src/main/java/com/example/ui/theme/tokens/AppDimensions.kt`
  - `app/src/main/java/com/example/ui/theme/tokens/AppShapes.kt`
  - `app/src/main/java/com/example/ui/theme/tokens/AppDesignTokens.kt`
  - `app/src/main/java/com/example/ui/theme/Theme.kt`
  - `app/src/main/java/com/example/util/UiText.kt`
  - `app/src/main/java/com/example/util/MetricFormatter.kt`
- **Implementation Scope:**
  1. Implemented centralized `AppSpacing`, `AppDimensions`, `AppShapes`, and `AppDesignTokens` accessible via `MaterialTheme.spacing`, `MaterialTheme.dimens`, `MaterialTheme.appShapes`.
  2. Created `UiText` sealed class for decoupled, context-free text modeling.
  3. Created `MetricFormatter` for localized number, step, distance, calorie, duration, and percentage formatting.
  4. Refactored UI components to consume tokens and formatters.
- **Acceptance Criteria:** Zero hardcoded dp dimensions, corner shapes, numbers, or units in refactored UI components.

---

## Milestone 9: TrueStep Retention & UX Engagement Architecture

### Task 9.1: Retention & Engagement Domain & Localization
- **Status:** `COMPLETED`
- **Dependencies:** Milestone 8
- **Files Affected:**
  - `app/src/main/res/values/strings.xml`
  - `app/src/main/res/values-ar/strings.xml`
  - `app/src/main/java/com/example/domain/content/QuestRegistry.kt`
  - `app/src/main/java/com/example/domain/model/DailyQuest.kt`
  - `app/src/main/java/com/example/domain/model/PaceZone.kt`
- **Implementation Scope:**
  1. Defined 100% of strings for Journey tabs, Streak Vault, Daily Mini-Quests, World Trail Expeditions, Cadence SPM, Milestone Celebration, and Home Daily Briefings in both English and Arabic with exact 1:1 key parity (571 lines).
  2. Built `QuestRegistry` defining 5 daily mini-quests (Morning, Brisk, Midday, Target Master, Twilight Trek) with localized titles, descriptions, and XP rewards.
  3. Built `PaceZone` model mapping cadence SPM to zones (Stroll, Moderate, Brisk, Power).
- **Acceptance Criteria:** Zero raw hardcoded strings; domain models decoupled from language.

---

### Task 9.2: DataStore Persistence & Reactive ViewModel State
- **Status:** `COMPLETED`
- **Dependencies:** Task 9.1
- **Files Affected:**
  - `app/src/main/java/com/example/data/preferences/UserPreferencesRepository.kt`
  - `app/src/main/java/com/example/ui/viewmodel/StepViewModel.kt`
- **Implementation Scope:**
  1. Connected DataStore preferences for `bankedStreakFreezes`, `lastFreezeUsedDate`, `completedQuestIds`, and `questDate`.
  2. Implemented `journeyFlow` in `StepViewModel` combining streak freezes, quest tracking, cadence SPM, and milestone celebrations into `uiState`.
  3. Added helper methods: `dismissCelebration()`, `triggerCelebration()`, `updateCadence()`, `useStreakFreeze()`, `completeQuest()`, and `shareProgress()`.
- **Acceptance Criteria:** State is reactive, persistent, and survives process death.

---

### Task 9.3: Journey Screen & Modular Engagement Components
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 9.1, 9.2
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/journey/components/DailyQuestCard.kt`
  - `app/src/main/java/com/example/ui/screens/journey/components/StreakVaultCard.kt`
  - `app/src/main/java/com/example/ui/screens/journey/components/ExpeditionTrailCard.kt`
  - `app/src/main/java/com/example/ui/screens/journey/components/TrophyCabinetView.kt`
  - `app/src/main/java/com/example/ui/screens/JourneyScreen.kt`
  - `app/src/main/java/com/example/ui/navigation/AppNavigation.kt`
  - `app/src/main/java/com/example/ui/navigation/Screen.kt`
- **Implementation Scope:**
  1. Created `DailyQuestCard` with progress bar, XP reward badge, and checkmark state.
  2. Created `StreakVaultCard` displaying current streak, best streak, and banked freeze shields (max 2) with status hints.
  3. Created `ExpeditionTrailCard` converting lifetime km into world trails from `DestinationRegistry` with progress bar and completed trail milestones.
  4. Created `TrophyCabinetView` showcasing all 16 achievements with locked/unlocked statuses and requirements.
  5. Built `JourneyScreen` with primary tab row (Quests, Expeditions, Streak Vault, Trophies) and wired `Screen.Journey` into `AppNavigation`.
- **Acceptance Criteria:** Clean navigation, testTags on all tabs and components, 48dp touch targets.

---

### Task 9.4: Live Cadence SPM & Brisk Pace Zone Coaching
- **Status:** `COMPLETED`
- **Dependencies:** Task 9.1
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/WalkingSessionSheet.kt`
- **Implementation Scope:**
  1. Integrated real-time cadence calculation (`(sessionSteps / elapsedSeconds) * 60`).
  2. Added color-coded `PaceZone` indicator pill (Stroll, Moderate, Brisk, Power Walk).
  3. Added live brisk minutes tracker and included average cadence SPM and brisk minutes in session completion summary.
- **Acceptance Criteria:** Real-time intensity feedback during workouts without sensor battery drain.

---

### Task 9.5: Milestone Celebration Dialog & Shareable Card
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 9.1 – 9.3
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/CelebrationDialog.kt`
  - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
- **Implementation Scope:**
  1. Built `CelebrationDialog` displaying congratulations, steps, distance, streak, and "Share Progress Card" button.
  2. Implemented `shareProgress()` dispatching Android system share sheet with privacy-safe milestone stats.
  3. Integrated Daily Habit Briefing & Quests Glance banner on `HomeScreen` with direct "View Journey" shortcut.
- **Acceptance Criteria:** Delightful milestone celebrations without leaking telemetry.

---

### Task 9.6: Automated Unit & Robolectric Compose Verification
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 9.1 – 9.5
- **Files Affected:**
  - `app/src/test/java/com/example/JourneyRetentionUnitTest.kt`
  - `app/src/test/java/com/example/JourneyScreenComposeTest.kt`
- **Implementation Scope:**
  1. Created `JourneyRetentionUnitTest` testing quest generation, completion thresholds, pace zones, and persistence logic.
  2. Created `JourneyScreenComposeTest` testing tab navigation, quest cards, streak vault, trophy showcase, and celebration dialog actions.
  3. Clean compilation via `compile_applet` and 100% test pass via `gradle :app:testDebugUnitTest`.
- **Acceptance Criteria:** Zero failures across all unit and Robolectric tests.

---

## Milestone 10: Brand Identity & App Logo Modernization (Approved Prototype 3)

### Task 10.1: Adaptive Launcher Icon & Multi-Density Raster Generation
- **Status:** `COMPLETED`
- **Dependencies:** User approval of Prototype 3 (Winged Cyber Stride)
- **Files Affected:**
  - `app/src/main/res/drawable/img_app_logo_winged.jpg`
  - `app/src/main/res/drawable/ic_launcher_foreground.xml`
  - `app/src/main/res/drawable/ic_launcher_background.xml`
  - `app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher.png`
  - `app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_launcher_round.png`
  - `app/src/main/java/com/example/ui/screens/SplashScreen.kt`
- **Implementation Scope:**
  1. Configured `ic_launcher_foreground.xml` safe-zone layer-list strictly centering the asset within 66dp.
  2. Matched `ic_launcher_background.xml` solid vector fill to `#0B1324`.
  3. Generated complete multi-density PNG mipmap suites with true circular alpha masking on `ic_launcher_round.png`.
  4. Updated `SplashScreen.kt` logo emblem card to present the approved high-resolution winged logo with matching dark backdrop.
- **Acceptance Criteria:** Clean compilation via `compile_applet`, all 33 unit and Robolectric tests pass.

---

## Milestone 11: Cybernetic Stride Evolution & Gamified Athletic HUD (Step Strike)

### Task 11.1: Cybernetic Stride Design System, Tokens & Theme Integration
- **Status:** `COMPLETED`
- **Dependencies:** Milestone 10
- **Files Affected:**
  - `app/src/main/res/values/strings.xml`
  - `app/src/main/res/values-ar/strings.xml`
  - `app/src/main/java/com/example/ui/theme/ThemeChoice.kt`
  - `app/src/main/java/com/example/ui/theme/ThemeRegistry.kt`
- **Implementation Scope:**
  1. Add `ThemeChoice.CYBERNETIC_STRIDE` ("Cybernetic Stride") with localized title and subtitle.
  2. Implement `cyberneticStrideDarkColors` and `cyberneticStrideLightColors` with deep midnight obsidian `#0F131D`, Hyper Cyan `#00F0FF`, Electric Mint `#4DFFB2` / `#10E599`, and Neon Violet `#7B61FF`.
  3. Implement radial atmospheric gradient background brush (`cyberneticStrideBg`) replicating the anime cyberpunk HUD aesthetic.
- **Acceptance Criteria:** Theme selectable in Settings; passes localization parity.

---

### Task 11.2: Hunter RPG Domain Layer & Local Persistence (Hero, Gear & Currencies)
- **Status:** `COMPLETED`
- **Dependencies:** Task 11.1
- **Files Affected:**
  - `app/src/main/java/com/example/domain/model/HunterProfile.kt` (new)
  - `app/src/main/java/com/example/domain/model/GearItem.kt` (new)
  - `app/src/main/java/com/example/domain/content/GearRegistry.kt` (new)
  - `app/src/main/java/com/example/data/preferences/UserPreferencesRepository.kt`
  - `app/src/main/java/com/example/ui/viewmodel/StepViewModel.kt`
- **Implementation Scope:**
  1. Define domain models: `HunterProfile`, `GearItem`, `GearSlot` (Footwear, Core, Exosuit, Chrono), `GearRarity`, `Currencies` (Sparks, Cores, Shards), and `EncounterChallenge`.
  2. Build `GearRegistry` with 6+ iconic items (Neon Phantom Stride X-1, Turbulent Zephyr Engine, Shibuya Nanoweave, Cyber Pacer Chrono, Vapor Glide, Hyper-Pulse Apex).
  3. Calculate Combat Power (CP) dynamically from level, streak, and gear stats.
  4. Persist equipped items, currencies, and hunter level in `UserPreferencesRepository`.
  5. Expose hunter profile and loadout state reactively via `StepViewModel`.
- **Acceptance Criteria:** Pure local persistence in DataStore/Room; 0 cloud dependencies; thread-safe state flows.

---

### Task 11.3: Active Walking Session Cockpit Upgrade (Hold-to-Complete & Live HUD)
- **Status:** `COMPLETED`
- **Dependencies:** Task 11.2
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/WalkingSessionSheet.kt`
- **Implementation Scope:**
  1. Implement Real-Time Cadence Supercharge Gauge with animated circular progress and SPM state indicator.
  2. Add Tactile "Hold to Complete" slider/button with progress animation to prevent accidental termination.
  3. Add Lap Split marker button with temporary toast feedback.
  4. Add In-Stride Encounter Challenge (Mini-Boss / Speed Check): e.g. "Phantom Pacer Encounter" with target cadence >115 SPM and countdown timer for bonus rewards.
- **Acceptance Criteria:** Smooth animation, touch target >= 48dp, safe workout termination.

---

### Task 11.4: Post-Workout Raid Summary & Grade S Crest Recap
- **Status:** `COMPLETED`
- **Dependencies:** Task 11.3
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/WorkoutSummarySheet.kt` (new)
  - `app/src/main/java/com/example/ui/components/WalkingSessionSheet.kt`
- **Implementation Scope:**
  1. Build a dedicated post-workout victory summary displaying Grade S/A/B ranking crest based on pace and duration.
  2. Display Kilometer Splits & Cadence bar chart with Overdrive badges.
  3. Display Itemized EXP Surge (+850 EXP breakdown) and Raid Loot Cache dropped (Sparks, Cores, Shards).
  4. Add Victory Hologram Share Card button.
- **Acceptance Criteria:** Interactive, celebratory, and integrates cleanly with workout completion flow.

---

### Task 11.5: Journey Screen "Hero & Loadout" Tab & Equipment Vault
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 11.2, 11.4
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/journey/components/HeroLoadoutView.kt` (new)
  - `app/src/main/java/com/example/ui/screens/JourneyScreen.kt`
- **Implementation Scope:**
  1. Add 5th tab `HERO` to `JourneyScreen` (Quests, Expeditions, Streak Vault, Trophies, Hero).
  2. Implement `HeroLoadoutView` featuring:
     - Hunter Profile Card with Avatar, Level, Title, Combat Power (CP), and Currencies.
     - 4-Slot Active Loadout Paperdoll (Footwear, Core, Exosuit, Chrono).
     - Cybernetic Forge enhance banner.
     - Filterable Storage Vault bento grid (All, Footwear, Cores, Boosts, Materials).
     - Tactical Gear Inspection Drawer with stat tuning and equipping actions.
- **Acceptance Criteria:** Accessible M3 components, 48dp touch targets, clean integration with existing JourneyScreen tabs.

---

### Task 11.6: Home Dashboard Enhancements & Cadence Peak Insights
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 11.1, 11.2
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
- **Implementation Scope:**
  1. Add Hunter status chip (Level, Title, CP) to Home header.
  2. Add Mana Overdrive / Aura Multiplier state banner.
  3. Add Cadence Peak Insight callout under the hourly timeline.
  4. Upgrade start walk CTA with high-energy "Start Live Stride [GPS HUD]" design.
- **Acceptance Criteria:** Responsive layout, seamless RTL/LTR support, preserves all existing testTags.

---

### Task 11.7: Localization Parity, Unit & Compose UI Test Suite
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 11.1 – 11.6
- **Files Affected:**
  - `app/src/main/res/values/strings.xml`
  - `app/src/main/res/values-ar/strings.xml`
  - `app/src/test/java/com/example/CyberneticStrideUnitTest.kt` (new)
  - `app/src/test/java/com/example/HeroLoadoutComposeTest.kt` (new)
  - `app/src/test/java/com/example/LocalizationParityTest.kt`
  - `app/src/test/java/com/example/ExampleRobolectricTest.kt`
  - `app/src/test/java/com/example/JourneyScreenComposeTest.kt`
- **Implementation Scope:**
  1. Full Arabic and English 1:1 string parity for all new strings (gear, perks, loot, raid grades, hunter stats).
  2. Comprehensive unit tests for CP formula, gear equipping, and EXP progression.
  3. Robolectric Compose tests for HeroLoadoutView, Hold-to-Complete, and Raid Summary.
  4. Verification via `compile_applet` (clean build) and `gradle :app:testDebugUnitTest` (all unit and Compose tests passed with 0 failures).
- **Acceptance Criteria:** 0 compile errors, 100% test pass across entire test suite.

---

## Milestone 12: TrueStep UX Architecture Realignment & Name Consistency

### Task 12.1: Complete Name Purge & TrueStep Brand Consistency
- **Status:** `COMPLETED`
- **Dependencies:** None
- **Files Affected:**
  - `app/src/main/res/xml/backup_rules.xml`
  - `app/src/main/res/xml/data_extraction_rules.xml`
  - `app/proguard-rules.pro`
  - `docs/ASO_PLAY_STORE_METADATA.md`
  - `README.md`
  - `app/src/test/java/com/example/TrueStepComprehensiveTest.kt` (renamed from StrideTrustComprehensiveTest.kt)
  - `docs/plan.md`, `docs/task.md`, `docs/memory.md`, `docs/implementation_plan.md`
- **Implementation Scope:**
  1. Remove all lingering references to the legacy working name across XML comments, ProGuard rules, documentation, and metadata.
  2. Rename `StrideTrustComprehensiveTest.kt` to `TrueStepComprehensiveTest.kt` and update test class definitions.
  3. Ensure 100% brand consistency under **TrueStep** across all project artifacts.
- **Acceptance Criteria:** Zero occurrences of legacy name in active project files.

---

### Task 12.2: Home Dashboard De-cluttering (Remove RPG/Fantasy Artifacts)
- **Status:** `COMPLETED`
- **Dependencies:** Task 12.1
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
- **Implementation Scope:**
  1. Remove `home_hunter_header_card` (Hunter Level, Ren Amamiya, Combat Power CP).
  2. Remove `home_aura_overdrive_banner` (Mana Overdrive, Aura Multiplier).
  3. Streamline `home_daily_briefing_card` to focus on peaceful habit guidance without arcade quest progress injection.
  4. Reclaim ~220dp of vertical viewport space to restore TrueStep's calm, focused habit tracking.
- **Acceptance Criteria:** Dashboard reflects a clean, trustworthy, non-judgmental pedometer without gaming distractions.

---

### Task 12.3: Hero Step Ring Interaction Safety (Eliminate Accidental Tap-to-Pause)
- **Status:** `COMPLETED`
- **Dependencies:** Task 12.2
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/HeroStepRing.kt`
  - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
- **Implementation Scope:**
  1. Remove full 200dp clickable toggle on the central step ring that causes accidental pausing during vertical scroll drags.
  2. Transition `HeroStepRing` to a purely informative, beautifully rendered telemetry ring.
  3. Move pause/resume action to a deliberate, explicit action affordance (e.g. discreet icon button or dedicated control row) with confirmation feedback.
  4. Ensure `PausedBanner` appears prominently near the top of the Home screen whenever tracking is paused rather than being buried at the bottom.
- **Acceptance Criteria:** Zero accidental pauses during scrolling; pause state clearly visible without scrolling.

---

### Task 12.4: Above-the-Fold Metric KPI Grid Elevation
- **Status:** `COMPLETED`
- **Dependencies:** Task 12.3
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
  - `app/src/main/java/com/example/ui/components/HomeKpiGrid.kt`
- **Implementation Scope:**
  1. Move `HomeKpiGrid` directly underneath the Hero Step Ring / Trust Status Strip.
  2. Add active movement duration (Active Minutes) to the core KPI metrics so users see Steps, Distance, Active Time, and Calories at a glance.
  3. Ensure all 4 primary metrics are visible above the fold on standard phone screen dimensions (no scrolling required).
- **Acceptance Criteria:** Glancable daily stats available immediately upon opening the app.

---

### Task 12.5: Trust Card Touch Target & Accessibility Refactoring
- **Status:** `COMPLETED`
- **Dependencies:** Task 12.4
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
- **Implementation Scope:**
  1. Eliminate the nested clickable conflict inside `today_trust_status_card` (outer card opening Trust Center while inner row opens Goals).
  2. Separate the Trust & Transparency status from the Goal Tiers overview into two distinct, accessible cards.
  3. Give the Goal Tiers card a clear trailing chevron affordance (`>`) indicating that tapping navigates to the Goals screen.
- **Acceptance Criteria:** Clean accessibility tree (TalkBack friendly), zero touch target conflicts.

---

### Task 12.6: Hourly Scrubber & History Preview Polish
- **Status:** `COMPLETED`
- **Dependencies:** Task 12.5
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/HourlyScrubRibbon.kt`
  - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
  - `app/src/main/java/com/example/ui/components/HistoryPreviewChart.kt`
- **Implementation Scope:**
  1. Add a prominent floating or sticky banner when an hour is selected: `"Viewing 14:00 (620 steps) • [Return to Full Day]"`.
  2. Refine the Home screen's embedded historical chart to a compact 7-day sparkline preview with a *"View Full History →"* link to the dedicated History tab, preventing excessive 2500dp scroll bloat.
- **Acceptance Criteria:** Effortless exit from scrub mode; glanceable Home page without endless scrolling.

---

### Task 12.7: Walking Session Cockpit Refinement
- **Status:** `COMPLETED`
- **Dependencies:** Task 12.6
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/WalkingSessionSheet.kt`
- **Implementation Scope:**
  1. Clean out the "Mini-Boss / Encounter" and "Banked Sparks 💎" elements from the active walking cockpit.
  2. Focus telemetry strictly on biometrics: Duration, Distance, Cadence (SPM), Pace, and Calories.
  3. Replace the gamified "Hold to Complete" with a standard, accessible Pause / Resume / Finish control set with confirmation.
- **Acceptance Criteria:** Professional, calm walking cockpit focused on real physical health telemetry.

---

### Task 12.8: Automated Verification & Unit/Compose Test Suite Pass
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 12.1 – 12.7
- **Files Affected:**
  - `app/src/test/java/com/example/TrueStepComprehensiveTest.kt`
  - `app/src/test/java/com/example/StepTrackComposeUiTest.kt`
- **Implementation Scope:**
  1. Update existing UI and Compose tests to reflect the streamlined Home screen layout and removed RPG artifacts.
  2. Run `compile_applet` and verify 0 compiler warnings/errors.
  3. Run `gradle :app:testDebugUnitTest` and verify 100% test pass.
- **Acceptance Criteria:** Clean build and 0 test failures.

---

## Milestone 13: Gamified Athletic HUD & Cybernetic Experience Alignment (USER APPROVED)

### Task 13.1: Resolution of Mipmap Resource Collision & Default Theme Setting
- **Status:** `COMPLETED`
- **Dependencies:** User approval of `temp/02_gamified_athletic_tracker.jpg`
- **Files Affected:**
  - `app/src/main/res/mipmap-*`
  - `app/src/main/java/com/example/data/preferences/UserPreferencesRepository.kt`
- **Implementation Scope:**
  1. Removed duplicate `.webp` launcher files from mipmap resource folders resolving `packageDebugResources` build failure.
  2. Configured `UserPreferencesRepository` so the default active theme is `ThemeChoice.CYBERNETIC_STRIDE` (obsidian, hyper cyan, electric mint).
- **Acceptance Criteria:** `compile_applet` clean build; out-of-the-box Cybernetic Stride theme.

---

### Task 13.2: Elevate Journey to Primary Navigation
- **Status:** `COMPLETED`
- **Dependencies:** Task 13.1
- **Files Affected:**
  - `app/src/main/java/com/example/ui/navigation/Screen.kt`
- **Implementation Scope:**
  1. Added `Screen.Journey` to `Screen.navItems` (`Home`, `Journey`, `History`, `Goals`, `Settings`).
  2. Direct bottom navigation access to Quests, Hero Loadout, Expeditions, Streak Vault, and Trophies.
- **Acceptance Criteria:** One-tap navigation to all gamified features in bottom navigation bar.

---

### Task 13.3: Home Dashboard Gamified Athletic HUD Integration
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 13.1, 13.2
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
- **Implementation Scope:**
  1. Integrated `home_hunter_header_card` displaying Hunter Level, Avatar, Rank Title, Combat Power (CP), and Currencies (💎 Sparks, ⚙️ Cores, 💠 Shards).
  2. Integrated `home_aura_overdrive_banner` showing Mana Overdrive state and streak multiplier (1.25x Active).
  3. Upgraded workout CTA with high-energy "⚡ START LIVE STRIDE [GPS HUD]" button.
- **Acceptance Criteria:** Preserves 100% of test tags and matches `temp/02_gamified_athletic_tracker.jpg` design.

---

### Task 13.4: Active Workout Cockpit & Raid Victory Summary
- **Status:** `COMPLETED`
- **Dependencies:** Task 13.3
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/WalkingSessionSheet.kt`
  - `app/src/main/java/com/example/ui/components/WorkoutSummarySheet.kt`
- **Implementation Scope:**
  1. Integrated In-Stride Encounter Challenge card ("Phantom Pacer Encounter") with live optimal zone detection.
  2. Real-time Cadence Supercharge circular dial (>115 SPM with glowing cyan arc).
  3. Integrated post-workout Grade S/A/B Raid Victory Summary with EXP surge and loot drops.
- **Acceptance Criteria:** Safe workout finish, exciting telemetry, local inventory rewards banking.

---

## Milestone 14: Solo Leveling System Stat Allocator, Holographic Blue HUD & Option C Cyber-Gates (USER APPROVED)

### Task 14.1: Data Layer & Persistence Pipeline for Hunter Stat Points & Option C Registry
- **Status:** `COMPLETED`
- **Dependencies:** User approval of Option C (`temp/option_C_hybrid_arcade_cyber_gates.jpg`) & implementation plan
- **Files Affected:**
  - `app/src/main/java/com/example/domain/model/HunterProfile.kt`
  - `app/src/main/java/com/example/domain/content/GearRegistry.kt`
  - `app/src/main/java/com/example/data/preferences/UserPreferencesRepository.kt`
  - `app/src/main/java/com/example/ui/viewmodel/StepViewModel.kt`
- **Implementation Scope:**
  1. Verified and wired reactive DataStore attributes (`hunterStrength`, `hunterAgility`, `hunterVitality`, `hunterSense`, `availableStatPoints`) to `StepViewModel`.
  2. Implemented `allocateStatPoint(statType)` in `StepViewModel` and atomic decrement/increment in DataStore.
  3. Dynamic Combat Power calculation incorporating hunter attributes into live state.
- **Acceptance Criteria:** `allocateStatPoint` persists to DataStore and dynamically increases CP.

---

### Task 14.2: Interactive Solo Leveling Hunter Attribute Terminal in HeroLoadoutView
- **Status:** `COMPLETED`
- **Dependencies:** Task 14.1
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/journey/components/HeroLoadoutView.kt`
  - `app/src/main/java/com/example/ui/screens/JourneyScreen.kt`
- **Implementation Scope:**
  1. Replaced static text row in `HeroLoadoutView.kt` with an interactive Attribute Matrix:
     - Available Stat Points indicator badge with glowing cyan pulse.
     - 4 Attributes: STR (Strength), AGI (Agility), VIT (Vitality), SEN (Sense).
     - Tactile `[+]` allocate buttons with haptic feedback, enabled when `availableStatPoints > 0`.
     - Stat description labels explaining in-game perks.
- **Acceptance Criteria:** User can tap `[+]` on any stat to allocate points; points decrement and stat increases immediately.

---

### Task 14.3: Option C Hybrid Arcade Cyber-Gate Expeditions Component
- **Status:** `COMPLETED`
- **Dependencies:** Task 14.1
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/journey/components/ExpeditionTrailCard.kt`
- **Implementation Scope:**
  1. Upgraded `ExpeditionTrailCard.kt` to mirror Option C (`temp/option_C_hybrid_arcade_cyber_gates.jpg`):
     - S-Rank Cyber-Gate 04 Portal with dimensional vortex and glow lines.
     - Chamber Depth Progression (e.g. Chamber 1/4: The Depths -> Chamber 4/4: Core).
     - Arcade Combo multiplier badge (`COMBO X48`).
     - Loot Cache Grid: Legendary Core (S), Arcane Data Chip, Mystic Dust, Cybernetic Fragment.
- **Acceptance Criteria:** Matches user-approved Option C visual design and telemetry.

---

### Task 14.4: Translucent Holographic Blue System Window Dialog
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 14.1 – 14.3
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/SystemHologramDialog.kt`
  - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
  - `app/src/main/java/com/example/ui/screens/JourneyScreen.kt`
- **Implementation Scope:**
  1. Created `SystemHologramDialog.kt`:
     - Solo Leveling electric-blue holographic window with dark obsidian backdrop and cyan glowing border.
     - Chamfered technical corners, `[SYSTEM NOTICE]` header, sound/vibration cue.
     - Action buttons: "CLAIM REWARDS", "ACCEPT CONTRACT", "DISMISS".
  2. Wired to `StepViewModel`'s `showSystemDialog`, triggering for Level Up, Daily Quest arrival, and Gate breaches.
- **Acceptance Criteria:** Holographic system windows appear for major milestone events and dismiss cleanly.

---

### Task 14.5: Localization Parity & Test Suite Verification
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 14.1 – 14.4
- **Files Affected:**
  - `app/src/main/res/values/strings.xml`
  - `app/src/main/res/values-ar/strings.xml`
  - `app/src/test/java/com/example/*`
- **Implementation Scope:**
  1. Verified 100% Arabic and English string parity across all attributes, chamber depths, loot items, and system notices.
  2. Added unit tests for stat allocation and Compose tests for Attribute Terminal and System Dialog.
  3. Validated with `compile_applet` and `gradle :app:testDebugUnitTest`.
- **Acceptance Criteria:** 0 compile errors, 100% test pass.

---

## Milestone 15: Homepage UI/UX Remediation & Fluid Responsive Architecture (Master Plan)

### Task 15.0: Resolve Resource Collision & Prune Duplicate Mipmap WebP Files
- **Status:** `COMPLETED`
- **Dependencies:** None
- **Files Affected:**
  - `app/src/main/res/mipmap-mdpi/{ic_launcher,ic_launcher_round}.webp`
  - `app/src/main/res/mipmap-hdpi/{ic_launcher,ic_launcher_round}.webp`
  - `app/src/main/res/mipmap-xhdpi/{ic_launcher,ic_launcher_round}.webp`
  - `app/src/main/res/mipmap-xxhdpi/{ic_launcher,ic_launcher_round}.webp`
  - `app/src/main/res/mipmap-xxxhdpi/{ic_launcher,ic_launcher_round}.webp`
- **Implementation Scope:**
  1. Removed legacy duplicate `.webp` icons across all mipmap density directories resolving `packageDebugResources` duplicate resource collision.
- **Acceptance Criteria:** `packageDebugResources` task succeeds with 0 duplicate resource errors.

### Task 15.1: Action Buttons & Telemetry Card Fluid Layout & String Parity
- **Status:** `COMPLETED`
- **Dependencies:** Task 15.0
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
  - `app/src/main/res/values/strings.xml`
  - `app/src/main/res/values-ar/strings.xml`
- **Implementation Scope:**
  1. Maintained stacked full-width Action Buttons (52dp primary + 44dp secondary).
  2. Extracted localized string resources for `home_initiate_stride_raid`, `home_hunter_dossier`, `home_open_gear_vault`, `home_active_contracts`, `home_loot_vault`, and `home_mana_overdrive_engaged` in both English and Arabic.
  3. Enforced flex weights (`Modifier.weight(1f, fill = false)`) and `TextOverflow.Ellipsis` on hunter dossier and telemetry rows to eliminate horizontal squishing on narrow viewports.
- **Acceptance Criteria:** Zero hardcoded strings in Action Buttons and header cards; zero horizontal clipping.

### Task 15.2: Hero Step Ring Radial Readout Auto-Fit & Localization
- **Status:** `COMPLETED`
- **Dependencies:** Task 15.1
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/HeroStepRing.kt`
  - `app/src/main/res/values/strings.xml`
  - `app/src/main/res/values-ar/strings.xml`
- **Implementation Scope:**
  1. Validated step count auto-scaling typography (32sp / 28sp / 24sp).
  2. Localized `telemetry_standby`, `telemetry_arc_reactor`, `overdrive_charge_badge`, and `charge_badge_format` with 1:1 Arabic parity.
  3. Enforced `maxLines = 1`, `softWrap = false`, `overflow = TextOverflow.Ellipsis` on inner chord pause hint.
- **Acceptance Criteria:** Hero Step Ring inner text never wraps or clips across all screen sizes and locales.

### Task 15.3: Home KPI Grid & Hourly Ribbon Legend Symmetry
- **Status:** `COMPLETED`
- **Dependencies:** Task 15.2
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/HourlyScrubRibbon.kt`
- **Implementation Scope:**
  1. Added `maxLines = 1`, `overflow = TextOverflow.Ellipsis`, and `Modifier.weight(1f, fill = false)` to the bottom legend row in `HourlyScrubRibbon.kt`.
  2. Ensured Start of Day and End of Day labels gracefully fit in both RTL (Arabic) and LTR (English).
- **Acceptance Criteria:** Legend labels never break into 2 lines.

### Task 15.4: Journey Screen ScrollableTabRow Migration
- **Status:** `COMPLETED`
- **Dependencies:** Task 15.3
- **Files Affected:**
  - `app/src/main/java/com/example/ui/screens/JourneyScreen.kt`
- **Implementation Scope:**
  1. Replaced fixed `PrimaryTabRow` with `ScrollableTabRow(edgePadding = 16.dp)`.
  2. Provided comfortable horizontal padding (4dp) and `maxLines = 1`, `softWrap = false` per tab to eliminate vertical label breaking in all languages.
  3. Preserved all 5 testTag IDs (`tab_quests`, `tab_hero`, `tab_expeditions`, `tab_streak_vault`, `tab_trophies`) and `journey_tab_row`.
- **Acceptance Criteria:** Tab titles render on single line with smooth horizontal scrolling.

### Task 15.5: Automated Verification & Unit/Robolectric Test Pass
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 15.0 – 15.4
- **Files Affected:**
  - `app/src/test/java/com/example/*`
- **Implementation Scope:**
  1. Ran `gradle :app:testDebugUnitTest` and achieved 100% pass across all unit and Robolectric tests (33 test tasks, 0 failures).
  2. Ran `compile_applet` and verified clean build with 0 warnings or errors.
- **Acceptance Criteria:** 0 compile errors, 100% test pass.

---

## Milestone 16: Step Ring Full-Surface Pause & Resume Interaction

### Task 16.1: Full-Surface Clickable Step Ring Dial in HeroStepRing
- **Status:** `COMPLETED`
- **Dependencies:** Milestone 15
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/HeroStepRing.kt`
- **Implementation Scope:**
  1. Attached `Modifier.clickable(onClick = onTogglePause, role = Role.Button, onClickLabel = ...)` to the central 196.dp circular dial container (`hero_ring_dial`).
  2. Verified tapping anywhere within the steps ring (numbers, circular arc, spark bead, background, or text chord) toggles tracking pause/resume with a circular ripple.
  3. Removed redundant inner `.clickable` from `ring_pause_hint` to eliminate nested clickable accessibility violations while preserving `.testTag("ring_pause_hint")`.
  4. Preserved all test tags (`hero_step_ring_card`, `hero_ring_toggle`, `hero_ring_dial`, `ring_step_count_text`, `ring_pause_hint`).
- **Acceptance Criteria:** User tapping anywhere within the circular steps ring toggles pause/resume; TalkBack announces clear action label; zero nested clickable warnings.

### Task 16.2: Compose UI Verification & Regression Test Suite
- **Status:** `COMPLETED`
- **Dependencies:** Task 16.1
- **Files Affected:**
  - `app/src/test/java/com/example/StepTrackComposeUiTest.kt`
- **Implementation Scope:**
  1. Added Compose test `testHeroStepRingEntireDialTogglesPause` verifying that clicking `hero_ring_dial` invokes `onTogglePause`.
  2. Verified existing tests for `hero_ring_toggle` and UI elements continue to pass.
  3. Ran `gradle :app:testDebugUnitTest` (all tests passed with 0 failures) and `compile_applet` (BUILD SUCCESSFUL).
- **Acceptance Criteria:** Clean build and 100% test pass.

---

## Milestone 17: Production Readiness Audit & Google Play Store Publishing Certification

### Task 17.1: Resource Compilation & Multi-Density Raster PNG Signature Fix
- **Status:** `COMPLETED`
- **Dependencies:** None
- **Files Affected:**
  - `app/src/main/res/mipmap-mdpi/{ic_launcher.png, ic_launcher_round.png}`
  - `app/src/main/res/mipmap-hdpi/{ic_launcher.png, ic_launcher_round.png}`
  - `app/src/main/res/mipmap-xhdpi/{ic_launcher.png, ic_launcher_round.png}`
  - `app/src/main/res/mipmap-xxhdpi/{ic_launcher.png, ic_launcher_round.png}`
  - `app/src/main/res/mipmap-xxxhdpi/{ic_launcher.png, ic_launcher_round.png}`
- **Implementation Scope:**
  1. Diagnosed AAPT2 resource compilation error in `mergeReleaseResources`.
  2. Identified invalid byte signatures (`\xEF\xBF\xBD` UTF-8 replacement chars instead of `\x89PNG`).
  3. Executed ImageMagick pipeline defined in `app-icon-generation` skill to regenerate all 5 mipmap densities as pristine 32-bit RGBA PNGs (`ic_launcher.png`) with circular alpha masking (`ic_launcher_round.png`) from master vector asset `img_app_logo_winged.jpg`.
  4. Verified `od -t x1 -N 16` confirms valid `89 50 4E 47` signature.
- **Acceptance Criteria:** `mergeReleaseResources` succeeds cleanly with 0 AAPT2 errors.

### Task 17.2: Play Store Policy Anti-Review-Gating Alignment
- **Status:** `COMPLETED`
- **Dependencies:** Task 17.1
- **Files Affected:**
  - `app/src/main/java/com/example/ui/components/RatingReviewDialog.kt`
- **Implementation Scope:**
  1. Refactored `RatingReviewDialog` to guarantee unhindered access to "Rate on Google Play" (`rate_google_play_button`) regardless of star selection, eliminating review-filtering / gating violations under Google Play developer policies.
  2. Provided constructive private feedback button (`send_feedback_button`) as an optional secondary action when selecting < 4 stars.
- **Acceptance Criteria:** Zero policy flags for review gating; 100% preservation of Compose test tags.

### Task 17.3: Defensive Foreground Service Lifecycle & Sequential Runtime Permissions
- **Status:** `COMPLETED`
- **Dependencies:** Task 17.1
- **Files Affected:**
  - `app/src/main/java/com/example/service/StepCounterForegroundService.kt`
  - `app/src/main/java/com/example/MainActivity.kt`
- **Implementation Scope:**
  1. Added defensive `try { ... } catch (_: Exception)` in `StepCounterForegroundService.start(context)` to protect against `ForegroundServiceStartNotAllowedException` and `SecurityException` on Android 12+ / 14+.
  2. Added fallback sequential `activityRecognitionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)` in `MainActivity`'s onboarding completion effect when permissions were bypassed.
- **Acceptance Criteria:** Zero unhandled crashes during background/foreground service state transitions.

### Task 17.4: Full Release Binary & Android App Bundle (.aab) Verification
- **Status:** `COMPLETED`
- **Dependencies:** Tasks 17.1 – 17.3
- **Files Affected:**
  - `app/build/outputs/apk/release/app-release.apk`
  - `app/build/outputs/bundle/release/app-release.aab`
- **Implementation Scope:**
  1. Verified clean release APK compilation (`gradle :app:assembleRelease`) with R8 minification, resource shrinking, and ProGuard optimization.
  2. Verified clean Android App Bundle generation (`gradle :app:bundleRelease`) creating `app-release.aab` (10.2 MB) ready for direct Google Play Console upload.
  3. Ran automated test suite (`gradle :app:testDebugUnitTest`) passing all 33 tasks with 0 failures.
  4. Ran `compile_applet` confirming pristine build state.
- **Acceptance Criteria:** Both `.apk` and `.aab` compile and sign cleanly; 100% test pass.





