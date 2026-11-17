# Homepage UI/UX Remediation & Fluid Responsive Architecture: Master Plan

A comprehensive design and engineering overhaul addressing text wrapping, vertical clipping, rigid height constraints, and visual overcrowding across TrueStep, specifically remediating the Homepage, Hero Step Ring, Action Buttons, Telemetry Cards, and Tab bars.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> **Confirmed Decisions from User Clarification (Phase 1):**
> - **Primary Action Buttons Hierarchy:** Stack the primary walk CTA ("⚡ INITIATE STRIDE RAID") full-width above the secondary "Edit Goal" button. Eliminates the rigid, crammed 48dp side-by-side row where 23-character uppercase text was wrapping into 3–4 vertical lines and clipping.
> - **Hero Step Ring Dial Integrity:** Keep the Arc Reactor step ring visual layout intact, but resolve inner text wrapping, oversized numerals, and bottom chord clipping by enforcing single-line constraints, auto-fitting typography, and safe horizontal chord bounds for pause/resume hints.
> - **Global Layout & Text Strategy:** Implement auto-adjusting font sizes, fluid multi-line responsive cards with proper flex weights, and convert rigid 5-tab rows to smooth `ScrollableTabRow` containers to guarantee pristine layout across all phone screen sizes, RTL/LTR locales, and accessibility font scalings.

---

### 1. Overview & Core Concept

- **The Problem:** Several high-visibility UI components suffered from layout rigidity, hardcoded container heights, missing text weights, and overcrowded horizontal rows. Specifically:
  1. *Action Buttons:* "⚡ INITIATE STRIDE RAID" (23 chars) and "EDIT GOAL" placed side-by-side with fixed `height(48.dp)` and `weight(1.3f)` caused text to wrap into 4 vertical lines ("⚡\nINITIATE\nSTRIDE\nRAID"), clipping against the container.
  2. *Hero Step Ring:* "TAP TO PAUSE" / "TAP TO RESUME" (and Arabic "اضغط للإيقاف المؤقت") placed at the bottom chord of the circular dial wrapped vertically into 3 words, while 5-digit step counts risked line breaks.
  3. *Hunter Dossier Card:* Top terminal row and middle name/rank row placed long titles side-by-side with `SpaceBetween` without flexible weights, causing rank badges to wrap vertically or push Combat Power (CP) chips off-screen.
  4. *Hardware Telemetry Strip:* Three distinct telemetry items crammed into a single row, forcing the status label to break into 3 vertical lines.
  5. *Journey Screen Tabs:* 5 tabs crammed into a fixed-width `PrimaryTabRow`, squishing labels like "Expeditions" and "Streak Vault" (and Arabic equivalents) into tall, distorted vertical text blocks.
- **The Solution:** A systematic, fluid responsive layout architecture that respects Android typography metrics, provides generous breathing room, handles variable locale text lengths (English and Arabic), and ensures touch targets remain comfortably $\ge 48\text{dp}$.

---

### 2. User Experience & Visual Design

