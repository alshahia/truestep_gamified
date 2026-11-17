# TrueStep Retention & UX Architecture Implementation Plan

Elevate TrueStep into an engaging, habit-forming health companion built on trust, transparency, adaptive goals, and calm usability. Featuring a dedicated Journey & Quests hub, streak resilience, daily micro-walk missions, cadence pace zones, shareable progress cards, and contextual daily debriefs.

---

### Progress Tracker & Execution Status
- **Overall State:** `IN_PROGRESS`
- **Milestone 1: Localization & Domain Registries** — `[COMPLETED]`
- **Milestone 2: Persistence & ViewModel State** — `[COMPLETED]`
- **Milestone 3: UI Components & JourneyScreen** — `[COMPLETED]`
- **Milestone 4: Cadence Gauge & Walk Session Upgrade** — `[COMPLETED]`
- **Milestone 5: Celebration & Shareable Cards** — `[COMPLETED]`
- **Milestone 6: Automated Verification & Unit Tests** — `[COMPLETED]`
- **Milestone 11: Cybernetic Stride Evolution & Gamified Athletic HUD** — `[COMPLETED]`
- **Milestone 12: TrueStep UX Architecture Realignment & Name Consistency** — `[COMPLETED]`
- **Milestone 13: Gamified Athletic HUD & Cybernetic Experience Adaptation** — `[COMPLETED]`

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The user has officially approved **`temp/02_gamified_athletic_tracker.jpg`** from the design options (`temp/README.md`). The app is replanned to adapt to this "gamified" athletic choice:

- **Confirmed Decision 1 (Design Direction - APPROVED BY USER)**: Adopt **`temp/02_gamified_athletic_tracker.jpg`** (**Gamified Athletic HUD**). Deliver an anime cyberpunk athletic tracker combining offline telemetry with Player Level & Combat Power (CP), glowing energy gauges, daily quest XP progression, and in-stride speed encounters.
- **Confirmed Decision 2 (Gamified Navigation Architecture)**: Elevate `Screen.Journey` into the primary bottom navigation bar (`Screen.navItems`: `Home`, `Journey`, `History`, `Goals`, `Settings`) for instant one-tap access to Quests, Hero Loadout, Expeditions, Streak Vault, and Trophies.
- **Confirmed Decision 3 (Premier Default Aesthetic)**: Set `ThemeChoice.CYBERNETIC_STRIDE` ("Cybernetic Stride") as the premier default theme, featuring obsidian `#0F131D`, Hyper Cyan `#00F0FF`, Electric Mint `#4DFFB2`, and Neon Violet `#7B61FF`.
- **Confirmed Decision 4 (Home Dashboard Gamified Athletic HUD)**:
  - **Hunter Profile Card**: Hunter Avatar, Level ("Lv.14 Ren Amamiya"), Rank ("B-RANK"), Combat Power ("4,820 CP"), and Currencies (💎 Sparks, ⚙️ Cores, 💠 Shards).
  - **Mana Overdrive / Aura Multiplier Banner**: Active streak multiplier banner (1.25x Overdrive Active!).
  - **Tactical Step Ring**: Cybernetic step arc with touch safety (display-safe, no accidental pause toggle on drag).
  - **Daily Quests XP Board**: Direct habit progress bar with XP rewards and instant shortcut to the Journey tab.
  - **Start Live Stride HUD CTA**: High-octane action button ("⚡ START LIVE STRIDE [GPS HUD]") triggering the active workout cockpit.
- **Confirmed Decision 5 (Active Walking Cockpit & Victory Summary)**:
  - Real-time circular Cadence Supercharge Gauge (>115 SPM with glowing cyan arc).
  - In-Stride Speed Encounter Challenge (Phantom Pacer speed sprint for bonus sparks/shards).
  - Hold-to-Complete tactile finish button.
  - On workout finish: Post-Workout Victory Raid Summary (`WorkoutSummarySheet.kt`) displaying Grade S/A/B rank crest, EXP surge, and itemized raid loot drops.

---

### 1. Overview & Core Concept

- **What It Does**:
  Transforms daily step tracking into an engaging, stress-free habit journey. Users track daily activity while completing bite-sized walking quests, safeguarding their hard-earned streaks with "Streak Freezes", conquering virtual world expeditions, monitoring live cadence during workouts, and celebrating milestones with shareable graphic badges.
- **Target Audience / Persona**:
  Everyday walkers, wellness enthusiasts, and commuters who want motivation, consistency, and positive reinforcement without intrusive ads, mandatory cloud accounts, or battery drain.
