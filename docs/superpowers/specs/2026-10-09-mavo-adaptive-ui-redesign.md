# Mavo Adaptive UI Redesign

- **Status:** Review requested
- **Scope:** App-wide native Android phone and tablet redesign
- **Platform:** Kotlin, Jetpack Compose, Material 3
- **Intent:** Make Mavo feel like one fast retail workspace across phone and
  tablet. Keep every existing accounting, GST, inventory, and data workflow.

## Product goals

- Make frequent work fast: sale, purchase, receipt, and expense entry.
- Keep broad accounting, inventory, and reports available without crowding
  primary navigation.
- Give phones and tablets layouts designed for their available space.
- Make local data appear quickly without showing false empty states.
- Preserve offline-first behavior, accounting calculations, and accessibility.

## Existing implementation

- `MainActivity.kt` owns the `NavHost` and four top-level destinations.
- `AppViewModel` exposes Room-backed `StateFlow` data, currently shared while
  subscribed and retained for five seconds after collection stops.
- Billing already uses Material 3 window size classes and a responsive split
  pane. Dashboard still detects tablets from raw width.
- Material 3, Material icons, Compose animation, and window-size classes are
  already dependencies. No UI dependency is needed.
- Existing app UI has many Zero-style composables, skeletons, and motion
  helpers. Use these only where they fit the new visual system.
- The supplied Lovable project has 89 Android artboards. Its source scales
  phone compositions onto tablet canvases. That scaling is not the target.

## Approaches considered

1. Restyle current Zero screens. Lowest risk, but does not meet the requested
   visual reset.
2. **Recommended:** replace shared visual language and adaptive shell while
   preserving features, accounting logic, and route identities.
3. Replace navigation, data, and screen architecture. Highest regression risk
   and unnecessary for the visual and adaptive goals.

This design selects approach 2. It supersedes the approved
`2026-10-08-android-ui-port-design.md` after this spec receives approval.

## Visual system

- Replace the monochrome Zero-first look with warm tonal Material 3 surfaces,
  a deep teal brand accent, and stronger separation between primary and
  secondary actions.
- Keep debit, credit, warning, and error colors semantically distinct.
- Apply Material 3 color roles to all existing light and dark themes. Preserve
  the user's selected theme and existing theme preference.
- Use Material 3 components and installed Material icons. Use Compose Animation
  APIs and existing motion helpers; add no animation dependency.
- Use restrained, purposeful transitions and keep reduced-motion / system
  animator-scale behavior.
- Keep typography readable for currency, GST identifiers, large font settings,
  and long Indian business names.

## Adaptive app shell

Use `WindowSizeClass` rather than raw screen-width checks.

| Window size | Navigation | Content |
|---|---|---|
| Compact | Material 3 bottom navigation; New Sale action on top-level screens | Single-column screens and compact forms |
| Medium | Material 3 navigation rail | Wider workspaces; two panes where content fits |
| Expanded | Material 3 navigation rail | List/detail panes and billing product/cart split |

Keep four primary destinations: Home, Vouchers, Parties, and More. More is the
existing Settings destination presented as a hub for Products, Reports,
Ledger, Bank & Cash, Expenses, and Settings. Keep route identities stable.
Hide the shell's New Sale action during onboarding and full-screen entry flows.

Home leads with four frequent actions (Sale, Purchase, Receipt, Expense),
business totals, actionable stock alerts, and recent transactions. Do not add
new accounting features or hide existing ones.

Use shared screen content with adaptive arrangement, not duplicate phone and
tablet implementations. On tablets, prevent phone-width scaling and excessive
empty space. Preserve independent scrolling for parallel panes.

## Cache and UI state

- Keep Room as the only persistent source of truth. Add an app-owned,
  in-memory cache for Room-backed read models; add no database or network
  cache.
- Key financial-year data by both dataset and active financial year. Keep
  profile and other global data outside FY-specific keys.
- Reuse cached snapshots immediately when navigating between screens. Replace
  each snapshot whenever its Room flow emits. Do not use TTL-based freshness.
- Room writes remain authoritative. A committed change must flow back through
  Room before replacing the cached read model.
- Cache lifetime is the app process. After process restart, load from Room.
- Represent initial loading, confirmed empty data, loaded content, and errors
  explicitly. Show skeletons only before the first cached or Room value.
- If Room observation fails after content loaded, keep cached content visible,
  show the error, and offer retry. If no cached content exists, show an error
  state with retry. Never convert read failures into empty lists.
- Keep form and voucher draft state on read/write errors. Keep save actions
  disabled or pending only while the operation is active; surface failures.

## Interaction and accessibility

- Keep primary daily actions reachable with one tap from Home.
- Preserve validation, keyboard/focus behavior, back navigation, export and
  share actions, permissions, and all business flows.
- Keep icon labels, semantic state, contrast, and touch targets of at least
  48 dp.
- Support font scaling without clipping totals, actions, or tablet panes.
- Animate meaningful state changes only. Respect system motion settings.

## Delivery slices

1. Build shared Material 3 tokens, adaptive shell, memory cache/load-state
   foundation, and redesigned Home.
2. Redesign daily workspaces: billing, vouchers, parties, products, and their
   create/detail flows.
3. Redesign remaining reports, ledger, bank/cash, expenses, settings,
   onboarding, and secondary surfaces.

All existing screens and functions remain in scope. Slices sequence delivery;
they do not narrow the final redesign.

## Verification

- Unit-test cache replay, Room-emission replacement, FY isolation, and cache
  behavior after errors and retry.
- Test explicit loading, empty, content, saving, and error states.
- Review Compose previews or screenshots in compact, medium, and expanded
  windows; include phone portrait, tablet portrait, and tablet landscape.
- Verify light/dark themes, large font scale, semantic labels, and reduced
  motion.
- Preserve existing navigation and accounting smoke tests.
- Run `testDebugUnitTest`, `assembleDebug`, and `lintDebug`.

## Acceptance criteria

1. All current routes, screens, business actions, and accounting behavior stay
   available.
2. Phone navigation uses a bottom bar; medium and expanded windows use a rail.
3. Tablet layouts are adaptive, not scaled phone artboards.
4. Frequent actions are reachable from Home with one tap.
5. Cached data appears immediately on return to a screen and updates on every
   corresponding Room emission.
6. Switching FY never displays another FY's cached records.
7. Loading, empty, and failure states are distinct; errors never appear as
   successful empty data.
8. UI remains usable with dark theme, large text, and reduced motion.
9. No DB schema migration or new UI dependency is introduced.

## Documentation impact

Update `docs/design/SCREENS.md`, `USER_FLOWS.md`, `DESIGN_SYSTEM.md`, and
`COMPONENTS.md`; update `docs/engineering/ARCHITECTURE.md` and `DECISIONS.md`;
write the required task report and user-facing changelog entry during
implementation. Do not bump app or database versions without instruction.
