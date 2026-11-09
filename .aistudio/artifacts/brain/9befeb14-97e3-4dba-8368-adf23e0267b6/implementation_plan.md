# Kishu Full-Screen Backdrop Catalog & Visuals Studio Plan

Transform the **Kishu (⚙️)** hub into a complete, full-screen **Backdrop Catalog** experience inspired by Kyant0's `AndroidLiquidGlass`, featuring dedicated bottom navigation, authentic visual effects (real-time backdrop blur, specular refraction, chromatic glow, and color tinting), interactive liquid controls, and 100% working, persistent settings applied live across the entire OkKhata app.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The plan integrates the user's explicit choices confirmed in Phase 1:

- **Confirmed Decision 1 (Layout Structure)**: Full-screen Backdrop Catalog experience with dedicated internal bottom navigation (`Visuals FX`, `Controls UI`, and `App Settings`).
- **Confirmed Decision 2 (Feature Focus)**: Primary focus on Backdrop visual effects (blur intensity, specular refraction, chromatic edge glow, glass tint palettes, and film grain overlay).
- **Confirmed Decision 3 (Scope & Persistence)**: All visual adjustments and settings apply live across the entire application and persist permanently to `SharedPreferences` via `KhataViewModel`.

---

### 1. Overview & Core Concept

- **What It Does**: Upgrades Kishu from a modal dialog into a fluid, full-screen Backdrop Catalog studio. Users can customize, test, and tune the app's liquid glass engine in real time, adjust frosted backdrop parameters, experiment with interactive liquid components, and manage persistent store configurations.
- **Target Audience**: Shop owners and power users who love deep visual customization, premium iOS frosted aesthetics, fluid 90Hz/120Hz spring physics, and tactile haptic feedback.
- **Key Value**: Delivers the complete Kyant Backdrop Catalog feature suite inside OkKhata with zero mock controls—every slider, toggle, and tint chip directly alters the live app experience.

---

### 2. User Experience & Visual Design

#### A. Full-Screen Backdrop Catalog Layout
- **Container**: Full-screen immersive scaffold with edge-to-edge rendering, respecting top notch (`statusBarsPadding`) and gesture navigation (`navigationBarsPadding`).
- **Header**: Sleek liquid glass top bar with title "Backdrop Catalog", active frame rate pill (90Hz / 120Hz), and a liquid spring close button.
- **Internal Bottom Navigation Dock**:
  1. **Visuals (FX)**: Backdrop blur, refraction rim, chromatic glow, tint color, and noise grain.
  2. **Controls (UI)**: Interactive liquid sliders, spring toggles, segmented tabs, and tactile buttons.
  3. **Settings (⚙️)**: Real persistent shop profile, UPI ID, PIN lock, auto-backup, and soundbox alerts.

#### B. Visual Effects Studio (Tab 1: Visuals)
- **Live Lens Preview**: An interactive liquid glass card floating over dynamic ambient color mesh that immediately reflects adjustments:
  - **Backdrop Blur Slider**: Continuous range from 4.dp to 64.dp blur radius with real-time value badge.
  - **Specular Refraction Slider**: Hairline border shine opacity (0% to 100%) and rim highlight width.
  - **Chromatic Glow & Tint Selector**: Color pills for iOS Frosted Clear, Neon Cyan, Emerald Green, Sunset Amber, Rose Quartz, and Amethyst Purple.
  - **Film Grain & Texture Toggle**: Subtle glass noise grain overlay.
  - **Mass 4x Physics Engine Toggle**: Ultra-smooth inertia damping for all spring animations.

#### C. Interactive Controls Showcase (Tab 2: Controls)
- **Interactive Liquid Slider**: With volume/brightness scrubbing, percentage badge, and haptic clicks.
- **Liquid Toggles**: Smooth spring sliding thumb with specular light effect.
- **Liquid Push Buttons**: Spring-compressed buttons with tactile sound and haptics.
- **Liquid Segmented Bar**: Floating capsule tab bar with animated sliding pill.

