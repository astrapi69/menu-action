# Overview

<div style="text-align: center">

[![Java CI with Gradle](https://github.com/astrapi69/menu-action/actions/workflows/gradle.yml/badge.svg)](https://github.com/astrapi69/menu-action/actions/workflows/gradle.yml)
[![Open Issues](https://img.shields.io/github/issues/astrapi69/menu-action.svg?style=flat)](https://github.com/astrapi69/menu-action/issues)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.astrapi69/menu-action)](https://central.sonatype.com/artifact/io.github.astrapi69/menu-action)
[![Javadocs](https://javadoc.io/badge2/io.github.astrapi69/menu-action/javadoc.svg)](https://javadoc.io/doc/io.github.astrapi69/menu-action)
[![MIT license](http://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat)](http://opensource.org/licenses/MIT)
[![Donate](https://img.shields.io/badge/donate-❤-ff2244.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=GVBTWLRAZ7HB8)
[![Hits Of Code](https://hitsofcode.com/github/astrapi69/menu-action?branch=main)](https://hitsofcode.com/github/astrapi69/menu-action/view?branch=main)
[![Lines Of Code](https://sloc.xyz/github/astrapi69/menu-action)](https://github.com/astrapi69/menu-action)

</div>

Tiny library for hold utility classes for swing menu and actions. Menus can be built with a
fluent java api or loaded from a declarative xml file.

Requires Java 25 or later.

> Please support this project by simply putting a Github <!-- Place this tag where you want the button to render. -->
<a class="github-button" href="https://github.com/astrapi69/menu-action" data-icon="octicon-star" aria-label="Star astrapi69/menu-action on GitHub">Star ⭐</a>
>
> Share this library with friends on Twitter and everywhere else you can
>
> If you love this project [![donation](https://img.shields.io/badge/donate-❤-ff2244.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=GVBTWLRAZ7HB8)

## Note

No animals were harmed in the making of this library.

## License

The source code comes under the liberal MIT License, making menu-action great for all types of swing applications.

## Features

**Declarative menus (xml)**
- `MenuXmlReader` / `MenuXmlWriter`: load and save menu bars, menus, popup menus, tool bars and tray menus from a plain xml format (`menubar`, `menu`, `item`, `checkbox`, `radio`, `separator`, `popup`, `toolbar`, `tray`, container `menus`); ampersand mnemonics (`&amp;File`), `visible`/`enabled` attributes; schema validation with line and column errors against the shipped `menu.xsd`; parser hardened against xml external entities
- `MenuInfoExporter`: exports existing swing menu bars, menus, popup menus and tool bars to a `MenuInfo` tree for the migration to xml
- `MenuBuilder`: builds `JMenuBar`, `JMenu`, `JPopupMenu`, `JToolBar`, awt `PopupMenu` for tray icons and all item types from the `MenuInfo` tree; `javax.swing.Action` objects are bound with `setAction` so their enabled state stays in sync; check box and radio items bound to model-data `IModel` objects (`model`/`value`, `updateFromModels`); tool bar options `showText`, `floatable`, `rollover`; `toolTipKey`, `accessibleName`, `accessibleDescription`; actions resolved by id from an `ActionRegistry`; `MissingActionPolicy` (`FAIL`, `DISABLE`, `IGNORE`); texts from a `ResourceBundle` via `textKey`; icons from classpath or file with a pluggable resolver; button groups for radio items; placement with `anchor`/`relativeTo`; lookup of every built component by id
- `MenuInfoExtensions`: `find`, `flatten`, `orderByAnchor` (resolves chained anchors), `insertIndex` and `merge` of plugin menu contributions into an existing menu tree
- runtime changes of built menus: `MenuBuilder.insert` places a new menu, item or separator into a built `JMenu`, `JPopupMenu` or `JToolBar` (a `JMenuBar` accepts menus only, standard swing menu bars have no separator) by its anchor, `MenuBuilder.remove` takes it out again
- declarative actions: `@MenuAction` annotated controller methods and fields (`ActionRegistry.ofHandlers`), `ActionProvider` services for plugins with an `ActionContext`, `ActionResolver` chain for custom lookups

**Menu model and conversion**
- `MenuInfo` (serializable tree definition), `MenuItemInfo` (runtime definition with `ActionListener` and `Icon`), `KeyStrokeInfo` (serializable `KeyStroke`)
- `MenuItemInfoConverter`: `MenuItemInfo` to `JMenu`, `JMenuItem`, `JCheckBoxMenuItem`, `JRadioButtonMenuItem`, `JMenuBar`, awt `MenuItem` and back
- `MenuType`, `Anchor` and `BaseMenuId` (standard ids like `global.menu.file.exit` for application menus)

**Fluent factories and helpers**
- `JMenuFactory`, `JMenuItemFactory`, `JMenuBarFactory`, `JCheckBoxMenuItemFactory`, `JRadioButtonMenuItemFactory`, `JToolBarFactory`, `JPopupMenuFactory` (attaches a `PopupListener` to the component), awt `MenuItemFactory` and `PopupMenuFactory`
- `MenuExtensions`: mnemonics, accelerators (`ctrl`/`alt` shortcuts, parsable keystroke strings), add menu items with action listener and accelerator
- `KeyStrokeExtensions`: register keyboard shortcuts on any `JComponent`, parse keystroke strings, convert to `KeyStrokeInfo`
- `ParentMenuResolver`: navigate the menu hierarchy (`getParentMenu`, `getRootJMenu`, `getRoot`, `getMenuAncestors`, `getChildMenuElements`, `getAllMenuElements`, element type detection)

**Actions**
- `ExitApplicationAction`, `ToggleFullScreenAction`, `OpenFileAction` (template around `JFileChooser`), `OpenBrowserAction` / `BaseOpenBrowserAction`, `ShowDialogAction` / `ShowInfoDialogAction` / `ShowFrameAction` (templates for dialogs and frames), `ShowHelpDialogAction`
- look and feel actions for GTK, Metal (default and Ocean theme), Motif, Multi, Nimbus, Synth, System and Windows
- `LookAndFeels` enum: switch the look and feel at runtime and update the component tree or window; `LookAndFeelMenuFactory` generates the look and feel menu and its actions from the installed look and feels

**Listeners and bindings**
- `DocumentListenerAdapter`, `EnableButtonBehavior` (enables a button while a document has text), `StringBindingListener` (binds a document to a model-data `IModel`)
- `ItemBindListener`, `ItemModelListener`, `SingleItemModelListener` (bind combo box selections to models)
- `RequestFocusListener`, `MouseDoubleClickListener`, `MouseTripleClickCounterListener`, `MouseButton`, `MouseClickedType`

**Browser**
- `BrowserControlExtensions`: open urls in the default browser over `java.awt.Desktop` with `xdg-open`, `open` and `rundll32` fallback, `OS` detection

**Packaging**
- Java module `io.github.astrapisixtynine.menu.action`, Java 25, single runtime dependency `model-data`

## Define menus in xml

A menu bar, popup menu or tool bar can be described in a plain xml file:

```xml
<menubar id="global.menu.bar">
    <menu id="global.menu.file" text="File" mnemonic="F">
        <item id="global.menu.file.open" text="Open..." mnemonic="O" accelerator="ctrl O" action="openFile"/>
        <separator/>
        <item id="global.menu.file.exit" text="Exit" mnemonic="E" accelerator="alt F4" action="exit"/>
    </menu>
    <menu id="global.menu.view" text="View">
        <checkbox id="global.menu.view.statusbar" text="Statusbar" action="toggleStatusbar" selected="true"/>
        <radio id="global.menu.view.mode.desktop" text="Desktop mode" action="desktopMode" group="view.mode" selected="true"/>
        <radio id="global.menu.view.mode.panel" text="Panel mode" action="panelMode" group="view.mode"/>
    </menu>
</menubar>
```

Elements: `menubar`, `menu`, `item`, `checkbox`, `radio`, `separator`, `popup`, `toolbar` and the
container `menus`. Attributes: `id`, `text`, `textKey` (resource bundle key), `toolTip`, `mnemonic`
(a character or a key code), `accelerator` (a `KeyStroke` string like `ctrl S`), `action` (the id
in the `ActionRegistry`, defaults to the `id`), `actionCommand`, `enabled`, `selected`, `group`
(button group of radio items), `icon` (classpath resource or file), `anchor` (`FIRST`, `LAST`,
`BEFORE`, `AFTER`) and `relativeTo`. The schema is shipped as `menu.xsd` in the jar.

Load the file and build the swing components with a `MenuBuilder`. Actions are resolved by id from
an `ActionRegistry`, so the xml never references class names:

```java
ActionRegistry actions = ActionRegistry.empty()
    .register("openFile", e -> openFile())
    .register("exit", new ExitApplicationAction())
    .register("toggleStatusbar", e -> toggleStatusbar());

MenuBuilder menuBuilder = new MenuBuilder(actions)
    .withMissingActionPolicy(MissingActionPolicy.DISABLE) // FAIL (default), DISABLE or IGNORE
    .withResourceBundle(ResourceBundle.getBundle("menus")); // resolves textKey attributes

MenuInfo menuBarInfo = MenuXmlReader.readResource("menubar.xml");
JMenuBar menuBar = menuBuilder.buildMenuBar(menuBarInfo);
frame.setJMenuBar(menuBar);

// built components are available by id
menuBuilder.getComponent("global.menu.file.exit", JMenuItem.class).ifPresent(item -> item.setEnabled(false));
```

Plugins can contribute menu items to an existing menu with a contribution file that mirrors the
path to the target and is merged with `MenuInfoExtensions.merge`:

```xml
<menubar id="global.menu.bar">
    <menu id="global.menu.file">
        <item id="plugin.file.export" text="Export..." action="export" anchor="BEFORE" relativeTo="global.menu.file.exit"/>
    </menu>
</menubar>
```

```java
MenuInfo merged = MenuInfoExtensions.merge(menuBarInfo, MenuXmlReader.readResource("plugin-contribution.xml"));
```

Menus that are built already can be extended at runtime, for instance by a plugin that is loaded
later: `builder.insert("global.menu.bar", pluginMenuInfo)` places the new menu according to its
`anchor`/`relativeTo`, `builder.remove("plugin.menu")` takes it out again.

More xml features:

- `text="&amp;File"` marks the mnemonic with an ampersand (`&amp;&amp;` is a literal ampersand), an
  explicit `mnemonic` attribute wins; the marker also works in resource bundle texts
- `visible="false"` hides an item, `enabled="false"` disables it
- `<tray>` describes the popup menu of a `java.awt.TrayIcon`, built with
  `builder.buildAwtPopupMenu(trayInfo)` (awt `PopupMenu`, `Menu`, `MenuItem`, `CheckboxMenuItem`)
- `MenuXmlReader.validate(...)` checks a document against the shipped schema `menu.xsd` and returns
  the errors with line and column, `MenuXmlReader.readValidated(...)` reads only valid documents
- if a registered action is a `javax.swing.Action` the item is bound with `setAction`, so the
  enabled state, icon and tool tip of the action stay in sync with every menu item and tool bar
  button that uses it; the xml text has precedence over the action name

Check box and radio button items can be bound to model-data models. A check box writes an
`IModel<Boolean>`, a radio button writes its `value` to the model of its group and is selected when
the model holds that value (enums, booleans and numbers are converted from the value string):

```xml
<checkbox id="view.statusbar" text="Statusbar" model="statusbar"/>
<radio id="view.mode.desktop" text="Desktop" group="view.mode" model="viewMode" value="DESKTOP"/>
<radio id="view.mode.panel" text="Panel" group="view.mode" model="viewMode" value="PANEL"/>
```

```java
IModel<Boolean> statusbar = BaseModel.of(true);
IModel<ViewMode> viewMode = BaseModel.of(ViewMode.DESKTOP);
MenuBuilder builder = new MenuBuilder(actions).withModels(Map.of("statusbar", statusbar, "viewMode", viewMode));
// or .withModels(actionContext) when the models are registered in the ActionContext
...
viewMode.setObject(ViewMode.PANEL);
builder.updateFromModels(); // pulls the model state into the bound items
```

Further attributes: `toolTipKey` resolves the tool tip from the resource bundle like `textKey`;
`showText="false"` on a `toolbar` or a single item shows icon only buttons with the text as tool
tip, `floatable` and `rollover` configure the tool bar; `accessibleName` and
`accessibleDescription` set the accessible context of every component.

A `MenuInfo` tree can be written back with `MenuXmlWriter.toXml(menuInfo)`. Existing menus that
were built in java can be exported with `MenuInfoExporter.fromJMenuBar(menuBar)` (also
`fromJMenu`, `fromJPopupMenu`, `fromJToolBar`) and written to xml, which is the fastest way to
migrate a programmatic menu to the xml format. Components without a name get an id derived from
the parent id and the text, action ids come from a strategy like
`MenuInfoExporter.withActionIds(MenuInfoExporter.actionIdFromActionClass()).export(menuBar)`.

A ready made look and feel menu comes from `LookAndFeelMenuFactory`: `newLookAndFeelMenuInfo()`
creates a radio item per installed look and feel plus the metal ocean theme,
`registerActions(actions, frame)` registers the matching actions.

What is not implemented yet and what is deliberately left out is listed in
[docs/roadmap.md](docs/roadmap.md).

Actions can also be declared on a controller object instead of registering them one by one:

```java
public class MainController
{
    @MenuAction("exit")
    void exit() { frame.dispose(); }

    @MenuAction("openFile")
    void openFile(ActionEvent event) { ... }
}

ActionRegistry actions = ActionRegistry.ofHandlers(new MainController(frame));
```

Plugins contribute their actions as `ActionProvider` services (`java.util.ServiceLoader`) that are
collected with `actions.loadProviders(context)`. All ways to bind actions are described in
[docs/actions.md](docs/actions.md).

## Build menus in java

```java
JMenu fileMenu = MenuItemInfo.builder().text("File").mnemonic(MenuExtensions.toMnemonic('F')).build().toJMenu();
JMenuItem exitMenuItem = MenuItemInfo.builder().text("Exit").mnemonic(MenuExtensions.toMnemonic('E'))
    .keyStrokeInfo(KeyStrokeInfo.toKeyStrokeInfo(KeyStroke.getKeyStroke("alt F4")))
    .actionListener(new ExitApplicationAction()).build().toJMenuItem();
fileMenu.add(exitMenuItem);
```

## Build

The project is built with gradle 9 and requires Java 25. The `Makefile` wraps the most used tasks:

```
make build                  # full build with tests, spotless check and jacoco report
make test
make spotless-apply         # formats the sources and adds missing license headers
make dependency-updates     # shows newer versions of dependencies and plugins
make publish-local          # installs the artifact into the local maven repository
```

## Release and publishing

Releases are published to Maven Central over the Sonatype Central Portal. The short version: set
the version in `gradle.properties`, commit, push the tag `RELEASE-<version>` and publish the
deployment in the Portal. The complete process, the required secrets, the Central Portal staging
api and the troubleshooting are described in [docs/publishing.md](docs/publishing.md).

## gradle dependency

Replace the variable ${latestVersion} with the current latest version: [![Maven Central](https://img.shields.io/maven-central/v/io.github.astrapi69/menu-action)](https://central.sonatype.com/artifact/io.github.astrapi69/menu-action)

You can first define the version in the ext section and add than the following gradle dependency to
your project `build.gradle` if you want to import the core functionality of menu-action:

define version in file gradle.properties
```
menuActionVersion=${latestVersion}
```

or in build.gradle ext area

```
    menuActionVersion = "${latestVersion}"
```

and then add the dependency to the dependencies area

```
    implementation("io.github.astrapi69:menu-action:$menuActionVersion")
```

## 📸 Snapshots

[![Snapshot](https://img.shields.io/badge/dynamic/xml?url=https://central.sonatype.com/repository/maven-snapshots/io/github/astrapi69/menu-action/maven-metadata.xml&label=snapshot&color=red&query=.//versioning/latest)](https://central.sonatype.com/repository/maven-snapshots/io/github/astrapi69/menu-action/)

This section describes how to import snapshot versions into your project.
Add the following code snippet to your gradle file in the repositories section:

repositories {

   //...
```groovy
    maven {
        name = "Sonatype Nexus Snapshots"
        url = "https://central.sonatype.com/repository/maven-snapshots/"
        mavenContent {
            snapshotsOnly()
        }
    }
```
}

## Maven dependency

Maven dependency is now on Maven Central.
Check out the [Central Portal](https://central.sonatype.com/artifact/io.github.astrapi69/menu-action) for the latest releases.

Add the following maven dependency to your project `pom.xml` if you want to import the core
functionality of menu-action:

Than you can add the dependency to your dependencies:

    <properties>
            ...
```xml
        <!-- menu-action version -->
        <menu-action.version>${latestVersion}</menu-action.version>
```
            ...
    </properties>
            ...
        <dependencies>
            ...
```xml
            <!-- menu-action DEPENDENCY -->
            <dependency>
                <groupId>io.github.astrapi69</groupId>
                <artifactId>menu-action</artifactId>
                <version>${menu-action.version}</version>
            </dependency>
```
            ...
        </dependencies>

# Donations

This project is kept as an open source product and relies on contributions to remain being
developed. If you like this library, please consider a donation

over paypal: <br><br>
<a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=MJ7V43GU2H386" target="_blank">
<img src="https://www.paypalobjects.com/en_US/GB/i/btn/btn_donateCC_LG.gif" alt="PayPal this" title="PayPal – The safer, easier way to pay online!" style="border: none" />
</a>
<br><br>
or over bitcoin(BTC) with this address:

bc1ql2y99q7e8psndhcc3gferk03esw3qqf677rhjy

<img src="https://github.com/astrapi69/jgeohash/blob/master/src/main/resources/img/bc1ql2y99q7e8psndhcc3gferk03esw3qqf677rhjy.png"
alt="Donation Bitcoin Wallet" width="250"/>

or over FIO with this address:

FIO7tFMUVAA9cHiPPqKMfMXiSxHrbpiFyRYqTketNuM67aULuwjop

<img src="https://github.com/astrapi69/jgeohash/blob/master/src/main/resources/img/FIO7tFMUVAA9cHiPPqKMfMXiSxHrbpiFyRYqTketNuM67aULuwjop.png"
alt="Donation FIO Wallet" width="250"/>

or over Ethereum(ETH) with:

0xc057D159D3C8f3311E73568b334FF6fE82EB2b7D

<img src="https://github.com/astrapi69/jgeohash/blob/master/src/main/resources/img/0xc057D159D3C8f3311E73568b334FF6fE82EB2b7D.png"
alt="Donation Ethereum Wallet" width="250"/>

or over Ethereum Classic(ETC) with:

0xF708cA86D86C246B69c3F4BAe431eBbe0c2bfddD

<img src="https://github.com/astrapi69/jgeohash/blob/master/src/main/resources/img/0xF708cA86D86C246B69c3F4BAe431eBbe0c2bfddD.png"
alt="Donation Ethereum Classic Wallet" width="250"/>

or over Dogecoin(DOGE) with:

D5yi4Um8cpakd6yPRm2hGWuQ5nrVzhSSW1

<img src="https://github.com/astrapi69/jgeohash/blob/master/src/main/resources/img/D5yi4Um8cpakd6yPRm2hGWuQ5nrVzhSSW1.png"
alt="Donation Dogecoin Wallet" width="250"/>

or over Monero(XMR) with:

49bqeRQ7Bf49oJFVC72pqpe5hFbb62pfXDYPdLsadGGF81KZW2ZfrPZ8PbAVu5X2v1TYAspeczMya3cYQysNS4usRRPQHVw

<img src="https://github.com/astrapi69/jgeohash/blob/master/src/main/resources/img/49bqeRQ7Bf49oJFVC72pqpe5hFbb62pfXDYPdLsadGGF81KZW2ZfrPZ8PbAVu5X2v1TYAspeczMya3cYQysNS4usRRPQHVw.png"
alt="Donation Monero Wallet" width="250"/>

or over the donation buttons at the top.

## Semantic Versioning

The versions of menu-action are maintained with the Semantic Versioning guidelines.

Release version numbers will be incremented in the following format:

`<major>.<minor>.<patch>`

For detailed information on versioning you can visit the [wiki page](https://github.com/lightblueseas/mvn-parent-projects/wiki/Semantic-Versioning).

## Want to Help and improve it? ###

The source code for menu-action are on GitHub. Please feel free to fork and send pull requests!

Create your own fork of [astrapi69/menu-action/fork](https://github.com/astrapi69/menu-action/fork)

To share your changes, [submit a pull request](https://github.com/astrapi69/menu-action/pull/new/develop).

Don't forget to add new units tests on your changes.

## Contacting the Developers

Do not hesitate to contact the menu-action developers with your questions, concerns, comments, bug reports, or feature requests.
- Feature requests, questions and bug reports can be reported at the [issues page](https://github.com/astrapi69/menu-action/issues).

## Credits

|**GitHub Actions**|
|     :---:      |
|[![Java CI with Gradle](https://github.com/astrapi69/menu-action/actions/workflows/gradle.yml/badge.svg)](https://github.com/astrapi69/menu-action/actions/workflows/gradle.yml)|
|Special thanks to [GitHub Actions](https://github.com/features/actions) for providing a free continuous integration service for open source projects|
|     <img width=1000/>     |

|**Maven Central Portal**|
|     :---:      |
|[![Maven Central](https://img.shields.io/maven-central/v/io.github.astrapi69/menu-action?style=for-the-badge)](https://central.sonatype.com/artifact/io.github.astrapi69/menu-action)|
|Special thanks to the [Central Portal](https://central.sonatype.com) of sonatype for providing a free maven repository service for open source projects|
|     <img width=1000/>     |

|**javadoc.io**|
|     :---:      |
|[![Javadocs](https://javadoc.io/badge2/io.github.astrapi69/menu-action/javadoc.svg)](https://javadoc.io/doc/io.github.astrapi69/menu-action)|
|Special thanks to [javadoc.io](https://javadoc.io) for providing a free javadoc documentation for open source projects|
|     <img width=1000/>     |
