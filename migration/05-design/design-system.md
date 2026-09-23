# Design System Extraction

## VERIFIED DESIGN TOKENS

Source: `ui/theme/Colors.kt`, `AppColors.kt`, `DesignTokens.kt`, `AppTheme.kt`.

- Brand teal: `#1A5C4B`, mid `#22755F`, card `#2D8A70`, light `#E8F5F1`, accent `#3AAA87`.
- Gold: `#C8943A`, light `#F0C060`.
- Surface background `#F8F7F4`; card/input `#FFFFFF`; border/divider `#E0E4EA`.
- Primary text `#1A1A1A`; secondary `#4A4A4A`; tertiary `#888888`.
- Semantic success/credit `#22A06B`; error/debit `#E24B4A`; warning `#D97706`; info `#6366F1`.
- Spacing: 4, 8, 12, 16, 24, 32 dp.
- Radius: 4, 8, 12, 14, 20 dp and full.
- Elevation: 2, 4, 8, 12.
- Type scale: 11–32 sp token values.

## Themes

`SAFFRON`, `SLATE`, `INK`, `LEDGER`, `NIGHT` are named runtime themes. Exact per-theme token maps live in `AppTheme.kt`; do not collapse them into one palette during migration.

## Components and motion

Material 3 controls, shared screen components, skeletons, selection bars, and transition helpers are present. `ScreenTransitions.kt` and `PremiumMotion.kt` implement press/enter/exit effects. Motion values and reduced-motion behavior are **UNKNOWN**.

## INFERRED DESIGN TOKENS

The palette suggests a warm off-white canvas with teal primary actions and semantic red/green/orange states. This is an interpretation, not a new design requirement.