- **Key Value**:
  - **Overcoming Churn (Streak Resilience)**: Eliminates the discouragement of broken streaks by awarding streak protection earned through sustained walking.
  - **Clear Daily Micro-Goals**: Breaks daunting 10,000-step goals into approachable daily quests (e.g. 500 steps before 10 AM, 15-minute evening stroll).
  - **Instant Cadence Feedback**: Empowers walkers with real-time intensity coaching (aerobic brisk walking at 100+ steps/min).
  - **Zero-Telemetry Pride**: Users can proudly share their fitness milestones with friends via branded visual cards with zero privacy risks.

---

### 2. User Experience & Visual Design

#### Key User Flows

1. **Daily Habit & Morning Briefing Flow**:
   - The user opens TrueStep in the morning and is greeted on the Home screen with a dynamic **Morning Briefing Banner** showing today's target, weather-appropriate encouragement, and personalized **Daily Mini-Quests Glance**.
   - As the user walks, mini-quests update live and check off with positive reinforcement.
2. **Journey & Expedition Hub (`JourneyScreen`)**:
   - Tapping the **Journey** navigation tab reveals:
     - **Streak Vault**: Current streak counter, streak shield badge, and banked Streak Freezes (earned every 7 days).
     - **Daily Quests Board**: Live progress on daily missions with progress bars and bonus completion badges.
     - **Expedition Trail**: Interactive expedition card showing current progress along famous world trails (e.g., *Mount Fuji Ascent*, *Camino de Santiago*, *Great Wall Trek*), next landmark milestone, and total completed expeditions.
     - **Trophy Showcase**: Visual grid of milestone badges with locked/unlocked statuses and requirements.
3. **Active Walk with Cadence & Brisk Zone Coaching**:
   - The user starts a walking session from the Home screen.
   - The session sheet transforms into an active walking cockpit featuring:
     - Prominent **Live Cadence Gauge** (Steps Per Minute - SPM).
     - Color-coded **Intensity Zone Pill**: Stroll (<80 SPM, muted blue), Moderate (80–100 SPM, vibrant teal), Brisk Aerobic (100–120 SPM, radiant green), Power Walk (>120 SPM, energized amber).
     - Live brisk minutes and average cadence tracking.
4. **Milestone Celebration & Shareable Card Flow**:
   - Upon hitting 100% of the daily target or unlocking an achievement, a celebratory dialog emerges with encouragement and a **Share Progress Card**.
   - Tapping "Share" dispatches an Android system share intent with privacy-safe milestone stats.

---

### 3. Detailed Milestone Status & Tasks

- [x] **Milestone 1: Localization & Domain Registries** `[COMPLETED]`
  - Added all quest titles, descriptions, cadence labels, streak freeze strings, and celebration copy to `res/values/strings.xml` and `res/values-ar/strings.xml` (571 lines, 100% parity).
  - Implemented `QuestRegistry`, `DailyQuest`, and `PaceZone` domain models.

- [x] **Milestone 2: Persistence & ViewModel State** `[COMPLETED]`
  - [x] DataStore preferences: `bankedStreakFreezes`, `lastFreezeUsedDate`, `completedQuestIds`, `questDate`.
  - [x] Connect `bankedStreakFreezes` and `lastFreezeUsedDate` into `StepViewModel`.
  - [x] Connect `QuestRegistry.generateDailyQuests` into `StepUiState` with reactive updates.
  - [x] Compute live cadence (SPM) and `PaceZone` during walking sessions.
  - [x] Wire automatic goal milestone celebration trigger.

- [x] **Milestone 3: UI Components & JourneyScreen** `[COMPLETED]`
  - [x] Create `DailyQuestCard` component with M3 design tokens and RTL support.
  - [x] Create `StreakVaultCard` / Streak Vault component.
  - [x] Create `ExpeditionProgressCard` showing global trail milestones.
  - [x] Create `TrophyCabinet` showcasing all achievements and unlock status.
  - [x] Implement `JourneyScreen` with 4 smooth tabs (Quests, Expeditions, Streak Vault, Trophies).
  - [x] Wire `Screen.Journey` into `AppNavigation` and handle back-press.
  - [x] Add Daily Briefing / Quests glance card to `HomeScreen` with quick link to Journey.

- [x] **Milestone 4: Cadence Gauge & Walk Session Upgrade** `[COMPLETED]`
  - [x] Add live cadence SPM readout to `WalkingSessionSheet`.
  - [x] Add color-coded `PaceZone` pill (Stroll, Moderate, Brisk, Power Walk).
  - [x] Track brisk active minutes and average cadence in completed session summary.

