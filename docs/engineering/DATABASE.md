# DATABASE.md — Mavo

> Last updated: 2026-09-29  
> DB file: `Mavo.db` (SQLite via Room)  
> Current schema version: **15** (in `AppDatabase.kt`)

## Version tracking

| Concept | Where | Current |
|---------|-------|---------|
| App version | `app/build.gradle.kts` → `versionName` | `2.2.1` |
| DB schema version | `AppDatabase.kt` → `@Database(version = N)` | `15` |

**These are independent.** An app version bump does NOT require a schema bump, and vice versa.

## Tables (20 entities)

### `financial_years`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| code | TEXT | ✅ | e.g., "2025-2026" |
| startDate | INTEGER | | epoch millis |
| endDate | INTEGER | | epoch millis |
| isClosed | INTEGER | | boolean |
| isLocked | INTEGER | | boolean |
| sourceFinancialYearCode | TEXT | | nullable, which FY this was created from |
| createdAt | INTEGER | | |
| closedAt | INTEGER | | nullable |
| lockedAt | INTEGER | | nullable |

### `business_profile`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| id | INTEGER | ✅ | Always 1 (singleton) |
| businessName, ownerName, address, city, state, pin, phone, alt_phone, email | TEXT | | alt_phone added in v15 |
| business_type, selling_type | TEXT | | v15: Manufacturer/Wholesaler/Retailer, Products/Services |
| gstin, pan, stateCode | TEXT | | |
| bankName, accountNo, ifsc, bank_branch, upiId, upiPhone | TEXT | | |
| logoPath, signaturePath | TEXT | | nullable, file paths |
| invoiceTitleDefault | TEXT | | Default: "TAX INVOICE" |
| invoiceFooterText | TEXT | | |
| show* flags | INTEGER | | 8 boolean toggle columns |
| fyLabel | TEXT | | Current FY code |
| termsAndConditions | TEXT | | |
| smtp_email, smtp_password, smtp_host, smtp_port | TEXT | | Email config |
| createdAt | INTEGER | | |

### `parties`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| id | TEXT | ✅ | UUID |
| name, type, phone, email, address, city, state, stateCode, pin | TEXT | | type: CUSTOMER/SUPPLIER/BOTH |
| gstin, pan | TEXT | | nullable |
| openingBalance | REAL | | |
| balanceType | TEXT | | DR or CR |
| credit_limit | REAL | | |
| credit_days | INTEGER | | |
| notes | TEXT | | |
| total_purchases_amount | REAL | | |
| total_transactions | INTEGER | | |
| first_transaction_date, last_transaction_date | TEXT | | |
| loyalty_points | INTEGER | | |
| birthday, anniversary | TEXT | | |
| createdAt | INTEGER | | |

### `products`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| id | TEXT | ✅ | UUID |
| name, hsnCode, unit | TEXT | | unit: PCS/KG/LTR/MTR/BOX/BAG/NOS |
| saleRate, purchaseRate, gstRate, openingStock | REAL | | |
| current_stock | REAL | | |
| enable_stock_alert | INTEGER | | boolean |
| low_stock_threshold | REAL | | default 5.0 |
| stock_unit | TEXT | | |
| barcode_value | TEXT | | |
| secondary_unit, conversion_factor | TEXT/REAL | | |
| batchEnabled, batchNumber, expiryEnabled, expiryDate, serialEnabled | mixed | | |
| createdAt | INTEGER | | |

### `vouchers`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| id | TEXT | ✅ | UUID |
| voucherNo | TEXT | | Auto-generated per type/FY |
| type | TEXT | | 23 types (SALE, PURCHASE, RECEIPT, etc.) |
| date | INTEGER | | epoch millis |
| partyId | TEXT | | nullable FK to parties |
| narration | TEXT | | |
| taxableAmount, cgst, sgst, igst, roundOff, netAmount | REAL | | |
| paymentMode | TEXT | | CASH/BANK/CHEQUE/UPI |
| chequeNo, chequeDate, bankName | TEXT/INTEGER | | nullable |
| isIgst | INTEGER | | boolean |
| documentType | TEXT | | |
| additionalChargesJson | TEXT | | JSON array of AdditionalCharge |
| transport_* columns | TEXT | | transporter, LR, vehicle, GSTIN, destination |
| dispatch*, buyer*, reference* | TEXT | | |
| status | TEXT | | DRAFT/POSTED |
| receiptImagePath, attachmentPath | TEXT | | nullable |
| bank details (IFSC, holder, name, branch) | TEXT | | nullable |
| memoNumber | TEXT | | nullable |
| outstandingAmount | REAL | | |
| financialYearCode | TEXT | | FK to financial_years |
| createdAt | INTEGER | | |

### `voucher_items`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| id | TEXT | ✅ | UUID |
| voucherId | TEXT | | FK to vouchers |
| productId, productName, hsnCode | TEXT | | |
| qty, rate, discount | REAL | | |
| discountType | TEXT | | PERCENT/AMOUNT |
| taxableAmount, gstRate, cgstAmount, sgstAmount, igstAmount, totalAmount | REAL | | |
| financialYearCode | TEXT | | |

