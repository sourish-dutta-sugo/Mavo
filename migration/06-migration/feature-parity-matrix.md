# Feature Parity Matrix

| Capability | Android evidence | Migration priority | Platform note |
|---|---|---:|---|
| Setup/profile | `SetupScreen`, `BusinessProfile` | P0 | shared domain + forms |
| Dashboard | `DashboardScreen` | P0 | responsive layout |
| Vouchers/GST | `VouchersScreen`, entities/repository | P0 | shared calculations |
| PDF documents | `InvoiceGenerator`, registry | P0 | platform renderer |
| Parties/products/stock | screens/entities | P0 | shared persistence contract |
| Ledger/bank/cash | screens/entities | P0 | shared domain |
| Reports | report screens | P1 | responsive charts/tables |
| Email/reminders | services/scheduler | P1 | platform/service adapter |
| Barcode/OCR | scanner dialog/dependencies | P1 | capability varies |
| Five themes | theme package | P1 | token portability |
| PIN lock | MainActivity/settings | P1 | platform security storage |