- [x] **Milestone 5: Celebration Dialog & Shareable Card** `[COMPLETED]`
  - [x] Create `CelebrationDialog` composable.
  - [x] Add share progress card action using standard Android `ACTION_SEND` intent.
  - [x] Connect celebration trigger to `StepViewModel` when goal is reached today.

- [x] **Milestone 6: Automated Verification & Unit Tests** `[COMPLETED]`
  - [x] Unit tests for `QuestRegistry`, cadence calculation, streak freeze logic.
  - [x] Compose UI tests for `JourneyScreen` tabs and components.
  - [x] Verify clean compilation and 0 test failures (`testDebugUnitTest` and `compile_applet` PASS).

---

### Milestone 11: Cybernetic Stride Evolution & Gamified Athletic HUD
- [x] **11.1 Cybernetic Stride Theme & Design System**: Obsidian `#0F131D`, Hyper Cyan `#00F0FF`, and Electric Mint `#4DFFB2` palettes, atmospheric gradient. `[COMPLETED]`
- [x] **11.2 Hunter RPG Domain Layer & Persistence**: Hunter Level, Combat Power (CP), equipped gear slots (Footwear, Core, Exosuit, Chrono), currencies (Sparks, Cores, Shards). `[COMPLETED]`
- [x] **11.3 Active Walking Cockpit Upgrade**: Hold-to-Complete tactile slider, real-time cadence supercharge gauge, in-stride speed challenges. `[COMPLETED]`
- [x] **11.4 Post-Workout Raid Summary**: Grade S/A/B ranking crest, kilometer split cadence bars, itemized EXP surge, and loot drops. `[COMPLETED]`
- [x] **11.5 JourneyScreen "Hero & Loadout" Tab**: 5th tab with paperdoll gear loadout, tuning drawer, and filterable storage vault. `[COMPLETED]`
- [x] **11.6 Home Dashboard Integration**: Hunter status chip, Mana Overdrive state banner, cadence peak insight. `[COMPLETED]`
- [x] **11.7 Localization Parity & Test Suite**: 100% Arabic and English parity, comprehensive Robolectric and unit tests. `[COMPLETED]`

---

### Milestone 12: TrueStep UX Architecture Realignment & Name Consistency
**Objective:** Restore TrueStep's core differentiators (privacy, transparency, adaptive goals, and calm usability). Eliminate gaming distractions from the primary dashboard, prevent accidental pauses, elevate key health telemetry above the fold, and maintain 100% brand name consistency.

- [x] **12.1 Brand Name Consistency & Legacy Name Purge** `[COMPLETED]`
  - Replaced legacy working name with **TrueStep** across tests, XML comments, ProGuard, ASO metadata, and documentation.
- [ ] **12.2 Home Dashboard De-cluttering** `[PLANNED]`
  - Remove Hunter Header (Level, CP, class title) and Mana Overdrive banner from `HomeScreen.kt`. Keep all RPG loadout and gear mechanics housed cleanly inside `JourneyScreen.kt`. Reclaim ~220dp of vertical space.
- [ ] **12.3 Hero Step Ring Interaction Safety** `[PLANNED]`
  - Eliminate the full-surface tap-to-pause hazard on the 200dp center circle in `HeroStepRing.kt`. Introduce an explicit, intentional pause button with confirmation feedback. Move `PausedBanner` to the top of the dashboard.
- [ ] **12.4 Above-the-Fold KPI Grid Elevation** `[PLANNED]`
  - Reposition the 2×2 metric KPI grid (Steps, Distance, Active Time, Calories) immediately below the Hero Step Ring and Trust Status so users see full daily activity without scrolling.
- [ ] **12.5 Trust Card Touch Target & Accessibility Refactoring** `[PLANNED]`
  - Decouple nested clickable regions on the Trust Card. Make Trust Status and Goal Tiers separate cards with dedicated TalkBack content descriptions and navigation chevrons.
- [ ] **12.6 Hourly Scrubber & History Preview Polish** `[PLANNED]`
  - Add prominent floating "Exit Scrub Mode" indicator when an hour is scrubbed. Streamline Home preview chart to a clean 7-day sparkline directing to the History tab.
- [ ] **12.7 Walking Session Cockpit Refinement** `[PLANNED]`
  - Remove arcade mini-boss challenges and currencies from `WalkingSessionSheet.kt`. Focus on real physical telemetry: duration, distance, pace, and cadence (SPM). Replace Hold-to-Complete with standard accessible buttons.
- [ ] **12.8 Automated Verification & UI Test Suite** `[PLANNED]`
  - Update Compose UI tests for the streamlined Home screen layout; verify 100% test pass via `gradle :app:testDebugUnitTest`.

