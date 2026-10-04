# SCREENS.md — Mavo

> Last updated: 2026-10-03

## Navigation structure

```
App Launch
  ├─ SplashScreen (brand splash)
  ├─ Initialising (DB init "Initializing Secure Database…" → "Preparing Mavo…")
  ├─ (no profile) → FirstRunFlow wizard
  │     Onboarding 1 → Onboarding 2 → Onboarding 3
  │       → Terms & Privacy (checkbox) → Location permission
  │       → Setup 1 (business) → Setup 2 (location) → Setup 3 (tax & bank)
  │       → Dashboard
  └─ (has profile) → PIN (if enabled) → Dashboard
```

The wizard is not a NavHost route: it is the same `when` gate in `MainActivity.kt`
that used to show `SetupScreen`, so system Back only walks wizard steps.

### Bottom navigation bar (4 tabs)

| Tab | Route | Screen | Icon |
|-----|-------|--------|------|
| Home | `dashboard` | DashboardScreen | Home |
| Vouchers | `vouchers` | VouchersScreen | Assignment |
| Parties | `parties` | PartiesScreen | Group |
| Settings | `settings` | SettingsScreen | Settings |

### Full screen map

| Screen file | Route | Purpose | Entry from | Exit to |
|-------------|-------|---------|------------|---------|
| `SplashScreen.kt` | (initial) | Brand splash on cold start | App launch | Initialising, Setup or Dashboard |
| `MainActivity.kt` (`MainAppEntry` / `AppContent`) | (initial) | Initialising screens: DB init spinner, DB error/retry, "Preparing Mavo…" | App launch | Wizard, PIN or Dashboard |
| `FirstRunFlow.kt` | (initial, no profile) | First-run wizard: step order, Back handling, shared `SetupDraft`, onboarding + T&C + permission steps | Splash (no profile) | Dashboard (after profile save) |
| `SetupScreen.kt` | (wizard steps 1–3) | Setup 1 business details, Setup 2 location & GST, Setup 3 tax & bank | FirstRunFlow | FirstRunFlow → Dashboard |
| `DashboardScreen.kt` | `dashboard` | KPIs, quick actions, business overview | Bottom tab | Any feature screen |
| `VouchersScreen.kt` | `vouchers` | List all vouchers, filter by type, create new | Bottom tab | NewVoucher, Invoice |
| `PartiesScreen.kt` | `parties` | List customers/suppliers, CRUD | Bottom tab | PartyDetail |
| `PartySheets.kt` | (bottom sheet in Parties) | Add/edit party form | PartiesScreen | PartiesScreen |
| `SettingsScreen.kt` | `settings` | Business profile, theme, FY, email, export, about | Bottom tab | Sub-sections inline |
| `ProductsScreen.kt` | `products` | Product catalog, CRUD, stock info | Dashboard quick action | Back to Dashboard |
| `ReportsScreen.kt` | `reports` | Business & financial reports | Dashboard quick action | Back to Dashboard |
| `StockReportScreen.kt` | (inline in Reports) | Stock level report | ReportsScreen | ReportsScreen |
| `BankCashScreen.kt` | `bank_cash` | Cash & bank transactions | Dashboard quick action | Back to Dashboard |
| `ExpensesScreen.kt` | `expenses` | Expense logging and list | Dashboard quick action | Back to Dashboard |
| `IncomeScreen.kt` | (inline) | Income logging and list | Dashboard quick action | Back to Dashboard |
| `LedgerListScreen.kt` | `ledger_books` | Browse ledger accounts and entries | Dashboard quick action | Back to Dashboard |
| `BillingScreen.kt` | `counter_sale` | Responsive counter sale flow; stacked on compact windows and split-pane on expanded windows | Dashboard quick action | Back to Dashboard |
| `NewVoucherScreen` | `new_voucher?voucherId={id}` | Create/edit voucher of any type | VouchersScreen, Dashboard | Invoice, back to Vouchers |
| `VoucherItemSheet.kt` | (bottom sheet in NewVoucher) | Add/edit line item on a voucher | NewVoucherScreen | NewVoucherScreen |
| `InvoiceScreen.kt` | `invoice/{voucherId}` | View generated invoice, share/print | NewVoucher (after save) | Back to Vouchers |
| `PartyDetailScreen` | `party_detail/{partyId}` | Party ledger, transaction history | PartiesScreen | Back to Parties |
| `BarcodeScannerDialog.kt` | (dialog overlay) | Camera barcode/OCR scanning | Products, NewVoucher | Caller screen |
| `BusinessProfileSettingsSection.kt` | (inline in Settings) | Edit business profile after setup | SettingsScreen | SettingsScreen |
| `EmailAutomationSection.kt` | (inline in Settings) | Email rules, accounts, history | SettingsScreen | SettingsScreen |
| `ProfileFormSupport.kt` | (shared form helpers) | Validation, `SetupStep` order, state auto-detect | First-run wizard, Settings | — |
| `SharedComponents.kt` | (shared) | `RetailTextField`, `StateDropdownMenu`, `WizardScaffold`, PIN lookup | Multiple screens | — |
| `ProductOptionalFields.kt` | (inline in Products) | Batch, serial, secondary unit fields | ProductsScreen | ProductsScreen |

## Navigation flow diagram

```mermaid
graph TD
    A["App Launch"] --> B["SplashScreen"]
    B --> I["Initialising (DB + setup status)"]
    I -->|no profile| W1["FirstRunFlow: Onboarding 1-3"]
    W1 --> W2["Terms & Privacy (checkbox)"]
    W2 --> W3["Location permission"]
    W3 --> W4["Setup 1: business details"]
    W4 --> W5["Setup 2: location & GST toggle"]
    W5 --> W6["Setup 3: tax & bank"]
    W6 --> D["DashboardScreen"]
    I -->|has profile| P["PIN lock (if enabled)"]
    P --> D
    D --> E["VouchersScreen"]
    D --> F["PartiesScreen"]
    D --> G["SettingsScreen"]
    D --> H["ProductsScreen"]
    D --> I2["ReportsScreen"]
    D --> J["BankCashScreen"]
    D --> K["ExpensesScreen"]
    D --> L["LedgerListScreen"]
    D --> M["BillingScreen (Counter Sale)"]
    E --> N["NewVoucherScreen"]
    N --> O["InvoiceScreen"]
    F --> P2["PartyDetailScreen"]
```
