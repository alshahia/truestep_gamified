# TrueStep — Privacy-First Offline Step Tracker & Pedometer

**TrueStep** is a calm, transparent, offline-first step counter for Android built with Kotlin and Jetpack Compose. TrueStep prioritizes trust over generic fitness metrics: it reveals exactly where every step came from, prevents double counting across phone sensors and smartwatches, and provides realistic adaptive goals that adjust sustainably to your actual routine.

---

## 🌟 Key Differentiators & Product Principles

1. **100% Offline-First & Privacy-First:**
   - No account, email, or cloud server required for normal functionality.
   - Zero telemetry, advertising SDKs, or remote tracking.
   - All step history, daily summaries, sessions, and streak calculations are stored locally in Room.
   - Complete local data deletion and portable CSV/JSON data export at any time.

2. **No Location Required for Normal Step Counting:**
   - Pedometer functionality strictly uses low-power hardware step sensors (`TYPE_STEP_COUNTER` with FIFO batching) and Health Connect.
   - Location is NEVER requested for normal daily step counting.
   - Optional GPS route tracking is strictly isolated to user-initiated Outdoor Walking Sessions, and falls back gracefully to sensor counting if location is denied.

3. **Transparent Data Sources & Trust Center:**
   - Real-time visibility into the data feed (Hardware pedometer, Health Connect, or Reconciled).
   - Conservative deduplication: if multiple sources report steps in overlapping intervals, the window is reconciled once to eliminate artificial inflation.
   - Visual confidence indicators (`High`, `Medium`, `Stale/Partial`).
   - Clear diagnostic feedback if background counting is restricted by OEM battery optimization.

4. **Adaptive Goals & Compassionate Habits:**
   - Three daily tiers: **Minimum Goal** (60% baseline), **Target Goal** (100% baseline), and **Stretch Goal** (120% baseline).
   - Reaching your Minimum Goal counts as a successful day and sustains your streak.
   - Adaptive smart goals adjust based on the 7-day median baseline with a conservative 5–10% weekly adjustment cap.
   - Designated Rest Days and temporary Recovery Mode protect streaks without guilt.

5. **Dedicated Walking Activity Sessions:**
   - Start, pause, resume, and record dedicated walking sessions (Indoor, Outdoor GPS/non-GPS, Treadmill).
   - Live session timer, cadence, step counter, and distance metrics.
   - Session history saved locally with personal journal notes.

6. **Clean State on Install (No Fake/Mock Data):**
   - The app installs completely clean with 0 steps and empty history.
   - Does not silently generate or invent fake historical data.
   - Optional demo data can be manually loaded from Settings for preview purposes only.

---

## 🏗️ Architecture & Technology Stack

- **UI Layer:** Jetpack Compose, Material 3, Single Activity architecture, Edge-to-Edge display with `enableEdgeToEdge()`.
- **Navigation:** 5 primary destinations (`Today`, `History`, `Goals`, `Trust Center`, `Settings`) plus sub-screens (`Virtual Walks & Milestones`).
- **Presentation:** MVVM (`StepViewModel`, `StateFlow`, `collectAsStateWithLifecycle`).
- **Domain Layer:** Clean Architecture repository interfaces (`StepRepository`), registries (`DestinationRegistry`, `MultiplierRegistry`, `AchievementRegistry`), reconciliation engine (`StepReconciliationEngine`).
- **Data Layer:** Room persistence (`StepDatabase` v3 with automated migrations for `DailySteps`, `HourlyStepRecord`, `GoalStreakCounter`, `AchievementUnlock`, `StepSourceRecord`, `UserGoalSettings`, `WalkingSession`).
- **Preferences:** Jetpack DataStore (`UserPreferencesRepository`) for persistent theme, carrying mode, and onboarding state.
- **Hardware Integration & 3-Tier Fallback:**
  - **Tier 1 (Primary):** Hardware Step Counter (`Sensor.TYPE_STEP_COUNTER`) with 10-second FIFO batching for minimal battery usage.
  - **Tier 2 (Secondary):** Hardware Step Detector (`Sensor.TYPE_STEP_DETECTOR`).
  - **Tier 3 (Universal Fallback):** 3-Axis Accelerometer (`Sensor.TYPE_ACCELEROMETER`) with real-time digital signal processing and refractory cadence gating (250ms–2000ms), calibrated to user carrying mode (Pocket, Hand, Bag, Backpack, Treadmill).
