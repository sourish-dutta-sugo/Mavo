# USER_FLOWS.md — Mavo

> Last updated: 2026-10-05

## Flow 1: First launch (wizard)

Runs only when no `business_profile` row exists; existing installs skip straight
to PIN (if enabled) → Dashboard. System Back returns one step at a time.

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | App opens, profile absent | SplashScreen → Initialising | `businessProfileDao.getProfile()` null → start wizard | Wizard step 1 | — |
| 2 | Onboarding 1 / 2 | Static explainers in a card shell with pager dots and a pill Next (›) button | — | Next → Terms page | — |
| 3 | Terms & Privacy page | Check card, terms/privacy copy, two expandable document rows, agree checkbox, Get Started pill | Local text | Get Started enabled only when ticked | Unticked → button dimmed, tap ignored |
| 4 | Location permission | Rationale card, "Allow location" / "Enter manually" pills | `play-services-location` (optional) | Grant → state code auto-detected into draft; Enter manually → skip detection | Denied → manual state entry |
| 5 | Setup 1 "Basic details" | Business Name*, Owner, Phone, Alt phone, Email, Address*, business type, what you sell | Empty `SetupDraft` | Continue enabled when Business Name + Address filled | Hint lists missing fields |
| 6 | Setup 2 "Where you are" | PIN*, City, GST toggle, State, (GST State Code if toggle on) | `fetchPinLookup` after 6 digits, `StateDropdownMenu` | PIN auto-fills city/state; GST toggle reveals state code | Lookup fails → "Unable to fetch location" |
| 7 | Setup 3 "Tax & bank" | GSTIN + PAN (only if GST on), Account No, IFSC, Bank, Branch | `fetchGstinDetails`, `fetchIfscDetails` | "Finish Setup" → sample-data dialog | IFSC unresolved → field left as typed |
| 8 | Sample-data choice | Yes, Import / No, Start Clean | `businessProfileDao.insertProfile(profile)` | Profile saved → `isSetupCompleted` → Dashboard | DB error → DB error card on relaunch |

## Flow 2: Create a sale voucher

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Tap "Vouchers" tab or "+" FAB | VouchersScreen / voucher type selector | — | Shows voucher type list | — |
| 2 | Select "Sale" type | NewVoucherScreen wizard step 1 (Details) | Auto-generated voucher number (`getLatestVoucherNo`) | Voucher No., Date, Party, Narration form with auto-number | — |
| 3 | Select party | Party picker sheet (step 1) | `partyDao.getAllParties()` | Party filled, state code compared for IGST | No parties → prompt to create |
| 4 | Tap "Continue to items" | Wizard step 2 (Line items & charges) | — | Line-items panel plus payment mode and charges shown | — |
| 5 | Add line item | VoucherItemSheet opens (step 2) | `productDao.getAllProducts()` | Select product → auto-fill HSN, rate, GST% | No products → prompt to create |
| 6 | Set qty, rate, discount | Real-time calculation in sheet | Item fields | Taxable amount + GST computed live | Qty=0 → validation error |
| 7 | Confirm item | Item added to voucher line list | — | Updates totals (taxable, CGST, SGST/IGST, round-off, net) | — |
| 8 | Set payment mode | Cash / Bank / Cheque / UPI selector (step 2) | — | Payment details recorded | — |
| 9 | Tap "Review & post" | Wizard step 3 (Review) | — | Totals and tax breakdown for a final check | — |
| 10 | Tap "Save & Post" | Voucher posting | All voucher data | (a) Voucher saved, (b) Ledger entries created (double-entry), (c) Stock updated, (d) Bank/cash transaction created, (e) Bills receivable created | FY locked → error toast |
| 11 | After save | InvoiceScreen opens | Saved voucher + items + business profile | PDF invoice rendered via WebView → ready to share/print | — |

## Flow 3: Record a payment received (Receipt)

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Select "Receipt" voucher type | NewVoucherScreen wizard step 1 (Details) | — | Receipt details form | — |
| 2 | Select party | Party picker (step 1) | Parties list | Party selected, outstanding shown | — |
| 3 | Tap "Continue to items" | Wizard step 2 (amount panel) | — | Amount field and invoice allocation shown | — |
| 4 | Enter amount | Amount field (step 2) | — | Net amount set | — |
| 5 | Allocate against invoices | Allocation section (step 2) | `billReceivableDao.getAllBills()` for party | Outstanding amounts reduced | Over-allocation → validation |
| 6 | Review & save | Wizard step 3 → post | — | Ledger entries (DR Cash/Bank, CR Party), outstanding updated, bank/cash transaction created | — |

## Flow 4: View reports

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Tap "Reports" from Dashboard | ReportsScreen | — | Report type list/tabs | — |
| 2 | Select report (e.g., P&L) | Computed report view | `ledgerEntriesForYear`, `vouchersForYear`, `expensesForYear` | Calculated totals rendered | Empty data → empty state message |
| 3 | Date/period filter | Filter controls | — | Report recalculated for selected range | — |

