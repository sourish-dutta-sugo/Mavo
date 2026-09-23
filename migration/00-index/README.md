# ZeroBook Migration Research

This package reverse-engineers the current native Android application for later feature-parity work in React, React Native, Flutter, or Dart. It documents the repository as observed on 2026-09-23.

## Evidence labels

- **VERIFIED**: directly observed in current source, resources, tests, build files, or Git history.
- **INFERRED**: reasonable interpretation of verified evidence.
- **UNKNOWN**: not established by repository inspection.
- **CONFLICTING**: sources disagree; both claims remain recorded.

## Scope

In scope: the single `app` Android module, Kotlin/Compose UI, Room persistence, document generation, email/reminder integrations, resources, tests, and relevant Git history. The repository is not evidence of a production backend or a completed multiplatform implementation.

## Package map

| Area | Document |
|---|---|
| Status and limits | [research-status.md](research-status.md) |
| Coverage | [coverage-matrix.md](coverage-matrix.md) |
| Codebase | [../01-codebase/codebase-research.md](../01-codebase/codebase-research.md) |
| Product | [../02-product/product-research.md](../02-product/product-research.md) |
| Architecture | [../03-architecture/architecture-research.md](../03-architecture/architecture-research.md) |
| UI/UX | [../04-ui-ux/ui-ux-research.md](../04-ui-ux/ui-ux-research.md) |
| Design | [../05-design/design-system.md](../05-design/design-system.md) |
| Migration | [../06-migration/migration-research.md](../06-migration/migration-research.md) |
| Master specification | [../07-final/MASTER-MIGRATION-SPEC.md](../07-final/MASTER-MIGRATION-SPEC.md) |

## Current source of truth

Repository: `sourish-dutta-sugo/ZeroBook`  
Observed head: `fc5dae3` (`2026-09-03`)  
Current app version: `2.2.1`, version code `4`.
