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
