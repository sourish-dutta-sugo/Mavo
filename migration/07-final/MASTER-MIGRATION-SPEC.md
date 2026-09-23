# ZeroBook Master Migration Specification

> Reverse-engineered from Android source at `fc5dae3` on 2026-09-23. This document describes current behavior only.

## 1. Product overview

ZeroBook is a local-first Android accounting and GST invoicing app for small Indian retailers. It manages business profile, parties, products, vouchers, ledgers, stock, cash/bank, reports, document output, and email reminders.

## 2. Feature inventory

Setup; dashboard; voucher list/create/edit; counter sale; parties and party ledger; products, HSN and barcode; reports and stock reports; bank/cash; expenses/income; ledger books; invoice/document rendering; CSV transfer; email/OAuth/SMTP; reminders; themes; PIN lock.

## 3. Workflows

Launch -> database state -> splash -> setup or PIN -> dashboard.  
Dashboard/feature route -> form/list -> ViewModel -> repository -> Room -> reactive UI.  
Voucher -> document renderer -> PDF/file share/email.  
Receivable -> reminder scheduler -> SMTP email -> email history.

## 4. Business rules

Financial-year code scopes transactions and balances. Voucher items retain snapshots. Document metadata controls draft/ledger posting intent. Receipt allocations recalculate outstanding. Financial-year close/lock carries balances and logs audit events. Exact formula details are **UNKNOWN**.

## 5. Screens and navigation

Routes: `dashboard`, `vouchers`, `parties`, `settings`, `reports`, `ledger_books`, `expenses`, `counter_sale`, `products`, `bank_cash`, `new_voucher`, `invoice/{voucherId}`, `party_detail/{partyId}`. Startup also exposes splash, setup, PIN, database-error, scanner, income, and stock-report surfaces.

## 6. UI hierarchy and components

Material 3 Compose screens use shared components, sheets, selection controllers, skeletons, loading indicators, theme tokens, and transition helpers. Exact per-screen hierarchy is listed in [screen-inventory.md](../04-ui-ux/screen-inventory.md).

## 7. Design system

Warm neutral surface (`#F8F7F4`), white cards, teal brand (`#1A5C4B` family), gold accent, semantic green/red/orange/indigo. Spacing 4/8/12/16/24/32; radius 4/8/12/14/20/full; elevation 2/4/8/12; type tokens 11–32 sp. Five runtime themes.

## 8. Data model

Room version 14 includes financial years, business profile, parties and FY balances, products and FY balances, vouchers/items, ledger entries/accounts, bank/cash, receipt allocations, bills receivable, audit logs, expenses, income, email accounts/rules/history.

## 9. Architecture

Single Android module. `MainActivity` composes navigation and ViewModels. ViewModels expose StateFlows. `AppRepository` wraps DAOs and accounting operations. Room persists SQLite. Services bridge document, file, email, camera, and scheduler capabilities.

## 10. Integrations

CameraX/ML Kit, Google sign-in, JavaMail SMTP, WorkManager, Android WebView/PDF, FileProvider, DataStore, SharedPreferences. No verified application CRUD backend.

## 11. Platform-specific behavior

Android permissions, Activity lifecycle, FileProvider URI grants, WebView PDF, WorkManager, JavaMail, Google Play Services, CameraX, and Android storage are platform-specific.

## 12. State behavior

Startup loading/error/success, setup completion, optional PIN, financial-year switching, draft/posted documents, selection mode, loading skeletons, and screen-local form state are verified. Retry/rollback behavior is not fully verified.

## 13. Error/loading/empty states

Database initialization exposes explicit error state. Loading indicators and skeletons exist. Empty lists and inline form errors are implemented across screens, but complete copy and accessibility announcements remain **UNKNOWN**.

## 14. Accessibility and responsive requirements

Android Compose semantics and `adjustResize` are present, but complete accessibility labels, minimum touch targets, tablet layout, desktop layout, and web behavior are **UNKNOWN**. Future implementation must test these explicitly.

## 15. Feature parity requirements

Preserve accounting semantics, FY partitioning, document prefixes/types, GST fields, invoice settings, route flows, local data compatibility, and theme roles. Verify formulas and PDFs against fixtures before replacing storage or renderers.

## 16. Migration constraints

Do not treat README-only Retrofit claims as requirements. Keep platform adapters separate from shared domain logic. Preserve legacy data through an explicit import/migration plan. Implement only document generators currently registered unless product scope expands.

## 17. Known unknowns and risks

See [unknowns.md](../06-migration/unknowns.md) and [migration-risks.md](../06-migration/migration-risks.md). Highest risk: accounting parity, legacy schema compatibility, document rendering, secrets, and absent cross-device sync model.

## 18. Traceability

See [traceability-matrix.md](../01-codebase/traceability-matrix.md), [screen-inventory.md](../04-ui-ux/screen-inventory.md), and [architecture-research.md](../03-architecture/architecture-research.md).
