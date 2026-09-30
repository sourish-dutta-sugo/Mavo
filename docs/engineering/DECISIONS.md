# DECISIONS.md — ZeroBook

> Last updated: 2026-09-29  
> Purpose: Document why specific technical choices were made so agents don't undo them.

## D1: Single Activity + Jetpack Navigation Compose

**Chose:** Single `MainActivity` with `NavHost` and route-based navigation.  
**Over:** Multiple Activities, or Fragment-based navigation.  
**Why:** Compose Navigation is the modern Android standard. Single Activity avoids Activity lifecycle complexity. Bottom nav tabs map cleanly to composable routes. Custom slide transitions are applied uniformly.

## D2: Single AppRepository (God repository)

**Chose:** One large `AppRepository.kt` (101 KB) handling all business logic.  
**Over:** Multiple feature-specific repositories.  
**Why:** This is a single-developer project with tightly coupled business logic (voucher posting touches parties, ledgers, stock, bank/cash, and bills). Splitting would create circular dependencies or require a coordinator. The single-repo approach keeps transactional operations atomic and simple. May be refactored in the KMP rebuild.

## D3: Room (SQLite) for local-only storage

**Chose:** Room with local SQLite database, no cloud sync.  
**Over:** Firebase, Supabase, or cloud-synced solutions.  
**Why:** Target users (small Indian retailers) often have unreliable internet. Offline-first is a hard requirement. Room is the standard Android local DB. SQLite is battle-tested for accounting data. Cloud sync adds complexity, cost, and privacy concerns.

## D4: Defensive migrations with ensureColumn pattern

**Chose:** `ensureColumn` helpers that check `PRAGMA table_info` before `ALTER TABLE`.  
**Over:** Standard Room `Migration` objects alone.  
**Why:** Room migrations can fail if the user skips versions or if the database was modified outside the app. The `ensure*` functions run on both `onCreate` and `onOpen`, guaranteeing schema integrity regardless of upgrade path. This pattern was born from real-world crashes during development.

## D5: Financial year scoping on all data

**Chose:** `financialYearCode` column on vouchers, items, ledger entries, bank transactions, expenses, incomes, and bills.  
**Over:** Global data with date-range filtering.  
**Why:** Indian accounting is legally FY-based (April–March). Carry-forward of balances, FY close/lock, and FY-wise reporting all require explicit FY scoping. Date filtering alone doesn't handle opening balances correctly.

## D6: 5 named themes (not dynamic Material You)

**Chose:** 5 hand-crafted themes (Saffron, Slate, Ledger, Ink, Night) stored in `AppTheme` data class.  
**Over:** Dynamic color from wallpaper (Material You / dynamic color).  
**Why:** Brand consistency matters for a professional accounting app. Dynamic color can produce unpredictable combinations that clash with semantic colors (red=debit, green=credit). The curated themes ensure readability of financial data in all modes.

## D7: HTML → WebView → PDF for invoices

**Chose:** Build HTML string in Kotlin → load into WebView → print to PDF.  
**Over:** PDF libraries (iText, Apache PDFBox), or server-side generation.  
**Why:** WebView rendering is free (no library cost/size), handles complex layouts naturally (tables, images, QR codes), and the HTML template is easy to maintain and customize. Server-side generation contradicts the offline-first design. PDF libraries add significant APK size.

## D8: JavaMail for email (not Intents)

**Chose:** Direct SMTP sending via JavaMail library + Google OAuth.  
**Over:** `Intent.ACTION_SEND` to delegate to user's email app.  
**Why:** Automated email reminders (via WorkManager) cannot use Intents — they require direct sending without user interaction. The automation feature is a key differentiator.

## D9: versionName as 3-segment (MAJOR.FEATURE.MINOR)

**Chose:** Three-segment version string (e.g., `2.2.1`).  
**Over:** Four-segment or semver.  
**Why:** Maps naturally to the versioning rules in AGENTS.md. `versionCode` (integer) is separate and used only for Play Store ordering.

## D10: No dependency injection framework

**Chose:** Manual dependency creation in ViewModels/Activities.  
**Over:** Hilt, Koin, or Dagger.  
**Why:** Single-activity, single-developer project. The dependency graph is simple (DB → DAOs → Repository → ViewModel). DI frameworks add build complexity and boilerplate without proportional benefit at this scale.

## D11: exportSchema = false

**Chose:** `@Database(exportSchema = false)`.  
**Over:** Exporting Room schemas to JSON for CI validation.  
**Why:** Schema validation is handled by the defensive `ensure*` functions and manual migration testing. Schema export adds CI complexity that isn't needed for a solo project. May change if the project grows.
