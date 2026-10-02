# ARCHITECTURE.md — ZeroBook

> Last updated: 2026-09-29

## Folder structure

```
app/src/main/java/com/zerobook/app/
├── MainActivity.kt          # Single Activity, NavHost, bottom nav, theme setup
├── core/
│   ├── db/                   # Database utilities
│   ├── export/               # Export helpers
│   ├── preferences/          # DataStore preference wrappers
│   ├── print/                # Print utilities
│   ├── scheduler/            # Scheduling helpers
│   ├── share/                # Share intents
│   └── util/                 # General utilities
├── data/
│   ├── AppDatabase.kt        # Room database class (version 14, 20 entities)
│   ├── AppDaos.kt            # All DAO interfaces
│   ├── AppRepository.kt      # Single repository (all business logic)
│   ├── Entities.kt           # All Room entity data classes
│   ├── Migrations.kt         # DB migrations (v4→v14) + ensureColumn helpers
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
│   ├── onboarding/           # (setup/onboarding logic)
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
│   ├── AppViewModel.kt       # Main ViewModel (DB init, all state)
│   ├── DashboardViewModel.kt # Dashboard-specific ViewModel
│   ├── screens/              # 23 Compose screen files (see ../design/SCREENS.md)
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

### Responsive Compose layout

The billing feature uses Material 3 window size classes rather than raw screen
width checks. Compact and medium windows stack product and cart panes; expanded
windows place them in a persistent split pane. `ResponsivePaneLayout` owns only
measurement and placement, while `BillingScreen` retains cart state and
business actions.

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

## Navigation

- Single `NavHost` in `MainActivity.kt`
- Routes defined in `Routes` object (string constants)
- 4 bottom tabs: Dashboard, Vouchers, Parties, Settings
- Sub-screens navigated via `navController.navigate(route)`
- Arguments passed via route parameters (e.g., `voucherId`, `partyId`)
- Custom transitions: slide enter/exit from `ui/transitions/`

## Data flow: Voucher posting

```
User saves voucher
  → AppRepository.saveVoucher()
    → Insert Voucher to vouchers table
    → Insert VoucherItems to voucher_items table
    → Create LedgerEntries (double-entry: DR + CR)
    → Update Party running balance
    → Update Product stock (currentStock)
    → Create BankCashTransaction
    → Create/update BillReceivable (for sales)
    → Create ReceiptAllocations (for receipts)
```

## Key design decisions

See [DECISIONS.md](DECISIONS.md) for rationale on architectural choices.
