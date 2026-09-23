# Codebase Research

## Identity

**VERIFIED:** ZeroBook is a native Android accounting and GST invoicing application for Indian small retailers. The application ID and namespace are `com.zerobook.app` ([app/build.gradle.kts](../../app/build.gradle.kts), lines 13-24). The launcher activity is `MainActivity` ([AndroidManifest.xml](../../app/src/main/AndroidManifest.xml), lines 22-34).

## Technology

| Layer | Observed technology |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose, Material 3 |
| State | ViewModel, StateFlow, Kotlin Coroutines/Flow |
| Storage | Room 2.7.0 over SQLite; DataStore preferences |
| Build | Gradle Kotlin DSL, AGP 9.1.1, KSP |
| Device features | CameraX, ML Kit barcode/text, location dependency |
| Background | WorkManager |
| Email | Android JavaMail, Google Play Services Auth |
| Testing | JUnit, Robolectric, Compose tests, Espresso, Roborazzi |
| Minimum/target/compile | API 24 / 35 / 36 |

`README.md` lists Retrofit/Moshi/OkHttp, but those are not present in current dependency declarations. This is **CONFLICTING** and must not become a migration requirement without runtime evidence.

Two Java support classes are also present for WebView PDF printing and Android print callback bridging.

## Structure

- `app/src/main/java/com/zerobook/app/MainActivity.kt`: composition root and navigation graph.
- `data/`: Room entities, DAOs, database migrations, repository, preferences, financial-year logic, reminders.
- `domain/model/`: domain-facing model types.
- `ui/`: ViewModels, screens, reusable Compose components, theme, selection, transitions.
- `services/`: invoice/PDF, CSV, export, email, document generator services.
- `feature/billing/`: counter-sale billing surface.
- `utils/`: file picker and Android file integration.
- `res/`: manifest resources, themes, colors, strings, provider paths, launcher/logo assets.

## Dependency flow

`MainActivity` owns `AppViewModel`, `DashboardViewModel`, and `ThemeViewModel`. ViewModels construct or use `AppRepository`; repository wraps Room DAOs; Room persists to `ZeroBook.db`. Screen composables collect state and call ViewModel operations. Services handle PDF/CSV/email/file boundaries.

## Build and release

Release build enables shrinking/minification and optional environment-provided signing (`KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`). `.env` and `.env.example` are wired through the Secrets Gradle plugin. ABI splits target `armeabi-v7a` and `arm64-v8a`.

## Tests

Current unit tests cover selection behavior, email automation, and voucher filtering:

- `app/src/test/java/com/zerobook/app/ui/selection/UniversalSelectionControllerTest.kt`
- `app/src/test/java/com/zerobook/app/services/EmailAutomationServiceTest.kt`
- `app/src/test/java/com/zerobook/app/ui/screens/VouchersScreenFilterTest.kt`

Broader UI, database migration, document-rendering, and end-to-end coverage is **UNKNOWN** from the current test tree.

`gradlew.bat testDebugUnitTest --no-daemon --console=plain` passed during research.
