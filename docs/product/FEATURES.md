# FEATURES.md — Mavo

> Last updated: 2026-10-03

Status key: ✅ Done · 🟡 Partial · 📋 Planned

## Billing & Invoicing

| Feature | Status | Notes |
|---------|--------|-------|
| Sale voucher (Tax Invoice) | ✅ | Auto CGST/SGST/IGST based on state code comparison |
| Purchase voucher | ✅ | |
| Sale Return | ✅ | Reverses stock and ledger entries |
| Purchase Return | ✅ | |
| Receipt voucher | ✅ | Payment received from party |
| Payment voucher | ✅ | Payment made to party |
| Debit Note | ✅ | Posts to ledger accounts |
| Credit Note | ✅ | Posts to ledger accounts |
| Quotation | ✅ | Non-posting voucher |
| Proforma Invoice | ✅ | Non-posting voucher |
| Delivery Challan | ✅ | |
| Journal Entry | ✅ | Multi-line DR/CR |
| Bills Receivable / Payable | ✅ | Auto-created from sale/purchase vouchers |
| Sales Order | ✅ | |
| Purchase Order | ✅ | |
| Goods Receipt Note | ✅ | |
| Material Note | ✅ | |
| Rejection Note | ✅ | |
| Petty Cash | ✅ | |
| Income voucher | ✅ | Full voucher type |
| Expense voucher | ✅ | Full voucher type |
| Quick Sale (counter sale) | 🟡 | Screen exists, minimal implementation |
| Additional charges on vouchers | ✅ | JSON-serialized in voucher |
| Transport details on vouchers | ✅ | Transporter name, LR no, vehicle, GSTIN, destination |
| Round-off calculation | ✅ | Automatic |
| Voucher numbering (auto-increment) | ✅ | Per type, per FY |
| Voucher draft / posted status | ✅ | |
| Voucher entry 3-step wizard | ✅ | Details → Items → Review for every voucher type on phones; edit opens as a single page with an "EDITING" header |
| Discount (percent / amount) | ✅ | Per line item |
| Voucher detail screen | ✅ | Zero summary with totals, line items, Edit/Share/PDF/Print actions (phones) |
| Multi-select batch actions | ✅ | Black selection bar: Select/Deselect all, CSV export, delete (phones) |
| Delete with reversal stats | ✅ | Bottom sheet shows voucher count, ledger entries reversed, total value |

## Party & Ledger Management

| Feature | Status | Notes |
|---------|--------|-------|
| Party CRUD (Customer / Supplier / Both) | ✅ | |
| Party detail view with transaction history | ✅ | |
| Running balance per party | ✅ | DR/CR with FY-wise opening balances |
| Credit limit and credit days | ✅ | |
| Loyalty points, birthday, anniversary | ✅ | Fields present, not used in logic |
| Ledger accounts (chart of accounts) | ✅ | System + user-created |
| Ledger entries (double-entry) | ✅ | Auto-posted from vouchers |
| FY-wise party balance carry-forward | ✅ | |
| FY-wise ledger account balance carry-forward | ✅ | |
| Receipt allocation against invoices | ✅ | |
| Outstanding amount tracking | ✅ | |

## Inventory / Products

| Feature | Status | Notes |
|---------|--------|-------|
| Product CRUD | ✅ | Zero "New Product" entry form (phones) |
| HSN code lookup (built-in database) | ✅ | `HsnLookup.kt`; Search HSN/SAC dialog with keyword search + Use-code confirm |
| Stock tracking (current stock) | ✅ | Updated on voucher post |
| Low stock alerts | ✅ | Configurable threshold |
| Low & Out of Stock alert screen | ✅ | From Dashboard alert or Products banner; Reorder / Create purchase order opens a Purchase voucher |
| Multiple units (PCS, KG, LTR, MTR, BOX, BAG, NOS) | ✅ | |
| Secondary unit + conversion factor | ✅ | |
| Barcode value field | ✅ | |
| Batch number / expiry date fields | ✅ | Fields present |
| Stock report | ✅ | Stock Summary: SKU / stock value / low-out stats, search toggle, filter chips, status pills |
| FY-wise product stock carry-forward | ✅ | |

## Bank & Cash

