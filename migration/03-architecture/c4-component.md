# Component View

## Application state

- `AppViewModel`: database initialization, profile/setup status, financial-year selection, observable collections, CRUD orchestration, voucher prefill.
- `DashboardViewModel`: dashboard profile header and KPI animation preference.
- `ThemeViewModel`: runtime theme selection and persistence.
- Screen composables: form/list/detail presentation and local transient state.

## Data

- Entity records define business profile, parties, products, vouchers/items, ledger, bank/cash, receivables, financial years, expenses/income, email.
- DAOs provide query/insert/update/delete boundaries.
- `AppRepository` combines flows and applies financial-year overlays, derived stock/rates, posting and recalculation.

## Output/integration

- `InvoiceGenerator` and `DocumentGenerator` produce HTML/PDF.
- `DocumentGeneratorRegistry` selects registered generators.
- `CsvTransferManager` and `ExportStorageManager` move data/files.
- `EmailAutomationService` handles account/auth/send/history.
- `EmailReminderScheduler` schedules background work.
