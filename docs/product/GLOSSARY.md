# GLOSSARY.md — ZeroBook

> Last updated: 2026-09-29

Project-specific accounting, taxation, and application terms used in ZeroBook.

---

## Accounting & Transaction Terms

| Term | Meaning in ZeroBook |
|------|---------------------|
| **Voucher** | A primary document recording a business transaction. ZeroBook supports Sale, Purchase, Sale Return, Purchase Return, Receipt, Payment, Expense, Income, Journal, Debit Note, and Credit Note. |
| **Party** | A business entity transacted with — either a Customer (Debtor) or a Supplier (Creditor). Stored in the `parties` table with running balance tracking. |
| **Ledger Entry** | An individual double-entry debit (`DR`) or credit (`CR`) posting associated with a voucher and account/party, recorded in `ledger_entries`. |
| **Day Book** | Chronological record of all vouchers entered within a selected date or date range. |
| **Cash Book** | Specialized ledger showing all transactions through cash accounts and bank accounts (`bank_cash_transactions`). |
| **Trial Balance** | Summary statement verifying that total debit balances equal total credit balances across all ledger accounts. |
| **Balance Sheet** | Statement summarizing assets, liabilities, and proprietor capital as of a selected date. |
| **Profit & Loss (P&L)** | Statement of business income vs trading expenses/cost of goods sold over a financial period. |
| **Financial Year (FY)** | Accounting year partition (April 1 to March 31 in India). Entries are partitioned and closed with locking rules. |
| **Debit Note (DN)** | Voucher issued to a supplier (or received from customer) reflecting a reduction in payable or goods returned. |
| **Credit Note (CN)** | Voucher issued to a customer (or received from supplier) reflecting a reduction in receivable or allowance granted. |

---

## Indian GST & Compliance Terms

| Term | Meaning in ZeroBook |
|------|---------------------|
| **GSTIN** | 15-character Goods and Services Tax Identification Number of a business or party. |
| **State Code** | First 2 digits of GSTIN identifying the Indian state/UT (e.g., `19` for West Bengal, `27` for Maharashtra). Used to determine intrastate vs interstate tax. |
| **CGST** | Central GST — applied equally with SGST on intrastate transactions (same state). |
| **SGST** | State GST — applied equally with CGST on intrastate transactions (same state). |
| **IGST** | Integrated GST — applied on interstate transactions (different states) instead of CGST + SGST. |
| **HSN Code** | Harmonized System of Nomenclature code for product goods classification under GST. |
| **SAC Code** | Services Accounting Code under GST (TODO: full service catalog expansion). |
| **E-Way Bill** | Electronic waybill for consignment goods transport (TODO: e-way bill generation logic). |

---

## App Architecture & Data Terms

| Term | Meaning in ZeroBook |
|------|---------------------|
| **Room / SQLite** | Local offline-first relational database engine powering ZeroBook (`ZeroBook.db`). Current schema version: 14. |
| **AppRepository** | Central data repository orchestrating DAOs, business rules, double-entry validation, and transactions. |
| **DesignTokens** | Single source of truth for color tokens, spacing, radiuses, elevations, and semantic colors. |

---

## TODO / Planned Terms

- **Composite Scheme:** TODO — simplified GST tax mechanism for small turnover retailers.
- **TDS / TCS:** TODO — Tax Deducted/Collected at Source handling.
- **Barcode SKU Batching:** TODO — Batch/lot serial number inventory conventions.
