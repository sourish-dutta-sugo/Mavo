# UI/UX Research

## Startup

Splash and database initialization precede setup. Missing profile opens setup; optional PIN lock can gate the main graph. Database initialization has explicit loading/error/success state in `AppViewModel`.

## Navigation destinations

Top-level: Home/Dashboard, Vouchers, Parties, Settings. Secondary: Reports, Products, Bank/Cash, Expenses, Counter Sale, New/Edit Voucher, Invoice, Ledger Books, Party Detail, Income, Stock Report. Source: `MainActivity.kt:120-142, 555-710`.

## Interaction patterns

- Compose forms and sheets for create/edit flows.
- Filter/search/selection behavior in voucher and party/product surfaces.
- Barcode scanner dialog uses camera integration.
- Shared components and selection controllers reduce repeated list interaction.
- Centralized screen transitions and press/click scale effects provide motion.

## State inventory

| State | Evidence |
|---|---|
| Database loading/error/success | `AppViewModel.kt` |
| Setup incomplete/complete | `MainActivity.kt`, profile flow |
| Empty lists | screen-level list branches |
| Loading skeletons | `ui/theme/Skeleton.kt`, `ui/components/LoadingIndicator.kt` |
| Draft/posted voucher | `Entities.kt`, `DocumentType.kt` |
| Financial-year locked/closed | `FinancialYear`, repository checks |
| Selection mode | `ui/selection/*` |
| PIN locked/unlocked | `MainActivity.kt` |

Exact text, accessibility semantics, and every visual branch are **UNVERIFIED** outside inspected files.
