# DESIGN_SYSTEM.md — Mavo

> Last updated: 2026-11-04  
> Source of truth: `ui/theme/` package — `DesignTokens.kt`, `AppTheme.kt`, `AppColors.kt`, `Type.kt`

## Mavo Loop color system

The app uses one branded semantic system. `MavoTheme` owns raw anchors and
components consume `MaterialTheme.colorScheme`.

| Anchor | Hex | Role |
|---|---|---|
| Peach Paper | `#F6E0CF` | Canvas and calm large surfaces |
| Aubergine Ink | `#3A263A` | Text, structure, and navigation |
| Mango Punch | `#F2A83B` | Primary action, focus, and selection |

Light roles:

| Role | Value |
|---|---|
| `background` | `#F6E0CF` |
| `surface` | `#F9E9DC` |
| `surfaceVariant` | `#F4D9C6` |
| `onSurface` | `#3A263A` |
| `onSurfaceVariant` | `#684E62` |
| `primary` | `#F2A83B` |
| `onPrimary` | `#3A263A` |
| `outline` | `#8D7181` |

Dark roles use `#211721`, `#2A1D2A`, and `#302130` for canvas, surface, and
surface variant. Text uses Peach Paper and its lighter tones. Mango always uses
Aubergine text.

`MavoTheme` supports system dark mode and an increased-contrast override.
Increased contrast replaces low-emphasis outlines with stronger semantic
outlines. Dynamic color is not enabled by default; branded mode remains the
identity baseline.

Status colors stay separate from brand colors:

| Role | Foreground | Container |
|---|---|---|
| Success / credit | `#286647` | `#D8EBDD` |
| Warning / pending | `#7A4E00` | `#F8E4B7` |
| Error / debit | `#8E3B3B` | `#F4D8D4` |
| Info | `#4E5878` | `#E1E5F3` |
| Disabled | `#8D7D86` | `#E8DCD8` |

Pair every status color with text, an icon, or a label. Do not use status
colors as decoration or brand accents.

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
| Payment | `#F0ECE4` | `#4D4A43` |
| Return | `#FEF0F0` | `#E24B4A` |
| Overdue | `#FEF0F0` | `#E24B4A` |
| Partial | `#FFF7E6` | `#D97706` |
| Paid | `#E8F8F0` | `#22A06B` |
| Unpaid | `#F0ECE4` | `#4D4A43` |

Badge colors are read through `AppColors.badge*` so every screen reports the
same value for the same status. Voucher type chips use
`voucherBadgeColor(type)` in `VouchersScreen.kt` as their single source.

## Spacing (dp)

All padding, margin, gap and fixed-height values sit on a **4 dp grid**
(4 / 8 / 12 / 16 / 20 / 24 / 32 …). Exceptions: 1 dp hairline borders, 2 dp
icon nudges, and `999` for pills.

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

Applied as literal `RoundedCornerShape(...)` values across screens. Keep to
this scale; nested radii add up (inner = outer − padding, never below 4).

| Value | Usage |
|-------|-------|
| 4 | Small chips, tiny tags |
| 8 | Inputs, badges, compact controls |
| 12 | Rows, inner cards, list items |
| 16 | Cards, sheets, grouped panels |
| 24 / 28 / 32 | Hero blocks, wizard headers, M3 dialogs |
| 999 | Pills (segmented controls, progress tracker, avatars) |

## Surface and elevation

The shared canvas is the default surface. Do not wrap ordinary headings, lists,
metrics, empty states, or navigation in cards. `ZbCard` is flat by default:
content sits directly on the canvas with no fill, border, or elevation.

Keep filled or outlined surfaces only when they provide a real boundary:
dialogs, bottom sheets, text inputs, selected controls, status badges, and
primary actions. Use an outline only for focus, selection, validation, or
another clear interaction state.

Shadows stay neutral and soft. Do not add colored shadows, glows, or ambient
tints.

| Token | Value |
|-------|-------|
| `small` | 2 |
| `medium` | 4 |
| `large` | 8 |
| `xLarge` | 12 |

## Typography

Defined in `Type.kt`. Display and headings use `DynaPuff` (`res/font/dynapuff.ttf`). All body text, buttons, labels, and numerics use `Tomorrow` (`res/font/tomorrow_*.ttf`). `Playwrite PL` is removed. Font scaling remains accessible and unlocked.

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

## Light theme handling

- Theme selection exposes Zero, Saffron, Slate, and Ledger.
- Saved Night / Ink / legacy Purple selections load as Zero.
- Semantic credit/debit colors remain distinct from the graph-paper canvas.

## Mavo · Redesign (Penpot design artifacts)

The redesign is specified in Penpot only (pages **Mavo · Redesign** and **Mavo · System**); no Android code or Kotlin theme files were changed.

### Palette

| Role | Value | Usage |
|------|-------|-------|
| ink | `#171A16` | Body text, headers |
| ink2 | `#3D423A` | Secondary text |
| muted | `#767B70` | Captions, hints |
| paper | `#F4EFE3` | Board background |
| paper2 | `#EAE1CE` | Inactive segments |
| card | `#FFFDF8` | Cards, sheets |
| field | `#FBF7EC` | Input fields |
| forest | `#0B5D4B` | Primary brand |
| forest2 | `#06382E` | Dark sheets |
| mint | `#C8F2E3` | Primary brand light |
| mint2 | `#E9F9F3` | Chip fills |
| amber | `#E7A63A` | Warning accent |
| amber2 | `#FBEBCB` | Warning chip fill |
| coral | `#D95C4A` | Error / danger accent |
| coral2 | `#F8DCD6` | Error chip fill |
| blue | `#4E7BE8` | Info accent |
| blue2 | `#DDE6FB` | Info chip fill |
| line | `#E1D8C4` | Strokes, dividers |
| white | `#FFFFFF` | Inverse text on brand |

### Type roles

| Role | Font | Usage |
|------|------|-------|
| Display / body | DynaPuff | Titles, headlines, section labels |
| Numerals / labels | Tomorrow | Currency, dates, field labels, captions |
| Editorial accents | DynaPuff | Short decorative headers |
| Icons | Custom 77 SVG set | Dock, header actions, list row avatars (replaces Material Symbols glyphs, which rendered poorly in Penpot export) |

### Motion rules

- Press scale `0.97` at `120 ms` ease-out
- Page / sheet transitions `220–280 ms`
- Animate `transform` / `opacity` only

## Dashboard bento metrics

The Dashboard KPI area uses a responsive bento grid by default. Metrics stay
visible together instead of requiring a horizontal carousel: two columns on
compact windows and three columns when navigation rail layouts are active.

- Cards use `cardBg`, `border`, and the metric's semantic highlight color.
- Card minimum height is 124 dp with 16 dp internal padding and 12 dp grid gap.
- Metric titles use `labelMedium`; amounts use `titleLarge` with
  `NumericFontFamily` for stable financial alignment.
- Every card is tappable and opens the existing metric analytics sheet.
- Long metric titles may wrap to two lines. Amounts remain on one line and
  never use ellipsis.
- Respect reduced motion

### Layout rules

- Boards `390 × 844`, 6 per row, `40 px` gaps; rows y = `0, 900, 1800, 2700, 3600, 4500`
- `20 px` page padding, `26 px` dock radius
- All screens share status bar, header, content area, floating dock, home indicator

## UX laws

Interaction, accessibility, financial-action review, and required screen states
are defined in [UX_LAWS.md](UX_LAWS.md). New screens and shared components must
follow that checklist.
