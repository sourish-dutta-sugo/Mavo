# Product Research

## Purpose

**VERIFIED:** ZeroBook combines GST billing, vouchers, customer/supplier ledgers, inventory, bank/cash tracking, expenses/income, reports, document export, and email reminders in one local-first Android app. Audience and product framing are described in `README.md`, while behavior is implemented in the screens, repository, entities, and services.

## Major capabilities

1. Guided business profile setup.
2. Dashboard with financial-year context and KPIs.
3. Voucher creation/editing/filtering, including sale, purchase, returns, receipts, payments, notes, and newer outbound document types.
4. Counter sale flow.
5. Party/customer/supplier management and detail ledger.
6. Product catalog, HSN lookup, stock and barcode fields.
7. Ledger books, bank/cash transactions, receivables, expenses, income.
8. Reports and stock reports.
9. HTML/PDF invoice/document generation, export, share, and CSV transfer.
10. Email account setup, invoice/report sending, history, and scheduled reminders.

## Roles

- **VERIFIED:** one business operator profile is stored locally.
- **INFERRED:** primary user is owner/operator; no multi-user authorization model appears in current entities or navigation.

## Business rules

- GST fields include CGST, SGST, IGST, tax rates, HSN, GSTIN and interstate flag.
- Financial year code partitions balances and transactional queries.
- Voucher status includes draft/posted; document metadata distinguishes draft and ledger-posting behavior.
- Party balances can be debit or credit; products carry rates, stock, units, thresholds, batch/expiry/serial flags.
- Receipt allocations trigger outstanding recalculation.
- Financial years can close/lock and carry balances forward.
- Unknown document type resolution falls back to tax invoice in `DocumentType.fromVoucherType`; registry then fails if generator is absent.

## Unknowns

Exact accounting formulas and all validation branches require focused per-function review. Treat tax rounding, stock mutation timing, and posting rollback behavior as **UNKNOWN** until tested against repository code and fixtures.
