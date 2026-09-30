# AGENTS.md — ZeroBook (Android)

> Last updated: 2026-09-29  
> Single source of truth for agent workflow rules, documentation standards, and database safety.

## What this project is

ZeroBook is a native Android retail accounting & GST invoicing app for small Indian retailers. See [docs/product/PRODUCT.md](docs/product/PRODUCT.md) for full context. Complete documentation index is at [docs/README.md](docs/README.md).

## Read these docs first

| Doc | What it covers |
|-----|----------------|
| [docs/agent-rules/DOC_SYNC_MAP.md](docs/agent-rules/DOC_SYNC_MAP.md) | Lookup table: "if you changed X, also update these docs" |
| [docs/agent-rules/CHANGE_REPORT_GUIDE.md](docs/agent-rules/CHANGE_REPORT_GUIDE.md) | Standard report format & template for every task |
| [docs/product/PRODUCT.md](docs/product/PRODUCT.md) | What the app is, who uses it, goals, non-goals |
| [docs/product/FEATURES.md](docs/product/FEATURES.md) | Every feature with status (done / partial / planned) |
| [docs/product/ROADMAP.md](docs/product/ROADMAP.md) | Current work, next up, known bugs, completed items |
| [docs/product/GLOSSARY.md](docs/product/GLOSSARY.md) | Project-specific accounting and GST terminology |
| [docs/design/SCREENS.md](docs/design/SCREENS.md) | Every UI screen, purpose, entry/exit, navigation map |
| [docs/design/USER_FLOWS.md](docs/design/USER_FLOWS.md) | Launch-to-end user flows, every tap documented |
| [docs/design/DESIGN_SYSTEM.md](docs/design/DESIGN_SYSTEM.md) | Colors, spacing, radius, shadows, icons, themes |
| [docs/design/TYPOGRAPHY.md](docs/design/TYPOGRAPHY.md) | Material 3 type scale, font weights, numeric formatting |
| [docs/design/COMPONENTS.md](docs/design/COMPONENTS.md) | Shared components, props, states, where used |
| [docs/engineering/ARCHITECTURE.md](docs/engineering/ARCHITECTURE.md) | Folder structure, layers, state management, data flow |
| [docs/engineering/DATABASE.md](docs/engineering/DATABASE.md) | Tables, fields, relations, schema version, migration rules |
| [docs/engineering/API.md](docs/engineering/API.md) | Services, email, export, invoice generation |
| [docs/engineering/TESTING.md](docs/engineering/TESTING.md) | How to test, test commands, pass criteria |
| [docs/engineering/DECISIONS.md](docs/engineering/DECISIONS.md) | Why X was chosen over Y — do NOT undo these |
| [CHANGELOG.md](CHANGELOG.md) | Release history |
| [VERSION](VERSION) | Current version string |

## Definition of done

A task is **not done** until ALL of these are true:

1. Code compiles and runs without crashes
2. Tests pass (`./gradlew testDebugUnitTest`)
3. Read [docs/agent-rules/DOC_SYNC_MAP.md](docs/agent-rules/DOC_SYNC_MAP.md) and update all affected docs in the same commit
4. Write a task change report in `docs/changes/` following [docs/agent-rules/CHANGE_REPORT_GUIDE.md](docs/agent-rules/CHANGE_REPORT_GUIDE.md)
5. `CHANGELOG.md` has a new entry (if user-visible change)
6. `VERSION` file matches `app/build.gradle.kts` → `versionName`
7. Version bumped in `app/build.gradle.kts` → `versionName` (only if instructed to release)
8. `app/src/main/assets/changelog.json` updated to match `CHANGELOG.md` (if releasing)

## Versioning rules

Single source of truth: `app/build.gradle.kts` → `versionName`

Scheme: **MAJOR.FEATURE.MINOR** (three-segment)

| Change | Bump | Example |
|--------|------|---------|
| Big UI redesign / breaking change | MAJOR | `1.x.x` → `2.0.0` |
| New or changed feature | FEATURE | `1.0.0` → `1.1.0` |
| Small improvement, tweak, or bug fix | MINOR | `1.1.0` → `1.1.1` |

Lower segments reset to `0` when a higher one bumps.

**Do NOT bump versions without explicit instruction from the developer.**

### Changelog entry format

```
## [version] — YYYY-MM-DD
### Added
### Changed
### Fixed
### Removed
DB migration: yes / no
```

## Database protection rules — CRITICAL

1. **App version ≠ DB schema version.** They are tracked separately.
   - App version: `versionName` in `app/build.gradle.kts`
   - DB schema version: `version = N` in `AppDatabase.kt` (currently `14`)
2. Every schema change **must** have a migration in `Migrations.kt`. Never edit old data by hand.
3. **Never delete, reset, or drop the database** without explicit developer approval.
4. Changelog must state `DB migration: yes` if a release includes schema changes.
5. Use `ensureColumn` / `ensureTable` patterns already established in `Migrations.kt`.

## What agents must NOT do

- Do NOT write one giant file. Keep each doc focused and under ~200 lines.
- Do NOT duplicate content across docs. One source of truth per topic.
- Do NOT put secrets, API keys, passwords, or signing keys in any doc.
- Do NOT auto-bump versions or schema versions. Wait for developer instruction.
- Do NOT delete test files, migrations, or database backup logic.
- Do NOT mix app version with DB schema version.
