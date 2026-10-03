# TYPOGRAPHY.md — Mavo

> Last updated: 2026-10-03  
> Source of truth: `app/src/main/java/com/mavo/app/ui/theme/Type.kt`

## Typography System

Mavo uses Jetpack Compose Material 3 typography with system default font family (`FontFamily.Default`), optimized for legibility on diverse Android retail screens.

---

## Type Scale Specification

| Style | Font Size | Line Height | Weight | Letter Spacing | Usage Rules |
|-------|-----------|-------------|--------|----------------|-------------|
| `displayLarge` | 32 sp | 40 sp | Bold | -0.5 sp | Splash / Brand prominent headers |
| `displayMedium` | 28 sp | 36 sp | Bold | -0.3 sp | Key total amounts in hero summary cards |
| `displaySmall` | 24 sp | 32 sp | Bold | 0 sp | Secondary hero metrics, major totals |
| `headlineLarge` | 22 sp | 28 sp | Bold | -0.3 sp | Top bar titles on primary screens |
| `headlineMedium` | 20 sp | 26 sp | Bold | 0 sp | Section headers, modal sheet titles |
| `headlineSmall` | 18 sp | 24 sp | SemiBold | 0 sp | Card headers, dialog titles |
| `titleLarge` | 16 sp | 22 sp | Bold | 0 sp | Party names, voucher item titles |
| `titleMedium` | 14 sp | 20 sp | SemiBold | 0 sp | Subtitle in list rows, form group titles |
| `titleSmall` | 12 sp | 16 sp | SemiBold | 0 sp | Badges, small card subtitles |
| `bodyLarge` | 16 sp | 24 sp | Normal | 0.5 sp | Primary reading copy, narrative explanations |
| `bodyMedium` | 14 sp | 20 sp | Normal | 0.2 sp | Default input text, table row contents |
| `bodySmall` | 12 sp | 16 sp | Normal | 0 sp | Secondary descriptions, timestamps, help text |
| `labelLarge` | 14 sp | 20 sp | SemiBold | 0 sp | Primary action buttons, CTA buttons |
| `labelMedium` | 12 sp | 16 sp | Medium | 0 sp | Filter chips, table column headers |
| `labelSmall` | 11 sp | 16 sp | Medium | 0.5 sp | Micro-labels, tax breakdown sub-rows |

---

## Numeric Formatting & Monospace Rules

- **Financial amounts / Currency figures:** Use consistent decimal alignment (`₹XX,XXX.XX`).
- **HSN & GSTIN numbers:** Display in uppercase with standard letter spacing to prevent misreading characters (`0` vs `O`, `1` vs `I`).
- **Tabular figures:** In reports (Trial Balance, P&L, Day Book), right-align numeric amounts and use `titleMedium` or `bodyMedium`.

---

## Related Documentation

- See [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md) for color tokens, spacing, and theme integration.
- See [COMPONENTS.md](COMPONENTS.md) for composables using these text styles.
