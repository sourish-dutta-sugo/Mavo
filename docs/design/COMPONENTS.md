# COMPONENTS.md — Mavo

> Last updated: 2026-10-03

## Shared components

### LoadingIndicator

- **File:** `ui/components/LoadingIndicator.kt`
- **Purpose:** Skeleton/shimmer loading placeholder
- **States:** Loading (shimmer animation), Content loaded (replaced by real content)
- **Where used:** Any screen that loads data asynchronously (dashboard, vouchers, parties, etc.)

### SharedComponents

- **File:** `ui/screens/SharedComponents.kt`
- **Purpose:** Common UI pieces reused across multiple screens
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

Located in `ui/transitions/`:

| Component | Purpose |
|-----------|---------|
| `enterTransition` / `exitTransition` | Navigation slide animations |
| `dialogEnter` / `dialogExit` | Dialog open/close animations |
| `pressScale` / `clickableScale` | Tap feedback (scale down on press) |
| `NavBarContent` | Bottom nav bar animated content switching |

## Format utilities

Located in `ui/format/`: Number/currency formatting helpers used across screens.

## Selection utilities

Located in `ui/selection/`: Multi-select state helpers for list screens.
