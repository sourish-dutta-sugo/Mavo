# DOC_SYNC_MAP.md — Documentation Synchronization Matrix

> Last updated: 2026-09-29  
> Single source of truth for keeping documentation in sync with codebase modifications.

Whenever any code, configuration, or asset is modified, consult this lookup table to determine which documentation files **must** be updated before marking the task complete.

---

## Change Mapping Matrix

| If You Change (X) | Trigger Examples | Also Update (Y) |
|---|---|---|
| **Any Change (Always Required)** | Any bug fix, feature, refactor, config tweak, or UI change | 1. `docs/changes/YYYY-MM-DD_vX.X.X_title.md` (write change report)<br>2. `CHANGELOG.md` (add entry if user-visible)<br>3. `VERSION` (keep aligned with `versionName`) |
| **UI / Screen Change** | Added or updated composable screen in `ui/screens/`, modified screen layout, routes, top bar, or action buttons | 1. `docs/design/SCREENS.md`<br>2. `docs/design/USER_FLOWS.md` (if screen steps changed)<br>3. `docs/engineering/ARCHITECTURE.md` (if routes/screen count changed) |
| **New Feature** | Added new business feature, new voucher category, new report, or new background worker | 1. `docs/product/FEATURES.md`<br>2. `docs/product/PRODUCT.md` (if core scope/goal affected)<br>3. `docs/product/ROADMAP.md` (move from Next to Completed)<br>4. `docs/design/SCREENS.md` / `docs/design/USER_FLOWS.md`<br>5. `docs/engineering/TESTING.md` (add test instructions) |
| **User Flow Change** | Modified onboarding, checkout, voucher entry steps, navigation backstack, or dialog confirmations | 1. `docs/design/USER_FLOWS.md`<br>2. `docs/design/SCREENS.md` |
| **Shared Component Change** | Modified or created composable in `ui/components/` (e.g. LoadingIndicator, cards, custom inputs) | 1. `docs/design/COMPONENTS.md`<br>2. `docs/design/DESIGN_SYSTEM.md` (if token usage changed) |
| **Colors / Theme Change** | Modified `ui/theme/AppColors.kt`, `DesignTokens.kt`, or `AppTheme.kt` | 1. `docs/design/DESIGN_SYSTEM.md` |
| **Typography / Font Change** | Modified `ui/theme/Type.kt` or text scale constants | 1. `docs/design/TYPOGRAPHY.md`<br>2. `docs/design/DESIGN_SYSTEM.md` |
| **Database Schema Change** | Modified Room `@Entity`, added columns/tables, created migration in `data/Migrations.kt` | 1. `docs/engineering/DATABASE.md`<br>2. `docs/engineering/ARCHITECTURE.md`<br>3. `CHANGELOG.md` (mark `DB migration: yes`) |
| **Services / Internal API Change** | Modified invoice PDF generator, email service, CSV transfer, or CameraX/MLKit helpers | 1. `docs/engineering/API.md`<br>2. `docs/engineering/ARCHITECTURE.md` |
| **Architecture / Tech Stack** | Changed dependency graph, added libraries, modified ViewModel/Repo patterns, updated Gradle plugins | 1. `docs/engineering/ARCHITECTURE.md`<br>2. `docs/engineering/DECISIONS.md` (document rationale)<br>3. `docs/engineering/TESTING.md` (if build/test commands change) |
| **Architectural / Design Decision** | Made an intentional technical trade-off (e.g., library choice, state architecture, pattern) | 1. `docs/engineering/DECISIONS.md` |
| **Bug Fix** | Resolved an issue, crash, edge case, calculation error, or navigation regression | 1. `docs/product/ROADMAP.md` (update Known Bugs / Completed)<br>2. `CHANGELOG.md` (under `### Fixed`)<br>3. `docs/engineering/TESTING.md` (if regression test added) |
| **Domain Terminology Added** | Introduced new Indian GST concept, accounting formula, or abbreviation | 1. `docs/product/GLOSSARY.md` |
| **Doc Added or Reorganized** | Created a new documentation file or moved existing documentation | 1. `docs/README.md`<br>2. `AGENTS.md` (if top-level reading list changed) |

---

## Verification Checklist

Before reporting completion of any task, verify:
- [ ] Have all mapped documentation files in the table above been updated?
- [ ] Is the change report file created in `docs/changes/` following [CHANGE_REPORT_GUIDE.md](CHANGE_REPORT_GUIDE.md)?
- [ ] Are all markdown hyperlinks pointing to valid relative paths?
- [ ] Is `CHANGELOG.md` updated with the appropriate category (`Added`, `Changed`, `Fixed`, `Removed`)?
