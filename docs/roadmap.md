# Roadmap and non goals

What the library covers is listed in the [README](../README.md#features). This document lists the
ideas that are **not** implemented yet with the reasoning, and the things that are deliberately
left out.

## Implemented from earlier roadmaps

These were roadmap items and are part of the library now, see the README for the usage:

- model binding of check box and radio button items to model-data `IModel` objects
  (`model`/`value` attributes, `MenuBuilder.withModels`, `updateFromModels`)
- `toolTipKey` for translated tool tips
- tool bar options `showText`, `floatable` and `rollover`
- `accessibleName` and `accessibleDescription`
- action id strategies for the export of programmatic menus (`MenuInfoExporter.withActionIds`)

## Known limitations

Design tradeoffs that were deliberately left as is during the 5.1 bug fixing round (two
independent multi agent reviews of the 5.0/5.1 changes), with the reasoning:

- **A tool bar shows its id, not its text, when floated.** `MenuBuilder.buildToolBar` sets the
  swing name of the `JToolBar` to the id of the `<toolbar>` element, matching the convention that
  every other built component (menu, item, menu bar, popup) uses its id as its swing name so it
  can be found again and so `MenuInfoExporter` round trips it correctly. Swing's
  `BasicToolBarUI` happens to also use the component name as the title of the window that
  appears when a tool bar is dragged off into a floating window, an implementation detail, not a
  documented api. Using the resolved text as the name instead would fix the floating title but
  break the id-as-name convention and the exporter round trip. Given the choice, identity and
  round trip correctness won over a rarely used cosmetic detail.
- **`LookAndFeelMenuFactory` does not roll back a failed look and feel change.** Swing's
  `JRadioButtonMenuItem` moves the button group selection to the clicked item before the action
  listener runs; if `UIManager.setLookAndFeel` then fails (for instance `GTK+` is listed as
  installed but unsupported in a headless environment) the menu shows the failed look and feel as
  selected while the previous one is still in effect. A correct fix needs the menu to be bound to
  a model of the current look and feel and re-select it on failure (see
  `MenuBuilder.updateFromModels`), which is more machinery than this factory currently has.
- **An awt `MenuShortcut` (used by `MenuBuilder.buildAwtPopupMenu` for system tray menus) can
  only represent an optional shift modifier**, `java.awt.MenuShortcut` has no other constructor;
  ctrl/alt/meta modifiers of the accelerator are silently dropped for tray menu items. This is an
  awt api limitation, not something the library can work around; documented in the javadoc of
  `buildAwtPopupMenu`.
- **`ParentMenuResolver.getChildMenuElements` returns an empty list when the given parent itself
  is a `JPopupMenu`** (as opposed to a `JMenu` or `JMenuBar`), because the method identifies a
  child by comparing the popup menu's invoker to the given parent, and a popup menu is never its
  own invoker. Passing a `JMenu` or `JMenuBar` (the common case, and the only one exercised by
  `MenuBuilder`) works correctly. Effort to fix correctly for both parent kinds without
  regressing the existing behaviour: medium; not done for the 5.1 release given the low real
  world impact.

Findings from working through the pitest mutation report introduced in 5.2-SNAPSHOT:

- **A whole class of pitest survivors in `MenuItemInfoConverter` (`setFields`, `toJMenuBar`) are
  equivalent mutants, not coverage gaps.** Both methods guard a swing setter with
  `if (value != null) { component.setX(value); }`; pitest's "replaced equality check with true"
  mutation forces the setter to run with a `null` argument when the guard is skipped. Manually
  verified (`JButton`/`JMenuItem`) that `setActionCommand(null)`, `setName(null)`, `setIcon(null)`,
  `addActionListener(null)` and `AccessibleContext.setAccessibleName/setAccessibleDescription(null)`
  all produce the exact same observable state as never calling the setter, since swing already
  treats an absent value the same as an explicit `null`. No test can ever kill these mutants; they
  are expected to stay SURVIVED.
- **Every `java.awt.MenuItem`/`java.awt.Menu`/`java.awt.PopupMenu`-based code path shows near-zero
  pitest mutation coverage: `MenuItemInfoConverter.toMenuItem` and `MenuBuilder`'s
  `setAwtFields`/`addAwtChildren`/`buildAwtPopupMenu`/`getAwtComponent` (roughly 55 of
  `MenuBuilder`'s own survivors alone).** Their exercising tests
  (`MenuItemInfoConverterParameterizedTest.toAwtMenuItem`, `MenuBuilderTest.awtPopupMenuForTray`)
  are correctly guarded with `assumeFalse(GraphicsEnvironment.isHeadless())`, and pitest's
  mutation JVMs always run headless regardless of the actual desktop environment: verified that
  `./gradlew test` runs these tests successfully in this sandbox (`DISPLAY` is set,
  `GraphicsEnvironment.isHeadless()` is `false`), while the exact same tests show 0% coverage
  under `./gradlew pitest` in the same shell, and directly confirmed that constructing a
  `java.awt.MenuItem` throws `HeadlessException` under `-Djava.awt.headless=true`. This is a
  structural limitation of measuring AWT-based code with pitest, not a coverage gap; the guard
  must stay as is (removing it would break real headless CI runs of the normal test suite).
- **`ActionRegistry.toActionListener` builds its reflective invocation target with
  `Modifier.isStatic(member.getModifiers()) ? null : controller`, and pitest can remove that
  check for both the `Method` and the `Field` overload without a test noticing.** Verified
  directly against the JDK javadoc: `Method.invoke(Object, ...)` and `Field.get(Object)` both
  explicitly ignore the `obj`/target argument when the underlying member is static, so forcing
  the ternary to always pass `controller` instead of `null` is unobservable. Equivalent mutant.
- **`ActionRegistry.registerHandlers`'s class-hierarchy walk
  (`type != null && type != Object.class`) has two equivalent survivors on the same line.**
  `controller.getClass()` is always a concrete class, whose superclass chain always reaches
  `Object.class` before it could ever reach `null`; removing the `type != null` half of the
  guard never changes anything for a real object, and removing the `type != Object.class` half
  just adds one harmless extra iteration over `Object.class`'s own (never `@MenuAction`
  annotated) members. Equivalent mutants; see the analogous `ActionRegistry.find`/`.contains`
  survivors below for the same defense-in-depth pattern.
- **`ActionRegistry.find`/`.contains` have one equivalent survivor each for their
  `actionId != null`/`actionId == null` guard.** `LinkedHashMap.get(null)`/`.containsKey(null)`
  are both legal, safe no-ops that return the same result (`null`/`false`) the guard would have
  short-circuited to, since this registry only ever stores non-null keys (`register` requires a
  `@NonNull` id). Removing the guard is unobservable.
- **`LookAndFeelMenuFactory.newLookAndFeelMenuInfo`'s `UIManager.getLookAndFeel() != null` guard
  is an equivalent survivor in practice.** Swing always has a default look and feel installed
  from the moment `UIManager` is first touched by any code in the same JVM, so this branch's
  `null` side is not reachable in a real test without reflectively resetting `UIManager`'s
  internal state; not done given the low value. `LookAndFeelMenuFactory.installed()`'s
  `!result.containsKey(id)` dedup guard and one of the two `instanceof` checks in
  `isOceanActive()` also still have one survivor each after adding
  `LookAndFeelMenuFactoryMutationCoverageTest` (dedup collision and ocean/non-ocean-selection
  tests) — improved the class from 65% to 88% mutation score, the remaining three were not
  fully root-caused (unlike the entries above, these are not confirmed equivalent, just not
  successfully killed after a couple of test attempts each).
- **`MenuBuilder.buildMenuBar`'s own `applyAccessible(menuBarInfo, menuBar)` call is redundant,
  hence an equivalent survivor.** `JMenuBar` is built via
  `toMenuItemInfo(menuBarInfo, null).toJMenuBar()` just above it, and both
  `MenuBuilder.toMenuItemInfo` (copies `accessibleName`/`accessibleDescription` into the
  `MenuItemInfo`) and `MenuItemInfoConverter.toJMenuBar` (copies them onto the `JMenuBar`) already
  apply the exact same two fields from the exact same source before this call runs; removing it
  sets nothing that was not already set. `buildPopupMenu`'s and `buildToolBar`'s own
  `applyAccessible` calls are not redundant (those components are built with `new JPopupMenu(...)`
  / `new JToolBar(...)` directly) and are covered by `MenuBuilderMutationCoverageTest`.
  `applyAccessible`'s own two null guards (`getAccessibleName()`/`getAccessibleDescription() !=
  null`) are separately equivalent for the same reason as the
  `MenuItemInfoConverter.setFields`/`toJMenuBar` survivors above:
  `AccessibleContext.setAccessibleName/setAccessibleDescription(null)` is a no-op matching the
  unset default.
- **`MenuBuilder.convertValue`, `.getComponent(String)` and `.getButtonGroup(String)` have one
  survivor each for a leading `null`-input guard, all equivalent for the same reason as the
  `ActionRegistry.find`/`.contains` survivors above** (a `LinkedHashMap` lookup with a `null` key
  is a safe no-op returning the same result the guard would have short-circuited to; for
  `convertValue` specifically, `current == null || value == null` reduces to "return the raw
  `value` unconverted", which is also what every conversion branch below it would produce anyway
  since none of them can be reached with a `null` operand).
- **`MenuBuilder.toMenuItemInfo`'s `iconResolver != null` guard is unreachable-false, hence
  equivalent.** The `iconResolver` field defaults to `MenuItemInfoConverter::resolveIcon` and
  `withIconResolver` is `@NonNull`, so no reachable `MenuBuilder` state ever has a `null`
  `iconResolver`.

Survivors that are genuinely unsafe to execute in an automated test (as opposed to equivalent
mutants, these are real gaps that stay open on purpose):

- **`BrowserControlExtensions.browse`/`browseWithPlatformCommand` (20 survivors) launch a real
  web browser or OS process** (`Desktop.getDesktop().browse(uri)`, or
  `new ProcessBuilder("xdg-open"/"open"/"rundll32", ...).start()` as a headless fallback — pitest's
  JVM is headless, so `Desktop.isDesktopSupported()` is `false` there and every mutation run would
  actually spawn `xdg-open`). The one test that exercises a valid, resolvable url is deliberately
  `@Disabled("opens the real browser")`; that was a considered decision already, not an oversight,
  confirmed by checking that PIT's own coverage pass shows 0% coverage past the url-parsing/null
  checks (both under `./gradlew test`, where the url-parsing failure paths are covered, and under
  `./gradlew pitest`, which additionally confirms none of the disabled paths ever run).
- **`ToggleFullScreenAction.toggleFullScreen` (6 survivors) actually enters and leaves OS level
  full screen mode** (`GraphicsDevice.setFullScreenWindow`). Its one real test,
  `toggleFullScreenTogglesTheFullScreenWindow`, is guarded with
  `assumeFalse(GraphicsEnvironment.isHeadless())` for the same reason the AWT-headless entries
  above are guarded, and is skipped under pitest's headless JVM.
- **`LookAndFeels.setLookAndFeel(LookAndFeels, Window)` (4 survivors) calls `window.pack()`**,
  which needs a real display to compute a native layout; its test
  `setLookAndFeelWithWindow` is `assumeFalse(GraphicsEnvironment.isHeadless())`-guarded like the
  other window/frame-based tests above.
- **`ShowHelpDialogAction.onShowHelpDialog`, `ShowDialogAction.onShowDialog` (4 survivors
  combined) show a real `JDialog`**, and **`ExitApplicationAction.onExit` (1 survivor) calls
  `System.exit`, which would kill the test JVM itself if ever actually invoked**. All three
  already use the best available strategy: the logic is verified through an overridden callback
  (`onShowDialog`/`onExit` overridden in the test to record the call instead of running the real
  side effect) and the real side effect has one `assumeFalse(isHeadless())`-guarded integration
  test where showing a dialog is unavoidable.
- **The legacy `java.awt.MenuItem`/`PopupMenu` factories (`MenuItemFactory`, `PopupMenuFactory`,
  `JPopupMenuFactory.newPopupMenu`, `MenuItemInfo.toMenuItem`, 4 survivors) join the AWT-headless
  entry above**: `MenuItemInfo.toMenuItem()` delegates to `MenuItemInfoConverter.toMenuItem`, and
  `PopupMenuFactory`/`JPopupMenuFactory.newPopupMenu` construct `new java.awt.PopupMenu()`
  directly, another `MenuComponent` subclass that throws `HeadlessException` under
  `java.awt.headless=true`.

A few survivors from this last pass were not chased further given diminishing returns; they may
or may not be equivalent, the following were not conclusively root-caused (tracing that succeeded
for the `MenuInfoExtensions` entry below did not succeed for these):

- `ParentMenuResolver.getChildMenuElements` (the `JPopupMenu`/invoker-equality checks),
  `.getRootType` and `.toMenuClass`'s `instanceof` chain, despite `ParentMenuResolverTest`
  and `ParentMenuResolverParameterizedTest` already covering the equivalent scenarios by
  inspection.
- `KeyStrokeInfoExtensions.getKeyStrokeInfos`'s `inputMap != null` guard and one direction of the
  `JMenuItem`/accelerator conjunct.
- `LookAndFeelAction.onChangeOfLookAndFeel`'s remaining `component != null` direction, after
  `actionPerformedWithoutAComponentStillSetsTheLookAndFeel` already killed two of the three
  survivors on the same lines.
- `MouseDoubleClickListener.resolveMultiClickInterval`'s `instanceof Integer` check: forcing a
  real (non-headless-null) `awt.multiClickInterval` desktop property value would require calling
  `Toolkit.setDesktopProperty`, which is `protected` and not reachable without subclassing the
  process-wide `Toolkit` singleton — judged not worth the engineering effort for one mutator.

**`MenuInfoExtensions` (5 survivors: `find`, `collect`, `merge`×2, `orderByAnchor`) are all
equivalent, confirmed by tracing rather than by elimination.** `MenuInfo.children` is
`@Builder.Default List<MenuInfo> children = new ArrayList<>()`, so `getChildren()` never returns
`null` — only empty. Every survivor here guards a `for` loop or an `indexOf` call with a
`hasChildren()`/`isEmpty()` check whose only purpose is to skip work on an empty list; forcing the
guard to run anyway just iterates zero elements (or calls `indexOf` on an empty list, which
already returns `-1`, the same value the guard would have produced directly). `MenuExtensions
.parseMnemonic`'s `text.indexOf('&') < 0` guard is equivalent for the same reason: with no `&` in
the text, forcing the early return away just runs the character-copy loop instead, which
reconstructs the identical string.

## Candidates for a later version

### Model change notification

The model binding pulls the model state when the menu is built and on
`MenuBuilder.updateFromModels()`, and writes the model when the item changes. model-data models
have no change listeners, so an automatic update of the menu after the application changed a
model is not possible without an observable model. If model-data gets observable models the
builder can subscribe to them. Effort: depends on model-data.

### Icon keys and themed icons

`icon` is a classpath or file path. An `iconKey` resolved from the resource bundle (per theme or
look and feel) would allow switching icon sets without touching the menu files. Effort: small.

## Deliberately not planned

- **Recent files menu, window list, dynamic menus**: application logic. Build the entries with
  `MenuBuilder.insert` and `MenuBuilder.remove` when the state changes.
- **JSON or YAML menu format**: the `MenuInfo` model is format independent and the xml reader
  and writer are small, but a second format doubles the surface for no gain. It can be added as a
  separate module if a project really needs it.
- **Resolving actions by class name from the xml**: not refactoring safe, no constructor
  arguments, lets a configuration file instantiate arbitrary classes. Use `@MenuAction`
  controllers, `ActionProvider` services or a custom `ActionResolver`, see
  [actions.md](actions.md).
- **A dependency injection container**: the `ActionContext` is a plain map of application objects
  for providers. Applications that use a container plug it in with `MenuBuilder.withActionResolver`.
- **Swing component library features** (dialogs, panels, models): these live in
  swing-base-components and swing-model-components; menu-action stays the menu and action
  library.
