# CHANGE_REPORT_GUIDE.md — ZeroBook Change Reports

> Last updated: 2026-09-29  
> Single source of truth for post-task change reporting.

---

## Purpose & Requirement

After **EVERY** task completed on ZeroBook (including features, UI changes, bug fixes, refactoring, documentation reorganization, or small tweaks), the agent **must** create a new change report file in `docs/changes/`.

### Naming Convention

Reports must be named with the format:
```
YYYY-MM-DD_vX.X.X_short-title.md
```
*(e.g., `2026-09-29_v2.2.1_docs-reorganization.md`)*

- `YYYY-MM-DD`: Current local date
- `vX.X.X`: Current application version from `VERSION` / `app/build.gradle.kts`
- `short-title`: Kebab-case descriptive slug (2–4 words)

---

## Required Report Sections

Every report must include all of the following sections:
1. **Summary:** 2–3 concise lines summarizing the change.
2. **Type:** Exactly one of `Feature`, `UI`, `Fix`, `Refactor`, or `Other`.
3. **Files changed:** List of exact file paths and a brief note on what changed in each.
4. **Behavior before vs after:** Concrete description of what changed for the user/system.
5. **Effects / risks:** Potential impact on screens, flows, data, or other features.
6. **DB migration:** `yes` / `no` (and target schema version if `yes`).
7. **Version bump:** `old -> new` and reason, or `None` (per rule: only bump when instructed).
8. **Docs updated:** List of documentation files updated to maintain synchronization.
9. **How to test:** Step-by-step verification instructions.

---

## Copy-Paste Report Template

Use the following template when creating reports in `docs/changes/`:

```markdown
# Change Report: <Short Title>

- **Date:** YYYY-MM-DD
- **Version:** vX.X.X
- **Type:** Feature | UI | Fix | Refactor | Other
- **DB Migration:** no (or: yes — version N)
- **Version Bump:** none (or: old -> new — reason)

---

## Summary
<!-- 2-3 lines summarizing the task, why it was done, and the outcome -->

---

## Files Changed
| File Path | Description of Changes |
|-----------|------------------------|
| `path/to/file1.kt` | Added XYZ method to handle ABC |
| `path/to/file2.kt` | Updated UI layout for DEF |

---

## Behavior: Before vs After
- **Before:** 
- **After:** 

---

## Effects & Risks
- **Screens affected:** 
- **Flows affected:** 
- **Data integrity / migrations:** 
- **Risk evaluation:** Low | Medium | High — rationale: 

---

## Docs Updated
- [ ] `docs/product/...`
- [ ] `docs/design/...`
- [ ] `docs/engineering/...`
- [ ] `docs/agent-rules/...`
- [ ] `CHANGELOG.md`
- [ ] `VERSION`

---

## How to Test
1. Step 1...
2. Step 2...
3. Expected result:
```
