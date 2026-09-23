# Data Flow

1. User enters profile; setup stores `BusinessProfile` and ensures selected financial year.
2. ViewModels observe repository flows scoped by financial year.
3. Voucher forms create `Voucher` and `VoucherItem` records.
4. Repository updates ledgers, stock, balances, receivables, or bank/cash records according to voucher type.
5. Invoice/document output reads stored profile, voucher, and items, renders HTML, writes PDF, and exposes a shareable URI.
6. Email workflows read stored account/rules/history and use Google/SMTP/WorkManager boundaries.

Posting order and rollback guarantees are **UNKNOWN** without focused transaction tests.