#### D. Persistent App Settings (Tab 3: Settings)
- **Shop Profile & UPI**: Inline liquid card editor for Shop Name and UPI ID with one-tap save.
- **Security PIN**: 4-digit PIN protection with liquid switch toggle and keypad dialog.
- **Audio & Soundbox**: Scroll gear click sounds and payment alert voice notifications.
- **Data & Backups**: Local backup creation and configuration evaluation triggers.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Full-Screen Screen vs. Modal Dialog**
  - *Chosen Approach*: Implement a full-screen composable overlay with smooth slide-up / fade-in transition and dedicated back handling (`BackHandler`).
  - *Why*: Accommodates the full Backdrop Catalog component suite and visual testing playground without vertical crampedness.
  - *Alternatives Considered*: A multi-tab dialog was rejected by the user in favor of the full-screen experience.

- **Decision 2: App-Wide StateFlow Persistence in KhataViewModel**
  - *Chosen Approach*: Store `glassBlurIntensity`, `glassTintIndex`, `specularIntensity`, and `chromaticGlowEnabled` in `KhataViewModel` backed by `SharedPreferences`.
  - *Why*: Ensures the entire app (bottom dock, top header, transaction cards, dialogs) immediately reflects the user's visual tuning and retains it across app restarts.

---

### 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                       KhataViewModel                        │
│  - isGlassActive: StateFlow<Boolean>                        │
│  - glassBlurIntensity: StateFlow<Float> (4dp - 64dp)         │
│  - specularIntensity: StateFlow<Float> (0.0 - 1.0)          │
│  - glassTintIndex: StateFlow<Int> (0 to 5)                  │
│  - chromaticGlowEnabled: StateFlow<Boolean>                 │
│  - mass4xEngineActive, display90HzActive, touch240HzActive  │
│  - shopName, upiId, securityPin, soundboxAlertEnabled       │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│              KishuBackdropCatalogScreen                     │
│               (Full-Screen Edge-to-Edge)                    │
├─────────────────────────────────────────────────────────────┤
│  Top Bar: Title, FPS Badge, Liquid Close Button             │
├─────────────────────────────────────────────────────────────┤
│  Pager / Screen Content:                                    │
│    [Tab 0: Visuals FX]  [Tab 1: Controls UI]  [Tab 2: App]  │
├─────────────────────────────────────────────────────────────┤
│  Bottom Dock: LiquidBottomTabs (Visuals | Controls | App)   │
└─────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│            Backdrop Catalog Component Library                │
│  - LiquidButton.kt: Specular shine + spring compression     │
│  - LiquidToggle.kt: iOS glass sliding switch                │
│  - LiquidSlider.kt: Fluid scrubber with haptic ticks        │
│  - LiquidBottomTabs.kt: Sliding indicator glass dock        │
│  - LiquidColorPill.kt: Tint selection swatch with glow       │
└─────────────────────────────────────────────────────────────┘
```

---

### Verification & Testing Plan

1. **Compilation**: Run `compile_applet` to guarantee error-free compilation and dependency alignment.
2. **Back Navigation & Lifecycle**:
   - Verify tapping the 5th tab ("Kishu ⚙️") on the main bottom bar opens the full-screen Backdrop Catalog smoothly.
   - Verify tapping the close button or system Android back gesture returns cleanly to the dashboard.
3. **Visual Effects & App-Wide Reflection**:
   - Adjust the Blur slider and verify live blur updates on both the preview card and the bottom dock.
   - Select different color tint swatches (e.g., Neon Cyan or Emerald Green) and confirm immediate app-wide tint shift.
   - Toggle Chromatic Glow and Specular Refraction to verify edge highlights.
4. **App Settings Verification**:
   - Edit Shop Name and UPI ID, tap Save, and ensure updates reflect in `KhataViewModel`.
   - Toggle PIN lock and test PIN entry.
   - Trigger Auto-Backup and confirm toast confirmation.
