# Compose Modifier API Design

## Goal

Make every app-owned composable that draws or lays out UI individually tunable
from Kotlin call sites. Developers can adjust position, size, and spacing with
standard Jetpack Compose modifiers without changing the app's default layout.

## Approved scope

- Add `modifier: Modifier = Modifier` to every app-owned UI composable that
  lacks it, including screen roots, reusable components, private screen pieces,
  and billing UI.
- Leave callback-only or slot-only helpers and direct Material primitives
  unchanged.
- Keep tuning in source code. Do not add an in-app editor, persistent layout
  settings, global layout tokens, or a dependency.

## API and behavior

- Apply each composable's incoming modifier to its root UI node. Preserve
  existing root modifiers by chaining the caller modifier with the current
  layout modifiers.
- Keep existing call sites unchanged. The default `Modifier` must preserve
  current rendering and layout.
- Callers use `Modifier.offset(x, y)` for positional shifts,
  `size`/`width`/`height` for dimensions, and `padding` when neighboring layout
  must reflow.
- Do not introduce a custom XY extension. Compose already provides these
  operations. Modifier order is significant and must remain intentional.
- For composables with multiple emitted roots, apply the modifier without
  changing grouping, alignment, or measurement behavior.

`offset` changes placement but not measured size. It can overlap neighboring
content. Dimensions remain subject to parent constraints unless a required-size
modifier is explicitly chosen at the call site.

## Validation

1. Build the debug app to catch signature, import, and modifier-chain errors.
2. Run `./gradlew testDebugUnitTest`.
3. Inspect every app-owned UI composable declaration and confirm the modifier
   reaches its root without changing default layout behavior.

## Documentation

Update `docs/engineering/ARCHITECTURE.md`,
`docs/engineering/DECISIONS.md`, and `docs/design/COMPONENTS.md`. Add the
required task report in `docs/changes/`. Do not update `CHANGELOG.md` because
default user-visible behavior does not change. Do not bump the version.

## References

- [Jetpack Compose modifiers](https://developer.android.com/develop/ui/compose/modifiers)
- Existing modifier pattern: `app/src/main/java/com/mavo/app/ui/screens/SharedComponents.kt`
- Existing composable without a modifier parameter: `app/src/main/java/com/mavo/app/ui/screens/SplashScreen.kt`