- **Background Synchronization:** `StepCounterForegroundService` (health foreground service type) and periodic `StepSyncWorker` via WorkManager.
- **Health Integration:** Official AndroidX Health Connect Client library with permission rationales and graceful fallbacks.
- **Export & Portability:** Instant CSV and JSON export via standard Android share sheet.

---

## 📂 Project Structure

```
app/src/main/java/com/example/
├── MainActivity.kt                      # Edge-to-edge entry point & permission launchers
├── StepTrackApplication.kt              # Application lifecycle & DB seeding
├── data/
│   ├── local/                          # Room entities (DailySteps, WalkingSession, etc.) & DAOs
│   ├── repository/                     # StepRepository implementation & delta tracking
│   ├── source/                         # StepDataSource abstractions (HealthConnect, PhoneSensor, Fake)
│   ├── sync/                           # HealthConnectHelper & StepSyncWorker (WorkManager)
│   ├── preferences/                    # UserPreferencesRepository (DataStore)
│   └── di/                             # ServiceLocator (manual DI)
├── domain/
│   ├── model/                          # Domain data classes (Destination, Achievement)
│   ├── content/                        # Registries (Destinations, Multipliers, Achievements)
│   ├── reconciliation/                 # StepReconciliationEngine (conservative deduplication)
│   └── repository/                     # StepRepository interface
├── sensor/
│   └── StepSensorManager.kt            # Hardware pedometer listener with FIFO batching & fallbacks
├── service/
│   ├── StepCounterForegroundService.kt # Low-power foreground tracking service
│   └── StepNotificationBuilder.kt     # Ongoing persistent notification builder
├── receiver/
│   └── BootReceiver.kt                 # Device boot & update receiver
├── ui/
│   ├── components/                     # HeroStepRing, WalkingSessionSheet, HourlyScrubRibbon, HomeKpiGrid
│   ├── screens/                        # HomeScreen (Today), HistoryScreen, GoalsScreen, TrustCenterScreen, SettingsScreen, OnboardingScreen, AnalyticsScreen
│   ├── theme/                          # 16 Handcrafted M3 themes, Type (tabular digits)
│   ├── navigation/                     # AppNavigation & Screen routes
│   └── viewmodel/                      # StepViewModel & StepViewModelFactory
```

---

## 🧪 Testing

The project includes unit tests, Robolectric tests, and Compose UI tests running on the local JVM:

- `TrueStepComprehensiveTest`: Tests all 12 core edge cases and requirements:
  1. Single source with one day of data.
  2. Phone and watch with non-overlapping data intervals.
  3. Phone and watch with overlapping intervals (deduplication & primary preference).
  4. Identical time ranges from multiple sources.
  5. Midnight boundary crossings.
  6. User timezone transitions.
  7. Health Connect permission denial handling.
  8. Missing hardware sensor fallback handling.
  9. Recovery mode goal reduction and streak preservation.
  10. Designated rest day streak preservation.
  11. Manual step adjustments.
  12. Stale data detection and diagnostic flags.
- `StepTrackUnitTest`: Verifies goal tiers (60%/100%/120%), median baseline calculations, 10% weekly adjustment limits, virtual landmark distance progress, and CSV/JSON export schema.
- `StepTrackComposeUiTest`: Verifies `HeroStepRing` toggle, `HomeScreen` trust status card, metrics KPI rendering, and `PauseButton` interaction.
- `ExampleRobolectricTest`: Verifies app name string resolution and all 16 theme configurations.

Run tests:
```bash
gradle :app:testDebugUnitTest
```
