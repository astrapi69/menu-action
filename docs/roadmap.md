# Roadmap and non goals

What the library covers is listed in the [README](../README.md#features). This document lists the
ideas that are **not** implemented yet with the reasoning, and the things that are deliberately
left out.

## Candidates for a later version

### Model binding for check box and radio button items

Today a `checkbox` or `radio` item has a static `selected` attribute and fires its action. A
binding to a model-data `IModel<Boolean>` (check box) or `IModel<T>` (radio group with a value per
item) would keep the menu and the application state in sync in both directions, the same way
swing-model-components binds its components. Sketch:

```xml
<checkbox id="view.statusbar" text="Statusbar" model="statusbarVisible"/>
<radio id="view.mode.desktop" text="Desktop" group="view.mode" model="viewMode" value="DESKTOP"/>
<radio id="view.mode.panel" text="Panel" group="view.mode" model="viewMode" value="PANEL"/>
```

The `model` attribute would be resolved from the `ActionContext` (or a small `ModelRegistry`),
the builder would add an item listener that writes the model and a model listener that updates the
item. Effort: medium (new attributes, xsd, builder logic, tests). Wanted as soon as an application
needs it; until then the action of the item can write the model.

### More i18n keys

Only `textKey` is resolved from the resource bundle. `toolTipKey` (and maybe `iconKey` for
themed icons) would complete the picture. Effort: small, but it adds attributes to every element;
wait until a translated application needs tool tips.

### Tool bar options

`JToolBar` buttons show the text of the item. Typical tool bars are icon only with the text as
tool tip. An attribute like `showText="false"` on `toolbar` or per item, plus `rollover` and
`floatable` on the tool bar, would cover that. Effort: small.

### Accessibility

`accessibleName` and `accessibleDescription` attributes that are mapped to the accessible context
of the component. Effort: small, no demand yet.

### Exporting action ids

`MenuInfoExporter` exports existing swing menus to a `MenuInfo` tree, but a swing component does
not know an action id, so exported items use their id as action id. A mapping from the bound
`Action` object (for instance by class name or `Action.NAME`) to an id could be offered as an
optional strategy. Effort: small, only relevant while migrating programmatic menus.

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
