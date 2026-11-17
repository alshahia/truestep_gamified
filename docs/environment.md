# Environment Report

## Overview
- **Operating System:** Linux (Containerized cloud Android build environment)
- **Architecture:** x86_64
- **Working Directory:** `/` (Project Root)
- **Framework:** Modern Android (Kotlin + Jetpack Compose)
- **Compile SDK:** Android 36 (VanillaIceCream)
- **Min SDK:** 26 (Android 8.0 Oreo)
- **Build System:** Gradle (Kotlin DSL, `build.gradle.kts`) with Version Catalog (`libs.versions.toml`)
- **Annotation Processing:** KSP (Kotlin Symbol Processing)

## Tooling & Dependencies
- **UI Toolkit:** Jetpack Compose (BOM managed, Material 3)
- **State Management:** MVVM with Kotlin Coroutines (`StateFlow`, `Flow`, `viewModelScope`)
- **Local Persistence:** Room Database (`StepDatabase`) with KSP compiler (Version 3 with safe migrations)
- **User Preferences:** Jetpack DataStore Preferences (`androidx.datastore.preferences`)
- **Hardware Telemetry:** `SensorManager` (`TYPE_STEP_COUNTER`, `TYPE_STEP_DETECTOR`, `TYPE_ACCELEROMETER` fallback) with 10s hardware FIFO batching
- **Background Tracking:** Android Foreground Service with Health type (`StepCounterForegroundService`), category workout, and pause/resume action
- **Health Integration:** Health Connect API (`androidx.health.connect`)
- **Background Tasks:** Android WorkManager (`androidx.work.runtime.ktx`)
- **App Icons & Assets:** Production-grade adaptive vector icons + pure raster PNG density fallbacks without AAPT2 duplication
- **Backup & Migration:** Full backup and device transfer rules for Room database (`steptrack_database.db`) and DataStore preferences (`files/datastore`)
- **Google Play Readiness:** Release signing with environment upload key and local fallback, R8 optimization and code shrinking enabled, zero unused third-party dependencies

## Project Conventions
- **Naming Conventions:** Descriptive camelCase for Kotlin, snake_case for resources and compose testTags.
- **Insets & Edge-to-Edge:** `enableEdgeToEdge()` enabled; explicit handling of `WindowInsets.navigationBars` and `WindowInsets.statusBars`.
- **Localization:** Multilingual support (Arabic `ar` with Cairo/RTL, English `en` with Inter/LTR). Zero hardcoded UI strings.
- **Privacy & Telemetry:** 100% offline, local persistence, zero analytics or telemetry.
- **Settings Architecture:** Modular M3 structure (`com.example.ui.screens.settings.*`), isolated `SettingsUiState` (zero recomposition on step counting), modal bottom sheets for large selection sets, full testTag preservation.

## Available Verification Commands
- Unit & Robolectric Tests: `gradle :app:testDebugUnitTest`
- Compilation Check: `compile_applet`
- Release APK Assembly: `gradle :app:assembleRelease`
- Release App Bundle (AAB): `gradle :app:bundleRelease`

## Capabilities & Limitations
- **Emulator / ADB:** Not directly accessible via CLI; visual preview rendered via Streaming Android Emulator.
- **Testing:** Local JVM testing with Robolectric (SDK 34-36) and Compose UI Test Rule.
