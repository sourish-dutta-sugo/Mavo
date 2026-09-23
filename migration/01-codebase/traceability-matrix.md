# Traceability Matrix

| Requirement/behavior | Source | Symbol/surface | Dependency | Confidence |
|---|---|---|---|---|
| First-run business setup | `MainActivity.kt:194-369`, `SetupScreen.kt` | startup gate/setup | profile Flow, Room | VERIFIED |
| Bottom navigation | `MainActivity.kt:120-142, 555-710` | `Routes`, `NavHost` | Navigation Compose | VERIFIED |
| FY-scoped data | `AppViewModel.kt`, `AppRepository.kt`, `FinancialYearUtils.kt` | `financialYear` StateFlow | financial-year tables | VERIFIED |
| Voucher lifecycle | `Entities.kt:135-220`, `AppRepository.kt`, `VouchersScreen.kt` | `Voucher`, `VoucherItem` | ledger, party, stock | VERIFIED |
| Six outbound documents | `DocumentGeneratorRegistry.kt`, `DocumentType.kt` | registry map | HTML/PDF renderer | VERIFIED |
| Local PDF sharing | `InvoiceGenerator.kt`, `ExportStorageManager.kt` | WebView/FileProvider | Android storage | VERIFIED |
| Email reminders | `EmailReminderScheduler.kt` | WorkManager scheduling | SMTP, local DB | VERIFIED |
| Theme switching | `AppTheme.kt`, `DesignTokens.kt` | `ThemeViewModel` | SharedPreferences | VERIFIED |
| Remote CRUD API | README only; no current Retrofit source | none found | none found | CONFLICTING |
