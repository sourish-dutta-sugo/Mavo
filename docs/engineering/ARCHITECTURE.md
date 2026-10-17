# ARCHITECTURE.md — Mavo

> Last updated: 2026-09-29

## Folder structure

```
app/src/main/java/com/mavo/app/
├── MainActivity.kt          # Single Activity, NavHost, adaptive shell, theme setup
├── core/
│   ├── db/                   # Database utilities
│   ├── export/               # Export helpers
│   ├── preferences/          # DataStore preference wrappers
│   ├── print/                # Print utilities
│   ├── scheduler/            # Scheduling helpers
│   ├── share/                # Share intents
│   └── util/                 # General utilities
├── data/
│   ├── AppDatabase.kt        # Room database class (version 16, 21 entities)
│   ├── AppDaos.kt            # All DAO interfaces
│   ├── AppRepository.kt      # Single repository (all business logic)
│   ├── Entities.kt           # All Room entity data classes
│   ├── Migrations.kt         # DB migrations (v4→v16) + ensureColumn helpers
│   ├── AppPreferences.kt     # Jetpack DataStore wrapper
│   ├── ChangelogLoader.kt    # Loads changelog.json from assets
│   ├── FinancialYearUtils.kt # FY date calculations
│   ├── HsnLookup.kt          # Built-in HSN code database
│   ├── InvoiceDefaults.kt    # Default terms text
│   ├── Utils.kt              # Data-layer utilities
│   ├── VoucherExtras.kt      # Voucher helper extensions
│   ├── dao/                  # (additional DAO files if any)
│   └── repository/           # (additional repository files if any)
├── domain/
│   ├── model/                # Domain models (Ledger, Party, Product, Voucher)
│   ├── reference/            # Reference data
│   └── rules/                # Business rules
├── feature/
│   ├── billing/              # BillingScreen.kt + responsive counter layout
│   ├── dashboard/            # (dashboard feature logic)
│   ├── invoice/              # (invoice feature logic)
│   ├── ledger/               # (ledger feature logic)
│   ├── parties/              # (party feature logic)
│   ├── products/             # (product feature logic)
│   ├── reports/              # (reports feature logic)
│   ├── settings/             # (settings feature logic)
│   └── vouchers/             # (voucher feature logic)
├── services/
│   ├── InvoiceGenerator.kt   # HTML → WebView → PDF invoice generation
│   ├── WebViewPdfWriter.java # Java WebView PDF helper
│   ├── CsvTransferManager.kt # CSV export/import
│   ├── ExportStorageManager.kt# File export to device storage
│   ├── EmailComposer.kt      # Email composition
│   ├── EmailAutomationService.kt # Automated email sending
│   └── documents/            # Document templates
├── ui/
│   ├── AdaptiveLayout.kt    # Window-size decisions and app-level size local
│   ├── components/
│   │   └── AdaptiveAppShell.kt # Material 3 bottom navigation and rail
│   ├── AppViewModel.kt       # Main ViewModel (DB init, all state)
│   ├── DashboardViewModel.kt # Dashboard-specific ViewModel
│   ├── UiDataCache.kt        # Process-local cached UI snapshots and read states
│   ├── screens/              # 26 Compose screen files incl. FirstRunFlow (see ../design/SCREENS.md)
│   ├── components/           # Shared composables (LoadingIndicator)
│   ├── theme/                # Design system (see ../design/DESIGN_SYSTEM.md)
│   ├── animation/            # Animation utilities
│   ├── transitions/          # Navigation transitions, press effects
│   ├── format/               # Formatting utilities
│   └── selection/            # Selection state helpers
└── utils/
    └── FilePicker.kt         # File picker utility
```

## Architecture pattern: MVVM

### Compose layout tuning

Visual composables with a `modifier: Modifier = Modifier` parameter apply it to
their root layout. Callers tune position with `offset`, dimensions with
`size`/`width`/`height`, and layout spacing with `padding`. `offset` does not
change measured size; use size or padding when surrounding content must reflow.
Keep modifier order intentional and preserve default geometry.

### Responsive Compose layout

The app shell and billing feature use Material 3 window size classes rather
than raw screen-width checks. Compact windows use a bottom bar; medium and
expanded windows use a navigation rail. Expanded Home, Vouchers, Parties,
Settings, and Reports use their existing wider layouts; billing uses a
product/cart split pane. Catalog and register screens cap content width on
wider windows. `ResponsivePaneLayout` owns billing-pane measurement and
placement, while `BillingScreen` retains cart state and business actions.

```
┌─────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────┐
│  Compose UI  │ ──▶ │  ViewModel   │ ──▶ │  Repository  │ ──▶ │  Room DB │
│  (screens)   │ ◀── │ (StateFlow)  │ ◀── │ (AppRepo)    │ ◀── │  (DAOs)  │
└─────────────┘     └──────────────┘     └──────────────┘     └──────────┘
```

- **UI layer:** Jetpack Compose screens observe `StateFlow` from ViewModels
- **ViewModel layer:** `AppViewModel` (shared), `DashboardViewModel`, `ThemeViewModel`
- **Repository layer:** Single `AppRepository.kt` contains all business logic, calls DAOs
- **Data layer:** Room entities + DAOs + migrations

## State management

- **UI state:** Compose `remember` / `mutableStateOf` for local screen state
- **Shared state:** `StateFlow` from ViewModels, collected via `collectAsState()`
- **Theme state:** `ThemeRuntime.currentTheme` (global mutable state), `LocalAppTheme` (CompositionLocal)
- **Preferences:** Jetpack DataStore (`AppPreferences`)
- **Navigation state:** Jetpack Navigation Compose (`NavHostController`)

### Room-backed UI snapshots

`AppViewModel` exposes explicit loading, content, confirmed-empty, and error
states. Home, billing, vouchers, parties, products, reports, ledger books,
income/expense, and Bank & Cash consume these states. `UiDataCache` keeps typed
read snapshots in process memory, keyed by dataset and financial year; global
profile data uses a global key. Each Room emission replaces its snapshot.
Room remains the source of truth, with no TTL or disk cache. Read failures
preserve cached values and expose retry; uncached failures do not appear as
confirmed empty lists or zero-valued financial reports.

## Navigation

- Single `NavHost` in `MainActivity.kt`
- Routes defined in `Routes` object (string constants)
- Adaptive Material 3 navigation: compact bottom bar; medium/expanded rail
- Stable routes: Dashboard, Vouchers, Parties, and Settings (presented as More)
- New Sale action appears only on top-level routes
- Sub-screens navigated via `navController.navigate(route)`
- Arguments passed via route parameters (e.g., `voucherId`, `partyId`)
- Custom transitions: slide enter/exit from `ui/transitions/`

## Data flow: Voucher posting

```
User saves voucher
  → AppRepository.saveVoucher()
    → Insert Voucher to vouchers table
    → Insert VoucherItems to voucher_items table
    → If POSTED: create ledger entries, update party/product balances, and create linked bank/cash, bill, and allocation records
    → If DRAFT: persist entered form data only; defer financial and inventory side effects

Journal drafts store their editable DR/CR rows in `journal_draft_lines`; posting
replaces those rows with ledger entries inside the repository transaction.
```

## Key design decisions

See [DECISIONS.md](DECISIONS.md) for rationale on architectural choices.
