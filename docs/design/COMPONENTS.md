# COMPONENTS.md — Mavo

> Last updated: 2026-11-02

## Shared components

### GraphPaperBackground

- **File:** `ui/components/GraphPaperBackground.kt`
- **Purpose:** Render a warm graph-paper canvas matching the supplied background
- **Where used:** Root content in `MainActivity.kt`, behind screens and system bars

### GraphPaperCanvas

- **File:** `ui/screens/GraphPaperCanvas.kt`
- **Purpose:** `Modifier.graphPaperCanvas()` — optional grid overlay for screens that draw on a plain surface
- **Geometry:** Same as `GraphPaperBackground`: 16 dp pitch, 0.55 dp stroke, `Surface.paperGrid` at 0.6 alpha (never a second, coarser grid)

### Caller modifiers

Visual composables with a `modifier: Modifier = Modifier` parameter apply it
to their root layout. Callers can use Compose's native `offset`, `size`,
`width`, `height`, and `padding` modifiers for source-level layout tuning.
`offset` changes placement but not measured size, so it can overlap nearby
content; use size or padding when neighboring layouts must reflow. Default
modifier values preserve existing layouts.

### Adaptive app shell

- **File:** `ui/components/AdaptiveAppShell.kt`
- **Purpose:** Material 3 compact bottom navigation and wider navigation rail
- **Actions:** Home, Vouchers, Parties, More, and New Sale
- **States:** Current route selected; compact uses bottom navigation; medium/expanded uses a rail with New Sale action
- **Where used:** `MainActivity.kt`

### UiLoadStateNotice

- **File:** `ui/components/UiLoadStateNotice.kt`
- **Purpose:** Show loading progress and retryable read errors without presenting missing data as confirmed empty
- **States:** Loading, confirmed-empty/content handled by the screen, cached-content error with retry, uncached error with retry
- **Where used:** Home, billing, vouchers, parties, products, reports, ledger, income/expense, and Bank & Cash

### LoadingIndicator

- **File:** `ui/components/LoadingIndicator.kt`
- **Purpose:** Theme-aware skeleton/shimmer placeholders for pending reads
- **States:** Loading (shimmer animation or static placeholder), content loaded (replaced by real content)
- **Where used:** Any screen that loads data asynchronously (dashboard, vouchers, parties, etc.)

Loading skeletons do not represent confirmed empty data or read failures.
Screens render explicit read states and expose retry for failures.

### SharedComponents

- **File:** `ui/screens/SharedComponents.kt`
- **Purpose:** Common UI pieces reused across multiple screens — `RetailTextField` (now a thin delegate over `ZbField`: label-above, sentence-case gray label, optional `fieldModifier` for focus targets), `StateDropdownMenu`, PIN lookup
- **Where used:** Throughout the app

### Skeleton

- **File:** `ui/theme/Skeleton.kt`
- **Purpose:** Skeleton loading shapes (shimmer effect composables)
- **Props:** Shape, size, color (uses `AppColors.shimmerBg`)
- **States:** Animating (shimmer), Static
- **Where used:** Dashboard cards, list screens while loading

### VoucherItemSheet

- **File:** `ui/screens/VoucherItemSheet.kt`
- **Purpose:** Bottom sheet for adding/editing a line item on a voucher
- **Props:** Product list, existing item data (for edit), GST mode (IGST vs CGST+SGST)
- **States:** Default (empty form), Editing (pre-filled), Validation error (red borders)
- **Where used:** NewVoucherScreen (all voucher types)

### PartySheets

- **File:** `ui/screens/PartySheets.kt`
- **Purpose:** Bottom sheets for party creation, editing, and detail display
- **Props:** Party data, party type filter
- **States:** Create (empty), Edit (pre-filled), View (read-only)
- **Where used:** PartiesScreen, NewVoucherScreen (party picker)

### BarcodeScannerDialog

- **File:** `ui/screens/BarcodeScannerDialog.kt`
- **Purpose:** Full-screen camera dialog for barcode scanning and OCR
- **Props:** Scan mode (barcode vs text), callback with result
- **States:** Camera preview, Processing, Result received, Permission denied
- **Where used:** ProductsScreen (barcode entry), NewVoucherScreen

### ProductOptionalFields

- **File:** `ui/screens/ProductOptionalFields.kt`
- **Purpose:** Expandable section for batch, serial, secondary unit fields
- **Props:** Product state, field change callbacks
- **States:** Collapsed (hidden), Expanded (showing optional fields)
- **Where used:** ProductsScreen add/edit form

### BusinessProfileSettingsSection

- **File:** `ui/screens/BusinessProfileSettingsSection.kt`
- **Purpose:** Business profile edit form (used in Settings after initial setup)
- **Props:** Current profile data, save callback
- **States:** Viewing, Editing, Saving, Validation error
- **Where used:** SettingsScreen

### EmailAutomationSection

