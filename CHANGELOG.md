# CHANGELOG.md — Mavo

> Last updated: 2026-10-05  
> Version source of truth: `app/build.gradle.kts` → `versionName`  
> In-app source: `app/src/main/assets/changelog.json`

---

## [Unreleased]

### Added
- Folder/Dossier filing visual components (`FolderTab`, `FolderDossier`, `SubFileRow`) in `FolderComponents.kt` giving accounting screens a physical filing binder look.

### Changed
- Removed checkered graph paper grid lines across all screens (`GraphPaperCanvas.kt` & `GraphPaperBackground.kt`); background now renders as seamless solid warm cafe paper without overlaying cards or controls.
- Tuned Material 3 motion: 100–120 ms screen fades, 0.97 press scale, subtle spring damping without UI lag.
- Verified mobile touch targets and font-scaling protection on tabular data.
- Complete visual UI redesign following the Golden Ratio (60:30:10) and cozy Cafe aesthetic:
  - 60% Base: Warm latte cream paper (`#FBF8F4`).
  - 30% Structure: Espresso and mocha typography & borders (`#2E1F16` / `#5D4432`).
  - 10% Accent: Caramel terracotta punch (`#C86D44`) for primary actions, folder tabs, and status badges.
- Upgraded `AdaptiveNavigationBar` and `AdaptiveNavigationRail` to clean pill-shaped folder selectors with floating action button.
- Replaced cold graph-paper grid with eye-friendly warm cafe paper canvas.
- Updated `AppTheme` runtime definitions and text field styling.
DB migration: no
- Mavo redesign specified in Penpot only (pages **Mavo · Redesign** and **Mavo · System**): all 36 retail POS/accounting screens in a shared Material 3 Expressive design system — custom palette (ink #171A16, paper #F4EFE3, card #FFFDF8, forest #0B5D4B, mint #C8F2E3, amber #E7A63A, coral #D95C4A, blue #4E7BE8, line #E1D8C4), DynaPuff / Tomorrow / Playwrite PL typography, 77 custom SVG icons, and custom components (floating dock, expressive buttons, filled fields, segmented controls, chips, ledger cards, metric tiles, tables, bottom sheets, empty states, status chips, logo tile, graph-paper background). Motion rules: press scale 0.97 @ 120 ms ease-out, page/sheet transitions 220–280 ms, transform/opacity only, reduced-motion respected. Original **Mavo UI mockups** boards preserved intact; no Android code or DB schema changed.

### Changed
- Reworked the shared Android visual system into a warm cafe palette: parchment background, cream surfaces, cocoa primary actions, muted status colors, cleaner cards, and a plain background canvas
- Updated adaptive navigation, search, fields, buttons, and cards to use the cafe hierarchy while preserving existing routes and local data behavior
- Dashboard KPI cards now use a responsive two/three-column bento grid by default; each metric remains tappable and opens its existing analytics sheet
- Full-app UI polish pass (nine passes): all spacing snapped to a 4 dp grid, one corner-radius scale (4/8/12/16 cards and inputs, 24/28/32 hero, 999 pill), off-scale text sizes and Black/ExtraBold weights normalized, tracked-uppercase labels removed, toasts and helper copy de-shouted (no exclamation marks, no hype), status badges and voucher chips read from one token set, colored glows/pulses and Dashboard spotlight rotation removed, and the graph-paper overlay redrawn to the shared 16 dp grid
- Theme picker now offers four light accents — **Ink (purple) retired**; Saffron recolored to `#A85F00` and Slate to `#4D4A43` so the choices are distinct instead of four near-identical teals; saved Night/Ink/Purple selections resolve to Zero
- Shared graph-paper canvas and refreshed Material 3 layouts across existing screens; saved Night selections resolve to Zero
- New app logo: MAVO mark cropped, background-transparent, recolored to brand ink, and compressed (~227 KB of duplicate logo PNGs → ~26 KB of correctly-sized assets); used on the launcher icon, splash/init screens (tinted theme-aware), Dashboard header lockup, and Settings → About
- First-run location permission now auto-fills PIN code and city into the setup draft (previously state code only); PIN field remains editable
- Dead code purge: ~1,350 lines of unreachable composables, wrappers, and commented-out UI removed (Settings, Dashboard, Reports, Ledger, InvoiceGenerator, FilePicker, ZeroUi, ThemeHelpers)
- Second dead-code purge: ~1,050 more lines deleted — the dead `if (false && …)` business-profile block in Settings (449 lines), 19 unreachable `EmailReminderScheduler` members, 9 zero-caller invoice/PDF/PIN functions, an unreachable quick-add auto-assign block, dead `isSavingAndPrinting`/`capBalance` locals, two unreachable dashboard filter enum entries, 77 raw hex/grey literal colors mapped to design tokens, and the tool-stamped "Bolt ⚡" comments stripped
- Phase-B dedupe batch 1: single strict `escapeHtml` (`DocumentHtmlComponents`) for all HTML; state-code maps collapsed to `Utils` as single source (dropdown now uses GST-era code 37 for `Andhra Pradesh`); Setup wizard GSTIN field routes through the shared regex-validated `parseGstinInput`
- Phase-C dedupe batch 2: one shared A4 base CSS for all 7 document generators (dead `buildCommonStyles`/`buildA4Styles` removed), Quotation and ProForma merged into one `quotationFamilyHtml` renderer, single amount-in-words and terms-lines helper, shared `PinAutoFillEffect`/`IfscVerifyEffect` composables replacing 5 hand-rolled effects
- Money in Reports and Ledger lists now uses Indian digit grouping via `Utils.formatIndianCurrency` / `formatIndianNumber` (was western `1,234,567.00`)
- Custom typography system: headers and headlines now use bundled `Playwrite Polska`, while all titles, body text, and labels use bundled `DynaPuff`. Numerics and currency figures use bundled `Tomorrow` (`NumericFontFamily`, replacing `FontFamily.Default` / `FontFamily.Monospace`).
- Device override protection: `MavoTheme` now locks `LocalDensity` `fontScale = 1.0f` to prevent system/device display and font scaling from overriding app typography hierarchy or breaking retail layouts.
- New standing anti-slop guide `docs/AI slop.md` (wired into `AGENTS.md`); crash logs and regenerable tool output gitignored

### Removed
- Duplicate logo assets: `logo_transparent.png`, orphaned `ic_launcher_background.xml` vector, all `ic_launcher_round.png` mipmaps and the round adaptive icon (`android:roundIcon` dropped from the manifest)
- JVM crash dumps (`hs_err_pid*.log`, `replay_pid*.log`), empty `feature/` + `core/` package scaffolds, root scratch files (`analyse.md`, `ft-anotate.md`)

DB migration: no

---

## [2.2.1] — 2026-10-05

### Added
- Save Draft action throughout the phone voucher wizard, including partial Journal rows

### Changed
- App shell and screens now adapt to phone, medium, and expanded tablet windows with Material 3 navigation, panes, and width constraints
- Room-backed lists reuse process-local cached snapshots and show distinct loading, empty, and retryable error states
- New Journal, Income, and Expense vouchers use the shared Details → Items → Review flow with account/category-specific inputs and summaries

### Fixed
- Setting Android animator duration scale to zero now disables the custom motion system

DB migration: yes (Journal draft lines, schema v15 → v16)

---

## [2.2.3] — 2026-09-XX

### Added
- First-run wizard split into nine steps: launch, initialising, two onboarding screens, a Terms & Privacy page with a consent checkbox, an optional location-permission screen, then three setup steps (Basic details, Where you are, Tax & bank)
- Alternate phone number, business type (Manufacturer / Wholesaler / Retailer) and what you sell (Products / Services) captured during setup
- GST toggle on setup step 2: the GST state code, GSTIN and PAN fields only appear for GST-registered businesses
- Income vouchers now work as a full voucher type, mirroring Expense workflows for posting and reporting
- Debit Note and Credit Note entries now post to ledger accounts with dedicated handling
- Data Management screen (Settings → Data): local database stats, Backup now, Restore from backup with confirmation, CSV export/import, and an Audit Log viewer for financial-year closes
- Settings menu now links to Party Master and Chart of Accounts directly
- Low & Out of Stock inventory alerts screen (reached from the Dashboard stock alert or the Products banner): item list with Reorder pills and a Create purchase order action that opens a Purchase voucher
- Search HSN/SAC dialog with live keyword results and a Use-code confirmation, replacing the old name-only HSN picker in Products and Quick Add
- Voucher detail screen (`voucher_detail/{id}`): totals, line items, status pill and Edit / Share / PDF / Print actions — reached by tapping a voucher row
- Multi-select delete confirmation bottom sheet showing voucher count, ledger entries to reverse and total value; CSV export of selected vouchers from the selection bar

### Changed
- Splash, initialising screen and the whole first-run flow restyled to the Zero design language: dark icon tile + wordmark splash, "INITIALIZING" progress bar, card onboarding pages with pager dots and pill buttons, Terms & Privacy page with document rows and agreement checkbox, circle-back "Step N of 3" wizard header with segment progress and pill CTA; the third onboarding screen was merged into the Terms page
- The single setup form is now a three-step flow with a shared wizard shell, Back navigation between steps, and draft state that survives moving backwards
- Location permission is requested on its own screen with a rationale before setup, instead of silently during setup
- Existing installs skip the wizard entirely and open the dashboard directly
- Party-related ledger entries now consistently populate `partyId` from the start
- Counter billing now adapts to compact, medium, and expanded windows
- Expanded windows keep products and cart/payment visible in a split pane
- Settings, Business Profile, Financial Year and voucher-entry screens restyled to the Zero design language (big titles, grouped section cards, circular header buttons, black pill bottom action bars) on phones; tablet layout is unchanged
- Product entry form and Stock Summary restyled to the Zero design language on phones: WizardHeader with back/close circles, two-column labeled fields, pinned Cancel / Save bar, stat cards, status pills; Stock Summary search is now behind a header icon and CSV export moved to the list label row
- Voucher list multi-select restyled on phones: black selection bar with row checkboxes, Select/Deselect all menu and Export / Delete tiles; tapping a row now opens the new detail screen instead of the editor; voucher edit mode header reads "EDITING" instead of "NEW …"
- Every voucher type now enters through the same 3-step wizard (Details → Items → Review) on phones — Receipt, Payment, Quotation, Orders, Challans, Notes, Inquiry and Petty Cash included — while edit mode is a single page under an "EDITING" header
- Wizard chrome matched to the Zero mockups: "New Purchase" / "New GRN" / "New Inquiry RFQ" titles, 4dp step-progress segments, "Voucher No." and "Narration" field labels, edit title "Editing voucher" with the party name in the subtitle, and shared text fields switched to the label-above uppercase style (RetailTextField now delegates to ZbField)
- Press feedback (scale-on-press with shared interaction sources) added to every ZeroKit control: circle icon buttons, primary/secondary buttons, chips, segmented tabs, cards, search fields and empty-state pills

### Fixed
- Quotation, Delivery Challan, Sales/Purchase Order, Proforma, GRN, Debit/Credit/Material/Rejection notes could not be saved: validation required at least one line item but those types had no Add Item UI. Every item-backed type now gets the line-items panel, driven by one shared `itemlessVoucherTypes` set
- Database upgrade from pre-rebrand installs now retires the old database file after a successful copy, and a failed copy is cleaned up instead of leaving a partial file for Room to open
- Billing screen compilation errors caused by the responsive layout migration
- Financial-year lock checks now protect Expense and Income entries more reliably
- Voucher reference handling now uses a dedicated column instead of fragile narration-based matching
- Balance Sheet no longer force-balances itself; Trial Balance issue corrected
- Crashes when opening Journal, Credit Note, Debit Note, and Income resolved

DB migration: yes (business_profile schema v14 → v15)

---

## [2.2.2] — 2026-09-XX

### Added
- Setup blocks save only when Business Name, Address, or PIN Code is empty, with red field errors and auto-focus
- GST state code auto-detection from device location during Setup
- Live English pending-fields summary at bottom of Setup and Business Profile editing

### Changed
- Phone, Email, GSTIN, PAN, and bank details remain optional in both Setup and Settings

DB migration: no

---

## [2.2.1] — 2026-09-XX

### Changed
- Back gesture shows screen preview
- Voucher type list preserved on back
- Smooth slide animations on navigation

### Fixed
- Build warnings removed for clean build
- Deprecated APIs replaced with stable ones
- Unused imports and variables cleaned up
- Predictive back gesture support added
- Navigation back stack behaves correctly
- Restored shared invoice defaults, file-picker, and multi-select APIs required by active screens
- Corrected business-profile default terms reference that blocked Kotlin compilation

### Performance
- Cleaner build output, zero warnings

DB migration: no

---

## [1.0.0-rc1] — 2026-XX-XX

### Added
- Unified premium motion system for screen transitions, tap feedback, FABs, and interactive surfaces

### Fixed
- Back button follows real in-app screen history instead of collapsing to Dashboard
- Root tab switching preserves expected navigation state

### Changed
- Refined press feedback across cards, action buttons, and primary controls

DB migration: no