#### Remediated Homepage Layout Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Remediated TrueStep HUD                         │
├────────────────────────────────────────────────────────────────────────┤
│ [// STANDBY BANNER] (Full width alert if paused)                       │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ Hunter Dossier: Level, Avatar, Name (Flexible weight)              │ │
│ │ Top: [// HUNTER DOSSIER] ───► [OPEN GEAR VAULT →] (Wrapped/flex)   │ │
│ │ Middle: [Avatar] Jinwoo Sung [S-RANK] ───► [12,450 CP]             │ │
│ │ Bottom: Segmented EXP Gauge + [💎 Sparks | ⚙️ Cores | 💠 Shards]    │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ Mana Overdrive Banner: [⚡ MANA OVERDRIVE] (Flexible title/sub)    │ │
│ │ Multiplier Chip: [1.25x BOOST] aligned gracefully                  │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ Hero Step Ring (Arc Reactor Dial):                                 │ │
│ │   - 48 Radial HUD ticks + Glowing Progress Arc                     │ │
│ │   - Center: 12,450 (Auto-sized, single-line headline)              │ │
│ │   - STEPS OF GOAL (Crisp uppercase label)                          │ │
│ │   - [⚡ 100% OVERDRIVE] / [ 78% CHARGE ] Badge                     │ │
│ │   - [⏸ TAP TO PAUSE] (Single-line, ellipsis-safe, auto-fit chord)   │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ 2x2 Metric KPI Grid (Auto-wrapping labels, no truncation)          │ │
│ │ [Steps: 12,450]          │ [Distance: 8.42 km]                     │ │
│ │ [Calories: 512 kcal]     │ [Goal Progress: 78%]                    │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ Telemetry Trust Strip (Two clean balanced rows instead of 3-in-1)  │ │
│ │ Row 1: [● HARDWARE TELEMETRY] ───► [STEP_COUNTER • HIGH CONFIDENCE]│ │
│ │ Row 2: Supportive Habit Briefing ───► Last Synced 10:45 AM         │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ Goal Tiers Overview: [Min 4k] • [Target 8k] • [Stretch 10k] ───► > │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ Primary Action Buttons (CONFIRMED USER CHOICE: STACKED FULL-WIDTH) │ │
│ │ ┌────────────────────────────────────────────────────────────────┐ │ │
│ │ │ ⚡ INITIATE STRIDE RAID                     (Full-Width 52dp)  │ │ │
│ │ └────────────────────────────────────────────────────────────────┘ │ │
│ │ ┌────────────────────────────────────────────────────────────────┐ │ │
│ │ │ ✏️ EDIT DAILY GOAL                          (Full-Width 44dp)  │ │ │
│ │ └────────────────────────────────────────────────────────────────┘ │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ Daily Habit Briefing & Quests Glance (Active Contracts)            │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ 24-Hour Hourly Scrub Ribbon (00:00 to 23:00 timeline + legend)     │ │
│ └────────────────────────────────────────────────────────────────────┘ │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ Embedded Multi-Range History Preview Chart (Daily, Weekly, Monthly)│ │
│ └────────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Stacked Action Buttons vs. Side-by-Side**
  - *Chosen Approach:* Stack the primary action ("⚡ INITIATE STRIDE RAID") as a dominant, high-visibility 52dp button, placed directly above the secondary action ("EDIT DAILY GOAL") as an outlined/tonal 44dp button.
  - *Why:* Solves 100% of the text wrapping defect. Gives the high-energy workout CTA prominent prominence while allowing "EDIT GOAL" to be translated into any language (including longer Arabic phrases) without horizontal squishing.
- **Decision 2: Hero Step Ring Inner Readout Geometry & Text Fitting**
  - *Chosen Approach:* Keep the Arc Reactor dial diameter and visual language intact, but apply strict single-line text constraints (`maxLines = 1`, `softWrap = false`, `overflow = TextOverflow.Ellipsis`), reduce padding slightly from 14dp to 8dp on the inner column, scale step count dynamically if digits exceed 5, and ensure the pause pill width conforms to the inner circle's chord geometry.
  - *Why:* Directly fulfills user choice ("keep the ring as its") while completely eliminating the unsightly multi-line vertical wrapping of "TAP TO PAUSE".
- **Decision 3: Fluid Two-Row Telemetry Card**
  - *Chosen Approach:* Structure the Hardware Telemetry card into two semantic rows: Top Row (Sensor Status Indicator + Data Source & Confidence Chip); Bottom Row (Supportive Message + Freshness Timestamp).
  - *Why:* Eliminates the 3-item horizontal collision on 360dp viewports while keeping all critical transparency telemetry immediately readable above the fold.
- **Decision 4: ScrollableTabRow for Journey Screen & History Tabs**
  - *Chosen Approach:* Migrate `PrimaryTabRow` with 5 fixed tabs in `JourneyScreen.kt` to `ScrollableTabRow(edgePadding = 16.dp)`.
  - *Why:* Eliminates squished, multi-line vertical text distortion on tabs like "Expeditions", "Streak Vault", and "Trophies" (especially in Arabic where text length expands by ~40%).

---

### 4. Technical Architecture & File Modification Roadmap

#### Phase 1: Homepage Action Buttons & Telemetry Card Refactoring `[COMPLETED]`
- **Target File:** `app/src/main/java/com/example/ui/screens/HomeScreen.kt`
- **Changes:**
  1. Replace side-by-side Button Row with vertical Column containing full-width "⚡ INITIATE STRIDE RAID" (height 52dp) and full-width "EDIT GOAL" (height 44dp).
  2. Refactor `today_trust_status_card`: separate hardware status from data source/confidence into distinct flex rows.
  3. Refactor `home_hunter_header_card`: assign `Modifier.weight(1f, fill = false)` to avatar/name group to prevent pushing Combat Power (CP) chip off-screen; wrap top dossier row with clean flex layout.
  4. Ensure all labels have `TextOverflow.Ellipsis` and `maxLines` protections.

#### Phase 2: Hero Step Ring Layout & Typography Auto-Fit `[COMPLETED]`
- **Target File:** `app/src/main/java/com/example/ui/components/HeroStepRing.kt`
- **Changes:**
  1. Set step count typography to auto-scale based on digit count (e.g., 32sp for $\le 5$ digits, 28sp for $>5$ digits).
  2. Set `ring_pause_hint` to `maxLines = 1`, `softWrap = false`, with compact horizontal padding (8dp), preventing word breaks.
  3. Ensure overdrive pill ("⚡ 100% OVERDRIVE" / "[ 78% CHARGE ]") enforces single-line rendering.

#### Phase 3: Home KPI Grid & Hourly Ribbon Legend Symmetry `[COMPLETED]`
- **Target Files:**
  - `app/src/main/java/com/example/ui/components/HomeKpiGrid.kt`
  - `app/src/main/java/com/example/ui/components/HourlyScrubRibbon.kt`
- **Changes:**
  1. In `HomeKpiGrid.kt`, adjust `label` text styling to `maxLines = 1`, `overflow = TextOverflow.Ellipsis`, and ensure numerical values never wrap vertically.
  2. In `HourlyScrubRibbon.kt`, adjust the bottom legend ("00:00 Start of day" / "23:00 End of day") to use compact phrasing or responsive text sizes so neither label wraps into two lines.

#### Phase 4: Journey Screen & Sub-Screen Tab Usability Overhaul `[COMPLETED]`
- **Target File:** `app/src/main/java/com/example/ui/screens/JourneyScreen.kt`
- **Changes:**
  1. Replace fixed `PrimaryTabRow` with `ScrollableTabRow(edgePadding = 16.dp)`.
  2. Ensure each tab has dedicated horizontal breathing room, preventing vertical label breaking.

#### Phase 5: Verification & Automated Regression Testing `[COMPLETED]`
- **Verification Commands:**
  - `compile_applet`: Verify clean compilation with 0 warnings or errors.
  - `gradle :app:testDebugUnitTest`: Verify all unit tests, Robolectric UI tests, and Compose navigation tests pass with 100% fidelity.
  - Preserve 100% of Compose `testTag` IDs (`start_walk_session_cta`, `edit_goal_button`, `hero_ring_toggle`, `today_trust_status_card`, `tab_quests`, `tab_hero`, `tab_expeditions`, `tab_streak_vault`, `tab_trophies`).
