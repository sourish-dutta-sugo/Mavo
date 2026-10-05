# CHANGELOG.md — Mavo

> Last updated: 2026-10-05  
> Version source of truth: `app/build.gradle.kts` → `versionName`  
> In-app source: `app/src/main/assets/changelog.json`

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
