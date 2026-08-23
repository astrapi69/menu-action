# Declarative actions

The menu definitions in xml reference actions only by **id** (`action="exit"`), never by class
name. This document describes the ways to bind those ids to code, from explicit registration to
fully declarative controllers and plugin providers.

## 1. Explicit registration

The simplest way. Good for small applications and for tests:

```java
ActionRegistry actions = ActionRegistry.empty()
    .register("exit", new ExitApplicationAction())
    .register("openFile", e -> openFile())
    .registerAll(otherRegistry);

JMenuBar menuBar = new MenuBuilder(actions).buildMenuBar(MenuXmlReader.readResource("menubar.xml"));
```

If a menu item has no `action` attribute its `id` is used as action id, so
`<item id="exit" .../>` and `register("exit", ...)` belong together without further configuration.

## 2. Controller objects with `@MenuAction`

Mark methods or fields of any object with `@MenuAction("<id>")` and register the object. This is
the same idea as `onAction="#exit"` in JavaFX fxml: the xml stays free of class names, the ids are
declared next to the code that handles them and constructor arguments of the controller are no
problem.

```java
public class MainController
{
    private final JFrame frame;
    private final DocumentModel model;

    public MainController(JFrame frame, DocumentModel model) { ... }

    @MenuAction("exit")
    void exit()
    {
        frame.dispose();
    }

    @MenuAction("openFile")
    void openFile(ActionEvent event)
    {
        // the ActionEvent parameter is optional
        model.open(chooseFile());
    }

    @MenuAction("toggleFullscreen")
    final ActionListener toggleFullscreen = new ToggleFullScreenAction("Toggle Fullscreen", frame);
}

ActionRegistry actions = ActionRegistry.ofHandlers(new MainController(frame, model), new HelpController());
// or
actions.registerHandlers(new PluginController());
```

Rules:

- a method has **no parameter** or a single **`ActionEvent`** parameter; static methods are allowed
- a field must be of the type `ActionListener` (or a subtype like `Action`) and not null at the time
  of registration
- inherited members of superclasses are included
- the same id twice in one controller is an error (`IllegalStateException`); registering a second
  controller with an id of the first replaces the first, like `register` does
- exceptions thrown by the handler are propagated to the caller of `actionPerformed`: runtime
  exceptions and errors unchanged, checked exceptions wrapped in an `IllegalStateException`
- non public members are made accessible with reflection. In a **named module** this requires that
  the package of the controller is opened to `io.github.astrapisixtynine.menu.action`:

  ```java
  module my.app
  {
      requires io.github.astrapisixtynine.menu.action;
      opens my.app.controller to io.github.astrapisixtynine.menu.action;
  }
  ```

  or simply declare the annotated members `public`.

## 3. Plugins with `ActionProvider`

An `ActionProvider` is a `java.util.ServiceLoader` service. Every module or jar that contributes
actions implements the interface and declares it as service, the application loads all providers
at start. The `ActionContext` carries the application objects the providers need, for instance the
frame or the models.

Plugin side:

```java
public class ExportPluginActions implements ActionProvider
{
    @Override
    public void registerActions(ActionRegistry registry, ActionContext context)
    {
        JFrame frame = context.require(JFrame.class);
        DocumentModel model = context.get("document", DocumentModel.class).orElseThrow();
        registry.register("export", new ExportAction(frame, model));
        // or declaratively
        registry.registerHandlers(new ExportController(frame, model));
    }
}
```

```java
// module-info.java of the plugin
module my.plugin
{
    requires io.github.astrapisixtynine.menu.action;
    provides io.github.astrapi69.swing.menu.build.ActionProvider with my.plugin.ExportPluginActions;
}
```

On the classpath without modules the service is declared in the file
`META-INF/services/io.github.astrapi69.swing.menu.build.ActionProvider` with the line
`my.plugin.ExportPluginActions`.

Application side:

```java
ActionContext context = ActionContext.empty()
    .put(frame)                    // registered under the class name, found by type
    .put("document", documentModel); // registered under a key

ActionRegistry actions = ActionRegistry.ofHandlers(new MainController(frame, documentModel))
    .loadProviders(context);                       // providers of the application modules
    // .loadProviders(pluginClassLoader, context)  // providers of a plugin class loader

MenuInfo menuBar = MenuXmlReader.readResource("menubar.xml");
for (MenuInfo contribution : pluginMenuContributions)
{
    MenuInfoExtensions.merge(menuBar, contribution);
}
JMenuBar bar = new MenuBuilder(actions).buildMenuBar(menuBar);
```

Together with `MenuInfoExtensions.merge` and the `anchor`/`relativeTo` attributes a plugin can
ship its own menu xml and its actions, and the application does not know anything about the
plugin at compile time.

## 4. Custom resolvers

`ActionRegistry` implements the functional interface `ActionResolver`
(`Optional<ActionListener> resolve(String actionId)`). The `MenuBuilder` asks the registry first
and then every resolver added with `withActionResolver` in order. This is the hook for anything
that does not fit in the registry, for instance a lazily created action or a lookup in a dependency
injection container:

```java
MenuBuilder builder = new MenuBuilder(actions)
    .withActionResolver(id -> Optional.ofNullable(injector.getInstance(id, ActionListener.class)))
    .withActionResolver(ActionResolver.of(resolverA, resolverB)); // composite, first match wins
```

A resolver that instantiates classes by name from the menu xml is deliberately not provided:
it is not refactoring safe, it can not pass constructor arguments and it lets a configuration file
instantiate arbitrary classes. If such a resolver is really needed it can be added with
`withActionResolver` and restricted to an allowed package.

## Missing actions

If no registry entry and no resolver can resolve the id of a menu item, the `MissingActionPolicy`
of the `MenuBuilder` decides:

| Policy | Behavior |
|---|---|
| `FAIL` (default) | `IllegalStateException` with the action id and the menu id |
| `DISABLE` | the menu item is created but disabled |
| `IGNORE` | the menu item is created without action listener |

Menus (`<menu>`) never require an action, they are containers.

## Summary

| Need | Use |
|---|---|
| a few actions, tests | `ActionRegistry.register` |
| application controllers | `@MenuAction` + `ActionRegistry.ofHandlers` / `registerHandlers` |
| plugins, optional modules | `ActionProvider` + `ActionContext` + `loadProviders` |
| external containers, lazy actions | `ActionResolver` + `MenuBuilder.withActionResolver` |