### `ledger_entries`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| id | TEXT | ✅ | UUID |
| accountHead | TEXT | | e.g., "Sales Account", "Party: X", "CGST Payable" |
| partyId | TEXT | | nullable FK to parties (when entry is party-related) |
| voucherId | TEXT | | FK to vouchers |
| date | INTEGER | | |
| debit, credit | REAL | | |
| narration | TEXT | | |
| financialYearCode | TEXT | | |
| createdAt | INTEGER | | |

### `bank_cash_transactions`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| id | TEXT | ✅ | UUID |
| type | TEXT | | RECEIPT/PAYMENT |
| mode | TEXT | | CASH/BANK/CHEQUE/UPI/NEFT/RTGS/IMPS |
| amount | REAL | | |
| date | INTEGER | | |
| partyId, partyName | TEXT | | nullable |
| sourceVoucherId | TEXT | | nullable FK to vouchers |
| narration | TEXT | | |
| chequeNo, chequeDate, bankName | TEXT/INTEGER | | |
| receiptImagePath | TEXT | | nullable |
| financialYearCode | TEXT | | |
| createdAt | INTEGER | | |

### `receipt_allocations`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| id | TEXT | ✅ | UUID |
| receiptId | TEXT | | FK to vouchers (receipt/payment) |
| invoiceId | TEXT | | FK to vouchers (sale/purchase) |
| allocatedAmount | REAL | | |
| financialYearCode | TEXT | | |
| createdAt | INTEGER | | |

### `ledger_accounts`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| id | TEXT | ✅ | UUID |
| name, groupName | TEXT | | |
| openingBalance | REAL | | |
| balanceType | TEXT | | DR/CR |
| isSystem | INTEGER | | 0/1 |
| isParty | INTEGER | | 0/1 |
| partyId | TEXT | | nullable FK |
| gstin, phone, email, address | TEXT | | |
| createdAt | INTEGER | | |

### `bills_receivable`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| id | TEXT | ✅ | UUID |
| voucherId, voucherNo | TEXT | | |
| partyId, partyName | TEXT | | |
| billDate, dueDate | INTEGER | | |
| originalAmount, paidAmount, outstandingAmount | REAL | | |
| status | TEXT | | UNPAID/PARTIAL/PAID/OVERDUE |
| daysOverdue | INTEGER | | |
| lastReminderDate | INTEGER | | nullable |
| financialYearCode | TEXT | | |
| createdAt | INTEGER | | |

### FY balance tables (3 tables, same pattern)

- `party_financial_year_balances` (PK: partyId + financialYearCode)
- `product_financial_year_balances` (PK: productId + financialYearCode)
- `ledger_account_financial_year_balances` (PK: accountId + financialYearCode)

Each has: opening balance/stock, balance type, createdAt, updatedAt.

### `financial_year_audit_logs`

| Column | Type | PK |
|--------|------|----|
| id | TEXT | ✅ |
| action, financialYearCode, targetFinancialYearCode, detailsJson | TEXT | |
| createdAt | INTEGER | |

### `expenses` / `incomes`

| Column | Type | PK | Notes |
|--------|------|----|-------|
| id | TEXT | ✅ | UUID |
| date | INTEGER | | |
| category, description | TEXT | | |
| amount | REAL | | |
| paymentMode, referenceNo, attachmentPath, voucherNo | TEXT | | |
| fyLabel | TEXT | | FY code |
| createdAt | INTEGER | | |

### Email tables (3 tables)

- `email_accounts` (accountId PK, gmailAddress, oauthStatus, tokenReference)
- `email_automation_rules` (id PK, customerId, schedule, frequency, template, etc.)
- `email_history` (id PK, recipient, subject, timestamp, status, attachment, details)

## Migration rules

1. All migrations live in `Migrations.kt`
2. Named migrations: `MIGRATION_4_5` through `MIGRATION_13_14`
3. Defensive `ensureColumn` / `ensureTable` functions run on both `onCreate` and `onOpen`
4. Pattern: check if column exists via `PRAGMA table_info`, add only if missing
5. **Never** destructive-migrate. Never `fallbackToDestructiveMigration()`.
6. Every new column must have a `DEFAULT` value
7. Test migrations before release

## Relationships (not enforced by FK constraints — app-level)

```
parties.id ← vouchers.partyId
vouchers.id ← voucher_items.voucherId
vouchers.id ← ledger_entries.voucherId
vouchers.id ← bank_cash_transactions.sourceVoucherId
vouchers.id ← receipt_allocations.receiptId / invoiceId
vouchers.id ← bills_receivable.voucherId
parties.id ← ledger_entries.partyId
parties.id ← ledger_accounts.partyId
financial_years.code ← (all FY-scoped tables).financialYearCode
```
