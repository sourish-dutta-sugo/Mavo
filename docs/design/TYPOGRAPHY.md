# TYPOGRAPHY.md — Mavo

> Last updated: 2026-11-02  
> Source of truth: `app/src/main/java/com/mavo/app/ui/theme/Type.kt`

## Typography System

Mavo enforces a dedicated, self-contained typography hierarchy independent of device settings:
- **Headers & Headlines (`display*`, `headline*`):** `Playwrite Polska` (`PlaywritePolskaFontFamily`, bundled in `res/font/playwrite_polska.ttf`).
- **Body, Titles, and Labels (`title*`, `body*`, `label*`):** `DynaPuff` (`DynaPuffFontFamily`, bundled in `res/font/dynapuff.ttf`).
- **Numerics & Financial Figures:** `Tomorrow` (`NumericFontFamily`, bundled weights Regular→Black in `res/font/tomorrow_*.ttf`) to sit visually with the other two families while keeping digit shapes clean and legible.
- **Device Override Protection:** `MavoTheme` enforces `LocalDensity` with fixed `fontScale = 1.0f`, guaranteeing external OS display size or font settings do not distort the visual hierarchy or break retail layouts.

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

## Numeric Formatting Rules

- **Financial amounts / Currency figures:** Use consistent decimal alignment (`₹XX,XXX.XX`).
- **HSN & GSTIN numbers:** Display in uppercase with standard letter spacing to prevent misreading characters (`0` vs `O`, `1` vs `I`).
- **Tabular figures:** In reports (Trial Balance, P&L, Day Book), right-align numeric amounts and use `titleMedium` or `bodyMedium`.

---

## Screen-Level Text Rules

Screens set `fontSize` directly instead of theme styles. Those literals must
stay on this ladder — anything off it gets normalized:

| Step | Where it is used |
|------|------------------|
| 10 / 11 sp | Captions, micro-labels, table meta |
| 12 / 13 sp | List rows, helper text, badges |
| 14 / 15 sp | Body, inputs, secondary list text |
| 16 / 17 sp | Emphasised body, row titles |
| 18 / 20 sp | Screen sub-headings |
| 22 / 24 sp | Screen titles, hero labels |
| 28 / 32 sp | Dashboard figures, page headers |

Rules:

- **Weights:** Normal → Bold only. `Black` / `ExtraBold` are flattened to
  `Bold`; hierarchy comes from size and color, not heavier weight.
- **No tracked uppercase labels.** Section labels and field labels are sentence
  case with `letterSpacing = 0`. `.uppercase()` remains only for input
  normalization (PAN, GSTIN, IFSC, HSN).
- **Negative tracking** only on display text (≥ 24 sp), at most -0.5 sp.
- **Figures:** right-aligned / `textAlign.End` in numeric columns.

---

## Related Documentation

- See [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md) for color tokens, spacing, and theme integration.
- See [COMPONENTS.md](COMPONENTS.md) for composables using these text styles.
