# ChargeScope Pixel - UI Layout & Architecture Info Sheet

## 1. App Architecture & Navigation
- **Main Entry Point:** `MainActivity.kt` sets the content to `ChargeScopeTheme` and calls `ChargeScopeApp`.
- **Navigation Controller:** `ChargeScopeApp.kt` uses Jetpack Compose Navigation with a `navController`.
- **Navigation Routes:**
    - `dashboard`: `DashboardScreen(summary, latest)`
    - `live`: `LiveMonitorScreen(samples)`
    - `sessions`: `SessionsScreen(sessions)`
    - `health`: `BatteryHealthScreen(state)`
    - `insights`: `InsightsScreen(summary, onExportCsv)`
    - `settings`: `SettingsScreen(settings, ...)`
- **Layout Structure:** `Scaffold` with a `bottomBar` containing a `NavigationBar`. The main content is a `Box` with a vertical gradient background containing the `NavHost`.

## 2. UI Screens (`app/src/main/java/com/chargescopixel/app/ui/screens/`)
- **DashboardScreen:** Uses `LazyColumn`. Displays `StatCard`s for total sessions, total charge gained, and estimated cycles. Shows the `latestSession` using a `SessionCard`.
- **LiveMonitorScreen:** Displays real-time battery telemetry. Includes `StatCard` for current battery status and multiple `LineChart`s for Battery %, Temperature, Voltage, and Current.
- **SessionsScreen:** A list of past charging sessions using `SessionCard`s in a `LazyColumn`.
- **BatteryHealthScreen:** Displays health-related stats using `StatCard`s (Estimated Full Cycles, Average Peak Temp, Average Charge Speed) and a summary note.
- **InsightsScreen:** Shows session averages and a button to export data as CSV.
- **SettingsScreen:** Contains switches for Alerts and Dynamic Color, and sliders for Overheat and Slow Charging thresholds.

## 3. Reusable Components (`app/src/main/java/com/chargescopixel/app/ui/components/`)
- **StatCard:** A styled card with a title, value, optional subtitle, and accent color. Used for displaying single metrics.
- **SessionCard:** A card representing a single charging session, displaying start time, duration, battery gain, speed, and peak temperature.
- **LineChart:** A custom `Canvas`-based line chart for visualizing telemetry data over time.
- **SettingRow:** (Private in `SettingsScreen.kt`) A row with a title, subtitle, and a `Switch`.

## 4. Theming (`app/src/main/java/com/chargescopixel/app/ui/theme/`)
- **Colors:** Defined in `Color.kt`. Uses a dark-centric palette (`DeepNavy`, `SkyBlue`, `MintGlow`, `WarmAlert`).
- **Theme:** `ChargeScopeTheme.kt` supports Material You dynamic colors on Android 12+. It falls back to `DarkColors` (default) or `LightColors`.
- **Typography:** Uses default Material 3 `Typography` (defined in `Type.kt`).

## 5. Data Models (`app/src/main/java/com/chargescopixel/app/domain/` & `data/`)
- **InsightsSummary:** Aggregated stats (total sessions, total gain, etc.).
- **SessionWithMetrics:** Combines a `ChargingSessionEntity` with its calculated `SessionMetrics` and a list of `BatterySampleEntity`s.
- **BatterySampleEntity:** Individual battery telemetry records (percent, temp, voltage, current).
- **AppSettings:** User preferences (alerts, thresholds, dynamic color).

## 6. ViewModels (`app/src/main/java/com/chargescopixel/app/viewmodel/`)
- Each screen has a corresponding ViewModel that exposes data via `StateFlow`.
- **DashboardViewModel:** `summary: StateFlow<InsightsSummary>`, `latestSession: StateFlow<SessionWithMetrics?>`
- **LiveMonitorViewModel:** `samples: StateFlow<List<BatterySampleEntity>>`
- **SessionsViewModel:** `sessions: StateFlow<List<SessionWithMetrics>>`
- **BatteryHealthViewModel:** `uiState: StateFlow<BatteryHealthUiState>`
- **InsightsViewModel:** `summary: StateFlow<InsightsSummary>`, `exportCsv()` function.
- **SettingsViewModel:** `settings: StateFlow<AppSettings>`, and various `setX` functions.
