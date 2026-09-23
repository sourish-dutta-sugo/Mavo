# UI State Inventory

| State family | Observed behavior | Evidence |
|---|---|---|
| Startup | loading, DB error, splash, setup, PIN, main graph | `MainActivity.kt`, `AppViewModel.kt` |
| Data | Flow-backed lists scoped to FY | `AppViewModel.kt`, `AppRepository.kt` |
| Forms | local mutable state plus save callbacks | screen files |
| Selection | single/multi selection bars and universal controller | `ui/selection/` |
| Document | draft vs posted; generator available/unavailable | `DocumentType.kt`, registry |
| Loading | skeleton/loading indicator components | `Skeleton.kt`, `LoadingIndicator.kt` |
| Error | DB error state and inline screen errors | startup/screens |

Not every screen's copy, retry policy, and accessibility announcement was verified.