| Feature | Status | Notes |
|---------|--------|-------|
| Cash transactions | ✅ | |
| Bank transactions (NEFT, RTGS, IMPS, cheque, UPI) | ✅ | |
| Auto-linked to voucher (sourceVoucherId) | ✅ | |
| Receipt image attachment | ✅ | |

## Expenses & Incomes

| Feature | Status | Notes |
|---------|--------|-------|
| Expense logging with categories | ✅ | Separate from trading vouchers |
| Income logging with categories | ✅ | |
| Attachment support | ✅ | |
| FY-scoped | ✅ | |

## Reports

| Feature | Status | Notes |
|---------|--------|-------|
| Business reports (sales, purchases, P&L, balance sheet, trial balance) | ✅ | |
| Stock report | ✅ | Stock Summary (Zero restyle) |
| Day book | ✅ | |
| Party-wise reports | ✅ | |
| GST summary | ✅ | |
| Aging / overdue reports | ✅ | |

## Camera / Scanning

| Feature | Status | Notes |
|---------|--------|-------|
| Barcode scanning (ML Kit) | ✅ | CameraX integration |
| OCR text recognition (ML Kit) | ✅ | For purchase bills |
| OCR-based auto data extraction | 📋 | Planned |

## Email & Communication

| Feature | Status | Notes |
|---------|--------|-------|
| Email composer (JavaMail) | ✅ | Send invoices/reports |
| Google OAuth email account linking | ✅ | |
| Email automation rules per customer | ✅ | Schedule, frequency, template |
| Email reminders (WorkManager) | ✅ | Scheduled background send |
| Email history log | ✅ | |

## Invoice / Document Generation

| Feature | Status | Notes |
|---------|--------|-------|
| PDF invoice generation (HTML → WebView → PDF) | ✅ | `InvoiceGenerator.kt` |
| Configurable invoice layout | ✅ | Logo, signature, bank details, UPI QR, terms |
| CSV export / import | ✅ | `CsvTransferManager.kt` |

## UI / Theme

| Feature | Status | Notes |
|---------|--------|-------|
| 5 color themes (Saffron, Slate, Ledger, Ink, Night) | ✅ | Material 3 |
| Dark mode (Night theme) | ✅ | |
| Skeleton loading states | ✅ | |
| Premium motion system (transitions, press feedback) | ✅ | |
| Splash screen | ✅ | AndroidX SplashScreen; Zero restyle: dark icon tile + "Mavo" wordmark on light bg |
| Initialising screen | ✅ | DB init + setup-status gate: Zero logo tile, "INITIALIZING", progress bar |
| Progress tracker (dashboard KPI) | ✅ | Configurable metric/period/target |

## Financial Year Management

| Feature | Status | Notes |
|---------|--------|-------|
| FY creation and switching | ✅ | Settings → Financial Year → Switch financial year (inline start-year field, Save changes) |
| FY close / lock | ✅ | Settings → Financial Year → Close current books: confirm dialog, locks source year, auto-advances active year |
| Balance carry-forward (parties, products, ledger accounts) | ✅ | |
| Audit log for FY transitions | ✅ | Viewable at Settings → Data Management → Audit log |

## Data Management

| Feature | Status | Notes |
|---------|--------|-------|
| Setup screen (first-run onboarding) | ✅ | 3 steps: Basic details, Where you are, Tax & bank |
| First-run wizard | ✅ | Onboarding ×2 → Terms page → permission → 3 setup steps (`FirstRunFlow.kt`), Zero restyle |
| Terms & privacy consent | ✅ | Terms page: document rows + agreement checkbox gates the wizard until accepted |
| Location permission step | ✅ | Optional GST state code auto-detect; skippable |
| Business type & selling type | ✅ | Manufacturer/Wholesaler/Retailer · Products/Services |
| Settings screen | ✅ | Zero-style grouped menu: Profile & master data, Appearance, Financial, Data sections |
| Data management screen | ✅ | Settings → Data Management: DB size/records, backup, restore, CSV export/import, audit log viewer |
| In-app changelog (What's New) | ✅ | Reads from `changelog.json` |
| Version display | ✅ | From `BuildConfig.VERSION_NAME` |
