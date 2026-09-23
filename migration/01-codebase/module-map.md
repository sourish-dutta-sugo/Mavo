# Module Map

| Module | Responsibility | Evidence |
|---|---|---|
| UI composition | Startup gating, navigation, bottom navigation | `MainActivity.kt:161-710` |
| Application state | Database init, flows, CRUD orchestration, voucher prefill | `AppViewModel.kt` |
| Persistence | Entities, DAOs, Room migrations, repository | `data/*.kt` |
| Accounting domain | Vouchers, ledgers, parties, products, financial years | `Entities.kt`, `AppRepository.kt` |
| Document output | HTML/PDF invoice and outbound templates | `services/InvoiceGenerator.kt`, `services/documents/**` |
| Email automation | OAuth/SMTP, history, reminders, WorkManager | `EmailAutomationService.kt`, `EmailReminderScheduler.kt` |
| Theme system | Five named themes and runtime switching | `AppTheme.kt`, `DesignTokens.kt` |
| Billing | Simplified counter-sale workflow | `feature/billing/BillingScreen.kt` |
