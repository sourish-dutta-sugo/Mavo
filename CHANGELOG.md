# CHANGELOG.md — Mavo

> Last updated: 2026-09-29  
> Version source of truth: `app/build.gradle.kts` → `versionName`  
> In-app source: `app/src/main/assets/changelog.json`

---

## [2.2.3] — 2026-09-XX

### Added
- Income vouchers now work as a full voucher type, mirroring Expense workflows for posting and reporting
- Debit Note and Credit Note entries now post to ledger accounts with dedicated handling

### Changed
- Party-related ledger entries now consistently populate `partyId` from the start
- Counter billing now adapts to compact, medium, and expanded windows
- Expanded windows keep products and cart/payment visible in a split pane

### Fixed
- Database upgrade from pre-rebrand installs now retires the old database file after a successful copy, and a failed copy is cleaned up instead of leaving a partial file for Room to open
- Billing screen compilation errors caused by the responsive layout migration
- Financial-year lock checks now protect Expense and Income entries more reliably
- Voucher reference handling now uses a dedicated column instead of fragile narration-based matching
- Balance Sheet no longer force-balances itself; Trial Balance issue corrected
- Crashes when opening Journal, Credit Note, Debit Note, and Income resolved

DB migration: no

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
