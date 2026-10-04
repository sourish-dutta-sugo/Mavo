# USER_FLOWS.md — Mavo

> Last updated: 2026-10-03

## Flow 1: First launch (wizard)

Runs only when no `business_profile` row exists; existing installs skip straight
to PIN (if enabled) → Dashboard. System Back returns one step at a time.

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | App opens, profile absent | SplashScreen → Initialising | `businessProfileDao.getProfile()` null → start wizard | Wizard step 1 | — |
| 2 | Onboarding 1 / 2 / 3 | Static explainers: what Mavo is, what it does, a quick note | — | Next → Terms | — |
| 3 | Terms & Privacy | T&C + Privacy text with one checkbox | Local text | Continue enabled only when ticked | Unticked → "Tick the box to continue." |
| 4 | Location permission | Rationale card, Allow / Skip | `play-services-location` (optional) | Grant → state code auto-detected into draft; Skip → manual pick | Denied → manual state entry |
| 5 | Setup 1 | Business Name*, Owner, Phone, Alt phone, Email, Address*, business type, what you sell | Empty `SetupDraft` | Continue enabled when Business Name + Address filled | Hint lists missing fields |
| 6 | Setup 2 | PIN*, City, GST toggle, State, (GST State Code if toggle on) | `fetchPinLookup` after 6 digits, `StateDropdownMenu` | PIN auto-fills city/state; GST toggle reveals state code | Lookup fails → "Unable to fetch location" |
| 7 | Setup 3 | GSTIN + PAN (only if GST on), Account No, IFSC, Bank, Branch | `fetchGstinDetails`, `fetchIfscDetails` | "Finish Setup" → sample-data dialog | IFSC unresolved → field left as typed |
| 8 | Sample-data choice | Yes, Import / No, Start Clean | `businessProfileDao.insertProfile(profile)` | Profile saved → `isSetupCompleted` → Dashboard | DB error → DB error card on relaunch |

## Flow 2: Create a sale voucher

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Tap "Vouchers" tab or "+" FAB | VouchersScreen / voucher type selector | — | Shows voucher type list | — |
| 2 | Select "Sale" type | NewVoucherScreen in SALE mode | Auto-generated voucher number (`getLatestVoucherNo`) | Empty sale form with auto-number | — |
| 3 | Select party | Party picker sheet | `partyDao.getAllParties()` | Party filled, state code compared for IGST | No parties → prompt to create |
| 4 | Add line item | VoucherItemSheet opens | `productDao.getAllProducts()` | Select product → auto-fill HSN, rate, GST% | No products → prompt to create |
| 5 | Set qty, rate, discount | Real-time calculation in sheet | Item fields | Taxable amount + GST computed live | Qty=0 → validation error |
| 6 | Confirm item | Item added to voucher line list | — | Updates totals (taxable, CGST, SGST/IGST, round-off, net) | — |
| 7 | Set payment mode | Cash / Bank / Cheque / UPI selector | — | Payment details recorded | — |
| 8 | Tap "Save" | Voucher posting | All voucher data | (a) Voucher saved, (b) Ledger entries created (double-entry), (c) Stock updated, (d) Bank/cash transaction created, (e) Bills receivable created | FY locked → error toast |
| 9 | After save | InvoiceScreen opens | Saved voucher + items + business profile | PDF invoice rendered via WebView → ready to share/print | — |

## Flow 3: Record a payment received (Receipt)

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Select "Receipt" voucher type | NewVoucherScreen in RECEIPT mode | — | Receipt form | — |
| 2 | Select party | Party picker | Parties list | Party selected, outstanding shown | — |
| 3 | Enter amount | Amount field | — | Net amount set | — |
| 4 | Allocate against invoices | Allocation section | `billReceivableDao.getAllBills()` for party | Outstanding amounts reduced | Over-allocation → validation |
| 5 | Save | Post receipt | — | Ledger entries (DR Cash/Bank, CR Party), outstanding updated, bank/cash transaction created | — |

## Flow 4: View reports

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Tap "Reports" from Dashboard | ReportsScreen | — | Report type list/tabs | — |
| 2 | Select report (e.g., P&L) | Computed report view | `ledgerEntriesForYear`, `vouchersForYear`, `expensesForYear` | Calculated totals rendered | Empty data → empty state message |
| 3 | Date/period filter | Filter controls | — | Report recalculated for selected range | — |

## Flow 5: Manage products

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Tap "Products" from Dashboard | ProductsScreen | `productDao.getAllProducts()` | Product list with stock info | Empty → empty state |
| 2 | Tap "+" to add | Add product form (bottom sheet) | — | Form: name, HSN (with lookup), unit, rates, GST%, opening stock | — |
| 3 | HSN lookup | User types HSN or description | Built-in `HsnLookup.kt` data | Auto-suggest HSN codes + descriptions | No match → manual entry |
| 4 | Save product | `productDao.insertProduct(product)` | Form data | Product in catalog, available for vouchers | Duplicate name → replace (upsert) |
| 5 | Barcode scan | Camera dialog | ML Kit barcode scanning | Barcode value auto-filled | Camera permission denied → manual |

## Flow 6: Financial year management

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Settings → Financial Year section | FY list | `financialYearDao.getAllFinancialYears()` | All FYs with open/closed/locked status | — |
| 2 | Create new FY | FY creation dialog | Previous FY data | New FY record, auto-computes dates | — |
| 3 | Close FY | Close confirmation | Current FY's balances | (a) FY marked closed, (b) Balances carried forward to new FY, (c) Audit log created | — |
| 4 | Lock FY | Lock confirmation | — | `isLocked = true`, prevents any voucher changes in that FY | Already locked → no-op |

## Flow 7: Email invoice to customer

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | From InvoiceScreen, tap "Email" | Email composer | Voucher data, party email | Pre-filled subject, body, PDF attachment | No party email → prompt |
| 2 | Send | JavaMail send via SMTP or OAuth | SMTP config from BusinessProfile or EmailAccount | Email sent, logged to `email_history` | SMTP auth failure → error shown |
| 3 | (Optional) Set automation rule | EmailAutomationSection in Settings | Party info | Rule created: schedule, frequency, template | — |
| 4 | Scheduled send | WorkManager fires | Automation rules | Email sent at configured time | WorkManager constraint not met → retry |
