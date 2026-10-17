# DESIGN_SYSTEM.md — Mavo

> Last updated: 2026-10-03  
> Source of truth: `ui/theme/` package — `DesignTokens.kt`, `AppTheme.kt`, `AppColors.kt`, `Type.kt`

## Themes

6 named themes, selectable at runtime. Persisted in SharedPreferences (`mavo_pref` → `selected_theme`).

| Name | Key | Accent | Background | Dark? |
|------|-----|--------|------------|-------|
| Zero | `ZERO` | `#006B5F` (deep teal) | `#F7F5EF` (warm off-white) | No |
| Saffron | `SAFFRON` | `#1A5C4B` (teal) | `#F7F5EF` | No |
| Slate | `SLATE` | `#22755F` (deep green) | `#F7F5EF` | No |
| Ledger | `LEDGER` | `#176B4A` (deep green) | `#F7F5EF` | No |
| Ink | `INK` | `#7C3AED` (purple) | `#F7F5EF` | No |
| Night | `NIGHT` | `#72D2B2` (mint) | `#101615` (deep dark) | **Yes** |

Each theme defines Material 3 primary, secondary, tertiary, surface, outline,
and error roles through `MavoTheme`. Light themes share warm surfaces;
selection still controls the user's accent and Night remains dark.

Default theme: **Zero**. Legacy names (`BEACH`, `BLUE`, `GREEN`, `TEAL`,
`PURPLE`, `DARK`) map to existing theme choices.

## Colors — Semantic

Defined in `DesignTokens.kt` → `Semantic` object. Theme-independent.
Material 3 container and foreground roles come from the active color scheme.

| Token | Value | Usage |
|-------|-------|-------|
| `success` | `#22A06B` | Positive actions, credit |
| `error` | `#E24B4A` | Errors, debit |
| `warning` | `#D97706` | Warnings, pending |
| `info` | `#6366F1` | Informational |
| `debit` | `#E24B4A` | DR amounts |
| `credit` | `#22A06B` | CR amounts |
| `chartIncome` | `#22A06B` | Report charts |
| `chartExpense` | `#E24B4A` | Report charts |

## Tonal surfaces

| Role | Light value | Usage |
|---|---|---|
| Background | `#F7F5EF` | App canvas |
| Surface | `#FFFCF7` | Cards and menus |
| Surface variant | `#F0ECE4` | Inputs and grouped content |
| Primary accent | Theme-selected | Primary actions and focus |
| Outline | `#77736B` | Control boundaries |

The Night theme supplies dark equivalents. Use `MaterialTheme.colorScheme`
for new UI instead of fixed surface colors.

## Colors — Brand

| Token | Value |
|-------|-------|
| `tealDark` | `#1A5C4B` |
| `tealMid` | `#22755F` |
| `tealCard` | `#2D8A70` |
| `tealLight` | `#E8F5F1` |
| `tealAccent` | `#3AAA87` |
| `gold` | `#C8943A` |
| `goldLight` | `#F0C060` |

## Colors — Badges

| Badge | Background | Text |
|-------|-----------|------|
| Sale | `#E8F8F0` | `#22A06B` |
| Purchase | `#FEF0F0` | `#E24B4A` |
| Receipt | `#FFF7E6` | `#D97706` |
| Payment | `#EEF2FF` | `#6366F1` |
| Return | `#FEF0F0` | `#E24B4A` |
| Overdue | `#FEF0F0` | `#E24B4A` |
| Partial | `#FFF7E6` | `#D97706` |
| Paid | `#E8F8F0` | `#22A06B` |

## Spacing (dp)

Responsive geometry is defined in `DesignTokens.kt` → `LayoutTokens`.

| Token | Value |
|-------|-------|
| `xs` | 4 |
| `sm` | 8 |
| `md` | 12 |
| `lg` | 16 |
| `xl` | 24 |
| `xxl` | 32 |

Responsive layout tokens:

| Token | Value | Usage |
|---|---:|---|
| `paneGap` | 16 | Expanded product/cart pane gap |
| `compactPaneGap` | 12 | Compact stacked pane gap |

## Adaptive windows

Use Material 3 window width classes, not device labels or scaled phone frames.
Compact windows keep a focused single-column workflow and bottom navigation.
Medium windows use a navigation rail and width-capped content. Expanded windows
use a rail plus list/detail or product/cart panes where supported. Keep pane
scroll state independent and keep checkout actions reachable.

## Corner radius (dp)

Defined in `DesignTokens.kt` → `CornerRadius`.

| Token | Value | Usage |
|-------|-------|-------|
| `xs` | 4 | Small chips |
| `sm` | 8 | Input fields |
| `md` | 12 | Cards |
| `lg` | 14 | Dialogs |
| `xl` | 20 | Bottom sheets |
| `full` | 999 | Circular (FAB, avatar) |

## Elevation (dp)

| Token | Value |
|-------|-------|
| `small` | 2 |
| `medium` | 4 |
| `large` | 8 |
| `xLarge` | 12 |

## Typography

Defined in `Type.kt`. All use `FontFamily.Default` (system sans-serif).

| Style | Size | Weight | Line height | Letter spacing |
|-------|------|--------|-------------|----------------|
| displayLarge | 32sp | Bold | 40sp | -0.5sp |
| displayMedium | 28sp | Bold | 36sp | -0.3sp |
| displaySmall | 24sp | Bold | 32sp | 0sp |
| headlineLarge | 22sp | Bold | 28sp | -0.3sp |
| headlineMedium | 20sp | Bold | 26sp | 0sp |
| headlineSmall | 18sp | SemiBold | 24sp | 0sp |
| titleLarge | 16sp | Bold | 22sp | 0sp |
| titleMedium | 14sp | SemiBold | 20sp | 0sp |
| titleSmall | 12sp | SemiBold | 16sp | 0sp |
| bodyLarge | 16sp | Normal | 24sp | 0.5sp |
| bodyMedium | 14sp | Normal | 20sp | 0.2sp |
| bodySmall | 12sp | Normal | 16sp | 0sp |
| labelLarge | 14sp | SemiBold | 20sp | 0sp |
| labelMedium | 12sp | Medium | 16sp | 0sp |
| labelSmall | 11sp | Medium | 16sp | 0.5sp |

## Icons

Material Icons (core + extended). No custom icon library. Access via `Icons.Default.*`, `Icons.Outlined.*`, `Icons.AutoMirrored.Filled.*`.

## Dark mode handling

- Night theme uses `isDark = true` flag
- `AppColors` object switches colors dynamically via `get()` delegates that read `ThemeRuntime.currentTheme`
- Semantic colors (debit/credit bg, success/error bg) have dark-mode variants with alpha-adjusted deep tones
- `MavoTheme` composable builds either `darkColorScheme()` or `lightColorScheme()` based on `appTheme.isDark`
