# TESTING.md — Mavo

> Last updated: 2026-09-29

## Test setup

| Tool | Purpose |
|------|---------|
| JUnit 4 | Unit test runner |
| Robolectric | Android framework mocking for unit tests |
| Espresso | UI instrumentation tests |
| Roborazzi | Screenshot comparison testing |
| Compose UI Test | Compose-specific test APIs |
| Coroutines Test | `kotlinx-coroutines-test` for suspending function tests |

## How to run

```bash
# Unit tests (fast, runs on JVM via Robolectric)
./gradlew testDebugUnitTest

# Instrumentation tests (requires device/emulator)
./gradlew connectedDebugAndroidTest

# Screenshot tests (Roborazzi)
./gradlew recordRoborazziDebug   # record baseline
./gradlew verifyRoborazziDebug   # compare against baseline
```

## What must pass before finishing a task

1. `./gradlew testDebugUnitTest` passes with zero failures
2. App builds without errors: `./gradlew assembleDebug`
3. No new lint warnings: `./gradlew lintDebug` (target: zero warnings)
4. Manual smoke test on device/emulator:
   - App launches without crash
   - The changed feature works as expected
   - Navigation (forward and back) doesn't break
   - Data persists after app restart

## Test file locations

```
app/src/test/java/          # Unit tests (Robolectric)
app/src/androidTest/java/   # Instrumentation tests (Espresso)
```

## Screenshot testing (Roborazzi)

- Captures Compose screen screenshots and compares pixel-by-pixel
- Baseline images stored in project
- Any visual regression fails the `verifyRoborazzi` task
- Re-record baselines after intentional UI changes

## What to test (guidelines)

| Area | Test type | Notes |
|------|-----------|-------|
| Repository business logic | Unit test | Voucher posting, balance calculations, stock updates |
| DAO queries | Unit test (Robolectric) | Verify Room queries return expected data |
| Migrations | Unit test | Use Room `MigrationTestHelper` |
| ViewModels | Unit test | State flow emissions, error handling |
| Compose screens | Screenshot test | Visual regression via Roborazzi |
| Responsive billing layout | Unit test + screenshot test | Compact/medium stack; expanded split pane |
| Full user flows | Instrumentation test | End-to-end with Espresso + Compose test APIs |

## Critical paths to always test

1. Create voucher → verify ledger entries + stock update + bank/cash transaction
2. FY close → verify balance carry-forward
3. DB migration → verify no data loss
4. App first launch → wizard (onboarding → Terms → permission → 3 setup steps) → Dashboard
5. Returning install → PIN (if enabled) → Dashboard, no wizard replay
