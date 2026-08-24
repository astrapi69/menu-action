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
- **`MenuItemInfoConverter.toMenuItem` (the `java.awt.MenuItem` factory for system tray menus)
  shows 0% mutation coverage because its one exercising test,
  `MenuItemInfoConverterParameterizedTest.toAwtMenuItem`, is guarded with
  `assumeFalse(GraphicsEnvironment.isHeadless())`.** The guard is required, not optional:
  constructing a `java.awt.MenuItem` throws `HeadlessException` when
  `java.awt.headless=true` (verified directly), unlike the swing components used everywhere else
  in this library. Mutation testing runs headless, so this method's mutants stay uncovered there
  even though the test passes and covers it on a normal desktop run.
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
