# Coverage Matrix

| Area | Evidence inspected | Status |
|---|---|---|
| Build and dependencies | `app/build.gradle.kts`, `gradle/libs.versions.toml`, manifest | VERIFIED |
| Entry and navigation | `MainActivity.kt` | VERIFIED |
| State | `AppViewModel.kt`, `DashboardViewModel.kt`, `AppTheme.kt`, `AppPreferences.kt` | VERIFIED |
| Persistence | `Entities.kt`, `AppDaos.kt`, `AppDatabase.kt`, `Migrations.kt`, `AppRepository.kt` | VERIFIED |
| UI screens | `ui/screens/*.kt`, `feature/billing/BillingScreen.kt` | VERIFIED at inventory level |
| Documents and export | `services/*.kt`, `services/documents/**` | VERIFIED |
| Email/reminders | `EmailAutomationService.kt`, `EmailReminderScheduler.kt` | VERIFIED |
| Tests | `app/src/test/**` | VERIFIED |
| Assets/resources | `app/src/main/res/**`, theme files | VERIFIED |
| Git history | `git log`, recent commit stats | VERIFIED |
| Every code branch | Full line-by-line review of all Kotlin | UNKNOWN |