- **File:** `ui/screens/EmailAutomationSection.kt`
- **Purpose:** Email account linking, automation rules, email history
- **Props:** Email accounts, rules, history
- **States:** No account linked, Account linked, Rules list, History view
- **Where used:** SettingsScreen

### ProfileFormSupport

- **File:** `ui/screens/ProfileFormSupport.kt`
- **Purpose:** Shared validation, `SetupStep` order and state auto-detect helpers for business profile forms
- **Where used:** FirstRunFlow / setup steps, BusinessProfileSettingsSection

### WizardScaffold

- **File:** `ui/screens/SharedComponents.kt`
- **Purpose:** Shared chrome for first-run steps — header (logo, back arrow, step label), scrollable body, pinned footer button
- **Props:** `title`, `subtitle`, `stepLabel`, `onBack`, `nextLabel`, `nextEnabled`, `hint`, `secondary` slot, `content`
- **States:** Primary action enabled / disabled, optional hint line, optional secondary action
- **Where used:** All 8 first-run wizard steps (`FirstRunFlow.kt`, `SetupScreen.kt`)

### ZeroUi kit

- **File:** `ui/components/ZeroUi.kt`
- **Purpose:** Shared Material 3 controls — `CircleIconButton`, `StepProgress`, `WizardHeader`, `WizardTitle`, `ZbField`, `PrimaryButton` / `SecondaryButton`, filter chips, segmented tabs, `ZbCard`, `SearchField`, `SectionLabel`, `EmptyState`
- **States:** Material 3 enabled, disabled, selected, and focus feedback
- **Conventions:** 16 dp card radius, 8 dp field radius, 1 dp hairline borders, sentence-case labels (no tracked uppercase)
- **Accessibility:** Icon actions have at least 48 dp touch targets
- **Where used:** Voucher wizard and every Zero-restyled screen

## Theme components

### MavoTheme

- **File:** `ui/theme/AppTheme.kt`
- **Purpose:** Root theme composable wrapping MaterialTheme with app-specific color scheme
- **Props:** `appTheme: AppTheme`
- **Where used:** MainActivity (root of composition tree)

### themedInputColors / mavoInputColors

- **File:** `ui/theme/AppTheme.kt`
- **Purpose:** Pre-configured `TextFieldColors` matching current theme
- **Where used:** Every `OutlinedTextField` in the app

### GstinHelpers

- **File:** `ui/theme/GstinHelpers.kt`
- **Purpose:** GSTIN validation and state code extraction
- **Where used:** Setup, Settings, party forms

## Animation components

Located in `ui/animation/PremiumMotion.kt`:

| Component | Purpose |
|-----------|---------|
| `enterTransition` / `exitTransition` | Navigation slide animations |
| `dialogEnter` / `dialogExit` | Dialog open/close animations |
| `pressScale` / `m3SpringPress` / `clickableScale` | Tap feedback — scale only, no translation or glow |
| `premiumClickable` / `premiumCombinedClickable` | Click wrappers that apply the shared press feedback |
| `premiumFabEntrance` | One-shot FAB entrance |
| `NavBarContent` | Bottom nav bar animated content switching |

Animations honor Android animator duration scale; scale zero disables motion.
Removed in the polish pass: press translation (`translationY`), `m3PulseHighlight`
(0.6 damping pulse), and Dashboard spotlight `rotationY` / `cameraDistance`.

## Format utilities

Located in `ui/format/`: Number/currency formatting helpers used across screens.

## Selection utilities

Located in `ui/selection/`: Multi-select state helpers for list screens.

## Mavo · Redesign custom components (Penpot)

Custom components implemented as Penpot helper functions (in `lib2.js`, executed in the plugin context). Not Kotlin code — design artifacts only.

| Component | Purpose | Key props |
|-----------|---------|-----------|
| Floating dock | Bottom nav bar: Home, Vouchers, Parties, More + centered New Sale FAB | Active destination |
| Expressive buttons | Primary / tonal / secondary / card / danger with icon + label variants | Kind, size, radius, shadow |
| Filled fields | Rounded input with uppercase label, focus stroke, optional badge/icon/chevron | Label, value, focus, badge |
| Segmented controls | Pill-segmented tab groups | Items, active index |
| Chips | Mint / amber / coral / blue / solid / outline | Kind, size, height |
| Ledger cards | Account cards with icon + title + value + status chip | Account, balance, status |
| Metric tiles | Label + large value + delta chip | Label, value, delta |
| Tables | Header + rows with right-aligned numeric columns | Columns, widths, rows |
| Bottom sheets | Drawer from bottom with handle, title, close, safe-area footer | Title, close action |
| Empty states | Icon + title + body centered on canvas | Icon, title, body |
| Status chips | Paid / due / overdue / posted pills | Status text, kind |
| Logo tile | Rounded square "MA·VO" mark with amber dot | Size, dark mode |
| Graph-paper background | Subtle grid overlay on board background | Step, color, opacity |

Motion helpers: press scale `0.97` @ `120 ms` ease-out; page/sheet transitions `220–280 ms`; transform/opacity animation only; reduced-motion respected.

