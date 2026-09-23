# Research Status

**Status:** complete for repository-level reverse engineering; not a claim of line-by-line source coverage.

## Verified

- One Android application module with Kotlin and Jetpack Compose.
- Room/SQLite local-first storage, DataStore and SharedPreferences.
- Main navigation, screens, domain entities, document generators, email/reminder services, tests, resources, and build configuration were inspected.
- Recent Git history was inspected through `fc5dae3`, including voucher/document and UI changes.
- Debug unit tests passed with `gradlew.bat testDebugUnitTest --no-daemon --console=plain`.

## Limits

- No production deployment manifest or server-side application was found.
- Exact runtime behavior of every screen branch, Android permission prompt, and device-specific rendering remains **UNVERIFIED**.
- Existing dirty worktree changes were not reverted or treated as historical truth.
- No migration implementation was made.

## Explicit conflicts

- `README.md` describes Retrofit/Moshi/OkHttp networking, but current Gradle/source inspection found no Retrofit or OkHttp dependency/use. Mark networking sync as **CONFLICTING**.
- `DocumentType` enumerates many document types, while `DocumentGeneratorRegistry` registers only six outbound generators. Treat registry membership as implemented capability.