## Flow 5: Manage products

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Settings → Products | ProductsScreen | `productDao.getAllProducts()` | Product list with stock info; amber low-stock banner if any item is low/out | Empty → empty state |
| 2 | Tap "+" (FAB) | Full-screen "New Product" entry: WizardHeader (back + close), title + "Master data · Inventory" subtitle, two-column fields, pinned Cancel / Save product bar | — | Fields: name, SKU/barcode + scan, unit, HSN + tax rate, sale/purchase price, opening stock, low-stock alert, batch/expiry/serial | Missing required fields → inline error |
| 3 | Tap the HSN search icon ("Find HSN") | Search HSN/SAC dialog with live keyword results | Built-in `HsnLookup.kt` data | Tap a row to select, "Use code · description" applies the HSN | No match → manual entry |
| 4 | Save product | `productDao.insertProduct(product)` | Form data | Product in catalog, available for vouchers | Duplicate name → replace (upsert) |
| 5 | Barcode scan | Camera dialog | ML Kit barcode scanning | Barcode value auto-filled | Camera permission denied → manual |
| 6 | Tap low-stock banner (Products) or stock alert (Dashboard) | LowStockScreen "Inventory alerts": count, "Low & Out of Stock" list with Reorder pills | Products with `currentStock <= 0` or alert-triggered | List of items needing attention | None → "Stock looks healthy" empty state |
| 7 | Tap Reorder or Create purchase order | NewVoucher opens with PURCHASE type preselected | `setVoucherPrefillRequest(PURCHASE)` | Purchase voucher ready for supplier + line items | — |

## Flow 6: Financial year management

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Settings → Financial | WizardHeader (back/close) + "Financial Year" title; Active FY card with label and 01 Apr–31 Mar range; rows: Switch financial year, Close current books | `financialYear` + `FinancialYearUtils.startDateFor/endDateFor` | FY overview | Unparseable code → date line hidden |
| 2 | Tap Switch financial year → type start year → Save changes | Inline 4-digit start-year field expands in the row card; bottom bar Cancel / Save changes | Parsed start year → `switchFinancialYear()` | Active FY + `profile.fyLabel` updated, toast | Not 4 digits → validation toast |
| 3 | Tap Close current books | Confirm dialog: freeze source year, carry balances to next | `closeFinancialYear(lockSourceYear = true)` | Balances carried forward, source locked, audit log row written, active year auto-advanced to target, toast | Already locked / error → error toast |
| 4 | Cancel (bottom bar) or header back/close | Returns to Settings menu | — | Unsaved start-year input discarded | — |

## Flow 7: Email invoice to customer

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | From InvoiceScreen, tap "Email" | Email composer | Voucher data, party email | Pre-filled subject, body, PDF attachment | No party email → prompt |
| 2 | Send | JavaMail send via SMTP or OAuth | SMTP config from BusinessProfile or EmailAccount | Email sent, logged to `email_history` | SMTP auth failure → error shown |
| 3 | (Optional) Set automation rule | EmailAutomationSection in Settings | Party info | Rule created: schedule, frequency, template | — |
| 4 | Scheduled send | WorkManager fires | Automation rules | Email sent at configured time | WorkManager constraint not met → retry |

## Flow 8: Data management (backup, restore, export)

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Settings → Data Management | Local database card (records, size, last backup) + rows: Backup now, Restore from backup, Export as CSV / Excel, Import from CSV, Audit log | Loaded record counts, DB file length, `mavo_pref.last_backup_at` | Data Management screen | Never backed up → "Last backup" segment hidden |
| 2 | Backup now | Runs on IO dispatcher | `viewModel.backupDatabase()` → Downloads/Mavo/Backups | Toast with save location, last-backup stamp refreshed | Backup result null → "Backup failed" |
| 3 | Restore from backup | Document picker, then confirm dialog ("overwrites current data") | `copyUriToInternalStorage` + `viewModel.restoreDatabase()` | Toast "restart Mavo to load it" | Pick cancelled → nothing; copy/restore fail → "Restore failed" |
| 4 | Export / Import CSV | Existing CSV export flow / import picker | `CsvTransferManager` | Export toast with path; import summary toast | Failure → error toast |
| 5 | Audit log | List of `YEAR_CLOSE_COMPLETED` events (FY → FY, date) or empty state | `financialYearAuditLogDao.getLogsForYear()` | Historical record of book closes | No events → empty state |

## Flow 9: Voucher list — view, select, export, delete

| Step | Trigger | What shows | Data used | Result | Error state |
|------|---------|------------|-----------|--------|-------------|
| 1 | Tap a voucher row | VoucherDetailScreen: type label header, voucher no + date, party + GSTIN, TOTAL + Posted/Draft pill, line items, Subtotal/Tax/Round Off | Voucher, party, `getItemsForVoucher` | Read-only summary | Voucher deleted meanwhile → "Voucher not found" |
| 2 | Tap Edit / Share / PDF / Print | Edit → NewVoucherScreen in edit mode; Share/PDF/Print → InvoiceScreen actions | voucherId | Edit or invoice view | — |
| 3 | Detail "…" → Delete | VoucherDeleteSheet: counts of vouchers and ledger entries to reverse, total value | `ledgerEntries` for the voucher | Confirm or cancel | — |
| 4 | Confirm delete | `deleteVoucher(id)` runs, returns to list | Voucher + linked ledger rows | Voucher removed, ledger postings reversed, stock restored | — |
| 5 | Long-press a row (or tap while selecting) | Black selection bar: X, "N selected", "…" menu (Select/Deselect all), Export + Delete tiles; row checkboxes appear | `UniversalSelectionController` | Selection mode active | — |
| 6 | Tap Export tile | CSV written of selected vouchers | `ExportStorageManager` | Toast with save location | Storage error → export fails |
| 7 | Tap Delete tile | Same VoucherDeleteSheet with multi-select counts and summed value | Selected voucher ids | Confirm → all deleted, selection cleared, snackbar shows count | — |
| 8 | Tap X (or "…" → Deselect all) | Selection bar and checkboxes disappear | — | Back to normal list | — |
