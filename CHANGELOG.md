## Change log
----------------------

Version 5.1-SNAPSHOT
-------------

ADDED:

- new annotation MenuAction for declare actions on controller methods and fields, registered with ActionRegistry.ofHandlers and ActionRegistry.registerHandlers
- new service provider interface ActionProvider with ActionContext for plugins that contribute actions, loaded with ActionRegistry.loadProviders over the ServiceLoader
- new functional interface ActionResolver; ActionRegistry implements it and MenuBuilder.withActionResolver adds further resolvers that are asked after the registry
- new documentation docs/actions.md for the declarative actions and docs/publishing.md for the release process
- new Makefile targets central-list, central-upload, central-upload-repository and central-drop for the staging repositories of the Central Portal
- new methods MenuBuilder.insert and MenuBuilder.remove for add and remove menus, items and separators in already built menu bars, menus, popup menus and tool bars at runtime, placed by anchor and relativeTo
- new method MenuBuilder.buildToolBarComponent and MenuInfoExtensions.insertIndex
- new class MenuInfoExporter that exports existing JMenuBar, JMenu, JPopupMenu and JToolBar objects to a MenuInfo tree for the migration to xml
- new method MenuBuilder.buildAwtPopupMenu for system tray menus (awt PopupMenu, Menu, MenuItem, CheckboxMenuItem) with MenuBuilder.getAwtComponent; new xml element tray for MenuType.SYSTEM_TRAY
- new methods MenuXmlReader.validate, readValidated and readValidatedResource that validate against the shipped schema menu.xsd and report the errors with line and column
- new attribute visible and the fields MenuInfo.visible and MenuItemInfo.visible
- new ampersand mnemonic marker in texts (text="&amp;File"), parsed by MenuXmlReader and by MenuBuilder for resolved resource bundle texts; new method MenuExtensions.parseMnemonic
- new class LookAndFeelMenuFactory that creates the look and feel menu and the actions from the installed look and feels
- new documentation docs/roadmap.md with the not yet implemented ideas and the non goals
- new model binding of check box and radio button menu items and tool bar toggle buttons to model-data IModel objects with the attributes model and value, MenuBuilder.withModels, withModelResolver, withValueConverter, updateFromModels and convertValue
- new attribute toolTipKey that is resolved like textKey from the resource bundle
- new tool bar attributes showText (tool bar and item), floatable and rollover
- new attributes accessibleName and accessibleDescription for all menu components, also in MenuItemInfo
- new action id strategies for the export: MenuInfoExporter.withActionIds(...).export(component) with actionIdFromActionClass and actionIdFromActionName

FIXED:

- MenuInfoExtensions.orderByAnchor placed a child at the end if its relative child was itself anchored and came later in the document; the placement is now repeated until all references are resolved, cycles are appended
- MenuInfoExtensions.orderByAnchor reversed the declaration order of several children anchored AFTER the same target and flipped the order again on every repeated call, for instance every merge of a plugin contribution; anchored children with the same target are now placed as one block that keeps their declaration order and is stable under repeated calls
- MenuBuilder required a registered action for a check box or radio button menu item even when it had a model binding, so the model binding documented in the README threw IllegalStateException under the default FAIL policy or was rendered permanently disabled under DISABLE; a model binding now counts as behaviour on its own
- MenuBuilder.updateFromModels() wrote the pulled value back into the model through the item listener, so a read only model (the default IModel#setObject throws UnsupportedOperationException) crashed on a pure refresh and a writable model got a spurious write on every refresh; pulls no longer trigger a write back
- MenuBuilder.bindModel converted the value of a radio button item once when the item was built; if the model held no object yet at that point the raw string was written to the model instead of the converted value, poisoning a typed model; the value is now converted on every read and write
- MenuXmlWriter did not re-escape a literal ampersand of the text, and MenuBuilder resolved the ampersand mnemonic marker of an already unescaped text a second time, together corrupting a literal ampersand on a read, write, read round trip unless it happened to be followed by whitespace; MenuXmlReader and MenuXmlWriter now pass the text attribute through unresolved, MenuBuilder is the only place that resolves the marker
- MenuXmlReader.toBoolean accepted any string as a valid boolean and silently read everything but the literal "true" as false, so a typo like enabled="yes" passed unnoticed; boolean attributes now only accept true, false, 1 or 0 and throw for anything else
- MenuXmlReader.validate used a differently hardened parser than the reader itself, so a document with a disallowed doctype was reported as schema valid by validate() and then failed with an unrelated exception from readValidated(); validate() now validates the same hardened parse result the reader uses
- MenuInfoExporter generated a name only from the ASCII letters and digits of the text, so different texts like "Neu!" and "Neu?" or "Save As" and "save as" collided on the same generated id and non ASCII characters like an umlaut were silently stripped instead of kept; names are now deduplicated with a numeric suffix per exporter instance and the slug keeps every unicode letter and digit
- MenuInfoExporter and MenuItemInfoConverter.fromJMenuItem (and the other from* methods) exported the swing fallback of AbstractButton#getActionCommand (the button text) as an explicit actionCommand even when none was set, so a plain item without an action command gained one on export; the fallback is now filtered out like the accessible name already was
- MenuInfoExporter exported the accessible description of a component even when swing had only derived it from the tool tip, duplicating the tool tip into the accessible description; only an explicitly set description is exported now, the same rule the accessible name already followed
- MenuItemInfoConverter.resolveIcon set the description of the resolved icon to the resolved url or the absolute file path, so MenuInfoExporter re-exported an icon path that could not be resolved again; the description is now the original icon path unchanged
- MenuItemInfoConverter.toJMenuBar ignored the accessibleName and accessibleDescription fields of the given MenuItemInfo, unlike every other to* method
- LookAndFeelMenuFactory's action for the metal look and feel called plain UIManager.setLookAndFeel, which keeps the metal theme unchanged; if the ocean theme was active the metal action had no visible effect and never left the ocean theme
- LookAndFeelMenuFactory derived the fallback id of an installed look and feel from its display name, so two installed look and feels with the same or a normalised-equal display name collided on the same id and one became unreachable; the id is now derived from the class name and the reserved ocean key can not be produced by an installed look and feel anymore
- LookAndFeelMenuFactory did not map the macOS system look and feel class name to the system key, so on macOS the "system" menu entry was never populated
- LookAndFeelAction threw a NullPointerException instead of a caught exception when performed on an action created with the no-arg or the name-only constructor (no component and/or no look and feel set, for instance every LookAndFeelNimbusAction()); it now skips the component tree update when there is no component and logs and returns when there is no look and feel
- ActionRegistry.registerHandlers rejected a normal subclass override of an @MenuAction method with an "action id registered twice" exception, and a javac generated bridge method that copies the annotation of the method it delegates to could cause the same false exception; the most derived declaration now wins and bridge and synthetic members are skipped
- ActionRegistry.registerHandlers accepted a @MenuAction method with any parameter type assignable from ActionEvent (for instance Object, EventObject or AWTEvent), contradicting the documented "single ActionEvent parameter"; only an exact ActionEvent parameter is accepted now
- ActionRegistry.loadProviders(ActionContext) used the thread context class loader although the javadoc promised the class loader of this class, so the found providers could depend on which thread called it; it now explicitly uses the class loader of ActionRegistry, matching the javadoc
- OS.MAC_PATTERN required the whole os.name system property to equal "mac" or "darwin", so a real macOS ("Mac OS X") was never detected as MAC and BrowserControlExtensions used the xdg-open fallback instead of open
- OS.getOperatingSystem() threw a NullPointerException instead of returning OTHER when the os.name system property was absent
- BrowserControlExtensions.displayURLonStandardBrowser(Component, String) could throw HeadlessException from the failure dialog in a headless environment instead of just returning false
- MenuBuilder.insert into a JMenuBar with a separator child fell through to a generic error naming the separator's id as "null"; it now throws a specific message explaining that a JMenuBar has no separator
- menu.xsd did not allow anchor, relativeTo and visible on a separator and did not allow a radio element in a toolbar, although the reader, the writer and MenuBuilder already supported both; a document using either was rejected by validate()/readValidated() although read()/fromXml() accepted it
- 9 bugs in the previously untested legacy packages, found while adding unit tests for them: ItemBindListener and SingleItemModelListener threw a NullPointerException when ItemSelectable#getSelectedObjects() returned null (a deselected check box); MouseDoubleClickListener and MouseTripleClickCounterListener threw a NullPointerException in their default constructor in a headless environment because the desktop property awt.multiClickInterval is not published there; MouseTripleClickCounterListener only fired a triple click on the 4th click of a sequence instead of the 3rd and mapped the right mouse button (BUTTON3) to MouseButton.LEFT; MenuXmlElements.toMenuType(null) threw a NullPointerException instead of returning an empty optional

CHANGED:

- MenuItemInfoConverter.setFields binds a javax.swing.Action with setAction instead of addActionListener, so the enabled state, icon and tool tip of the action stay in sync with all bound components; explicit fields of the MenuItemInfo keep precedence
- the publish workflow and the Makefile release target upload the staging repository to the Central Portal after publishing, so the deployment is visible there
- module-info: new uses io.github.astrapi69.swing.menu.build.ActionProvider
- the javadoc task disables the doclint group 'missing' for the lombok generated members
- README: fixed the javadoc.io and lines of code badges, added the feature overview, the build and the release sections
- MenuBuilder.convertValue also converts byte, short, float, BigInteger and BigDecimal model values, matching the javadoc of withValueConverter
- new unit tests for every previously untested class of the library (actions, listeners, look and feel actions, factories, MenuExtensions, KeyStrokeExtensions, PopupListener, OS, BaseMenuId, MenuType, Anchor, KeyStrokeInfo) and new parameterized tests for the xml round trip, the MenuBuilder combinatorics, the converter and exporter classes, ActionRegistry, ActionContext, ActionResolver, MenuInfoExtensions, ParentMenuResolver, LookAndFeels and LookAndFeelMenuFactory; two independent multi agent reviews of the 5.1 changes found and verified the fixes above

KNOWN LIMITATIONS (see docs/roadmap.md):

- MenuBuilder.buildToolBar uses the id of a tool bar as its swing name, so the resolved text of the toolbar element has no effect on the title swing shows when the tool bar is floated; changing this would break the name-as-id convention that MenuInfoExporter and every other built component relies on
- LookAndFeelMenuFactory does not roll back the radio button selection if installing the chosen look and feel fails (for instance GTK+ is listed as installed but unsupported in a headless environment); bind the look and feel menu to a model to keep it in sync in that case


Version 5.0
-------------

ADDED:

- new declarative xml menu format with the elements menubar, menu, item, checkbox, radio, separator, popup and toolbar and the attributes id, text, textKey, toolTip, mnemonic, accelerator, action, actionCommand, enabled, selected, group, icon, anchor and relativeTo
- new class MenuXmlReader that reads menu definitions from xml (string, file, classpath resource, stream) into a MenuInfo tree; the parser is hardened against xml external entity attacks
- new class MenuXmlWriter that writes a MenuInfo tree back to xml
- new xml schema src/main/resources/menu.xsd for validation and IDE completion of menu xml files
- new class MenuBuilder that builds JMenuBar, JMenu, JPopupMenu, JToolBar and menu items from a MenuInfo tree with action resolution from an ActionRegistry, resource bundle texts, icon resolution, button groups for radio items, anchor placement and a lookup of all built components by id
- new class ActionRegistry that maps action ids to ActionListener objects
- new enum MissingActionPolicy (FAIL, DISABLE, IGNORE) for menu items without a registered action
- new class MenuInfoExtensions with find, flatten, orderByAnchor and merge for plugin menu contributions
- new fields textKey, toolTip, actionId, enabled, selected, group, icon and children in MenuInfo; new fields toolTip, enabled, selected and icon in MenuItemInfo
- new enum value MenuType.SEPARATOR
- new method MenuItemInfoConverter.fromJMenuBar(JMenuBar), MenuItemInfoConverter.setFields(MenuItemInfo, AbstractButton) and MenuItemInfoConverter.resolveIcon(String)
- new method ParentMenuResolver.getMenuAncestors(JMenuItem)
- new methods LookAndFeels.isMetalTheme() and LookAndFeels.newMetalTheme()
- new method BrowserControlExtensions.browse(URI)
- new method BaseMenuId.getBaseMenuIdKeys()
- new demo class MenuXmlDemo that loads the menu bar from menubar.xml
- new unit tests for MenuXmlReader, MenuXmlWriter, MenuBuilder, ActionRegistry, MenuInfoExtensions, LookAndFeels and the converter classes
- new Makefile with build, test, release and publish targets
- new license header file src/main/resources/license-header.txt for the spotless licenseHeaderFile step
- new gradle file tagging.gradle with the tagRelease task based on a plain git Exec task
- new publishing repository configuration for the Central Portal (releases over the OSSRH staging API, snapshots to central.sonatype.com) with credentials from CENTRAL_USERNAME/CENTRAL_PASSWORD or the gradle properties centralUsername/centralPassword
- new github-actions workflow publish.yml that publishes to Maven Central on RELEASE-* tags with in-memory GPG signing
- new gradle plugin org.gradle.toolchains.foojay-resolver-convention in version 1.0.0 for automatic JDK provisioning

FIXED:

- LookAndFeels.OCEAN pointed to javax.swing.plaf.metal.OceanTheme which is a MetalTheme and not a LookAndFeel, so UIManager.setLookAndFeel threw a ClassCastException; the metal look and feels now install their theme before the MetalLookAndFeel
- LookAndFeelMetalAction compared the constant NAME with "Ocean" and never selected the ocean theme; the ocean theme is now selected if the action name contains 'ocean'
- MenuItemInfoConverter.fromJMenuBar hardcoded the action class io.github.astrapi69.awt.action.NoAction from the test dependency awt-extensions
- the accelerator of a menu item was read from the undocumented swing client property '_WhenInFocusedWindow'; it is now read from JMenuItem.getAccelerator and the input map of the component
- BrowserControlExtensions used reflection on com.apple.eio.FileManager which is not accessible under the module system and a dated browser list (netscape, galeon, kazehakase); it now uses java.awt.Desktop with xdg-open, open and rundll32 as fallback
- ParentMenuResolver.getChildMenuElements threw a NullPointerException for popup menus without invoker
- PopupListener printed the event source to System.out
- BaseMenuId.getBaseMenuIdsAsMap contained the key LOOK_AND_FEEL_SYSTEM twice and missed TOOL_BAR
- the javadoc task excluded all classes and produced an empty javadoc jar
- the /gradle rule in .gitignore swallowed new gradle build script files

CHANGED:

- update of the minimum java version to 25 (source and target compatibility, gradle toolchain, github-actions workflows)
- removed the xstream based menu serialization of TreeIdNode maps together with the class MenuVisitorExtensions; menus are now defined in the new xml format and built with MenuBuilder
- removed the fields ordinal and actionClass from MenuInfo and the field ordinal from MenuItemInfo; the order of menu items is the document order with optional anchors, actions are resolved by id from the ActionRegistry
- removed the reflection based method MenuItemInfoConverter.toMenuItemInfo(MenuItemInfo, String)
- removed the enum Browsers
- BrowserControlExtensions.displayURLonStandardBrowser returns now a boolean instead of Object
- ParentMenuResolver consolidated to a single ancestor walk, the public methods keep their behavior
- MenuItemInfo: removed the misleading @Setter annotation on the final fields
- removed the unused core dependencies data-api, gen-tree, id-generate, jobj-core, jobj-reflect, silly-collection, throwable, tree-api and visitor-pattern; model-data is the only remaining core dependency
- removed the unused test dependencies assertj-swing, awt-extensions, file-worker, junit-jupiter-extensions, meanbean, silly-io and xstream-extensions
- module-info: new requires java.xml, new exports io.github.astrapi69.swing.menu.build and io.github.astrapi69.swing.menu.xml, removed requires of jobj.core, id.generate, gen.tree, jobj.reflect.main, silly.collection and throwable
- interactive demo classes renamed from *Test to *Demo so that only real unit tests are discovered by the test engine; removed the duplicate demo SampleJTableWithPopup
- migrate publishing to Central Portal (snapshots to central.sonatype.com, signing with in-memory GPG keys from environment variables with fallback to the local gpg command)
- removed the grgit gradle plugin; the tagRelease task now uses a plain git Exec task, so the gradle configuration cache works without workarounds
- removed the license-gradle-plugin, license headers are now managed by the spotless licenseHeaderFile step
- enabled the gradle configuration cache
- github-actions workflow: removed obsolete ossrh secrets, updated setup-gradle to v4 and codecov-action to v5
- README: replaced the dead travis-ci and maven-badges.herokuapp.com badges with the github-actions badge and the shields.io maven-central badge, updated the sonatype links to the Central Portal, documented the xml menu format
- update gradle to new version 9.7.0
- update of gradle-plugin dependency 'io.freefair.lombok' to new version 9.5.0
- update of gradle-plugin dependency 'com.diffplug.spotless:spotless-plugin-gradle' to new version 8.10.0
- update of gradle-plugin dependency ben-manes versions to new version 0.61.0 with new plugin id io.github.ben-manes.versions
- update of gradle-plugin dependency 'nl.littlerobots.version-catalog-update' to new version 1.1.1
- update of jacoco tool version to 0.8.15
- update of dependency lombok to new version 1.18.46
- update of dependency model-data to new patch version 3.2.1
- update of test dependency junit-jupiter to new major version 6.1.3

Version 4.1
-------------

ADDED:

- new extension class KeyStrokeInfoExtensions for handling KeyStrokeInfo and KeyStroke objects

CHANGED:

- update of gradle to new version 8.10.2
- update of dependency gen-tree to new minor version 10.1
- update of dependency tree-api to new minor version 2.1
- update of test dependency junit-jupiter to new patch version 5.11.1

Version 4.0
-------------

ADDED:

- new libs.versions.toml file for new automatic catalog versions update
- new dependency jobj-reflect in version 2.4
- new default constructors on all action classes

CHANGED:

- rename of module to new name 'io.github.astrapisixtynine.menu.action'
- update of gradle to new version 8.10.1
- update of lombok to new version 1.18.34
- update of gradle-plugin dependency 'io.freefair.gradle:lombok-plugin' to new version 8.10
- update of gradle-plugin dependency 'com.github.ben-manes.versions.gradle.plugin' to new version 0.51.0
- update of gradle-plugin dependency 'org.ajoberstar.grgit:grgit-gradle' to new version 5.2.2
- update of gradle-plugin dependency 'com.diffplug.spotless:spotless-plugin-gradle' to new minor version 7.0.0.BETA2
- update of dependency awt-extensions to new minor version 1.2
- update of dependency jobj-core to new minor version 8.2
- update of silly-collection dependency to new major version 28
- update of dependency gen-tree to new major version 10
- update of dependency data-api to new major version 5
- update of dependency tree-api to new major version 2
- update of test dependency file-worker to new minor version 17.4
- update of test dependency silly-io to new major version 3
- update of test dependency junit-jupiter to new release candidate version 5.11.0-M2
- replaced obsolete package.html with package-info.java files

Version 3.4
-------------

ADDED:

- new field UNKNOWN in enum MenuType and Anchor for default return value in a search algorithm

CHANGED:

- update of gradle to new version 8.4
- update of lombok to new version 1.18.30
- update of gradle-plugin dependency 'io.freefair.gradle:lombok-plugin' to new version 8.4
- update of gradle-plugin dependency 'com.github.ben-manes.versions.gradle.plugin' to new version 0.49.0
- update of gradle-plugin dependency 'com.diffplug.spotless:spotless-plugin-gradle' to new version 6.22.0
- update of dependency gen-tree to new major version 9
- removed dependency 'javax.help:javahelp'
- moved dependency xstream-extensions to test dependency

Version 3.3
-------------

CHANGED:

- removed lombok experimental annotation class SuperBuilder from MenuInfo and MenuItemInfo
- MenuItemInfo does not extend from MenuInfo instead it has a field from it
- MenuInfo has no default type anymore it has to be set explicitly

Version 3.2
-------------

ADDED:

- new test dependency awt-extensions for unit tests
- missing packages for export

CHANGED:

- removed classes that were tagged as deprecated

Version 3.1
-------------

ADDED:

- new class file module-info.java that modularize this library

Version 3
-------------

ADDED:

- new enum class for the mouse button type
- new enum class for the mouse clicked count
- new enum class Anchor created for specify the order of a menu component and posible relative position to another target menu component
- new field anchor and relativeToMenuId in class MenuInfo for specify the order of a menu component
- new visitor class for reindex a BaseTreeNode
- new merge method in class MenuInfoTreeNodeConverter for merge multiply menu xml strings

CHANGED:

- update of JDK to newer version 17
- update of gradle to new version 8.3
- update of lombok to new version 1.18.28
- update of gradle-plugin dependency 'io.freefair.gradle:lombok-plugin' to new version 8.3
- update of gradle-plugin dependency 'com.github.ben-manes.versions.gradle.plugin' to new version 0.48.0
- update of gradle-plugin dependency 'org.ajoberstar.grgit:grgit-gradle' to new version 5.2.0
- update of gradle-plugin dependency 'com.diffplug.spotless:spotless-plugin-gradle' to new version 6.21.0
- update of dependency data-api to new minor version 4.1
- update of test dependency tree-api to new minor version 1.5
- update of test dependency gen-tree to new minor version 8.4
- update of silly-collection dependency to new major version 27
- update of silly-io dependency to new minor version 2.2
- update of test dependency file-worker to new version 17
- update of test dependency junit-jupiter to new release candidate version 5.10.0

Version 2.7
-------------

ADDED:

- new factory class MenuItemFactory for create MenuItem objects
- new factory class PopupMenuFactory for create PopupMenu objects
- new converter class MenuItemInfoConverter for convert MenuItemInfo to the corresponding menu class and back

CHANGED:

- update of gradle-plugin dependency 'com.diffplug.spotless:spotless-plugin-gradle' to new version 6.12.1
- update of silly-collection dependency to new version 20.3

Version 2.6
-------------

ADDED:

- new dependency tree-api in new minor version 1.2
- new dependency silly-collection in new minor version 20.2
- new factory class JCheckBoxMenuItemFactory for create JCheckBoxMenuItem objects
- new factory class JRadioButtonMenuItemFactory for create JRadioButtonMenuItem objects
- new factory methods in MenuItemInfo for create JRadioButtonMenuItem and JCheckBoxMenuItem objects

CHANGED:

- update of gradle-plugin dependency 'io.freefair.gradle:lombok-plugin' to new version 6.6.1
- moved enum types from package 'enumtype' to new 'enumeration' package
- update of dependency gen-tree to new version 7.4

Version 2.5
-------------

ADDED:

- new extension class for visit menus
- new factory method for create a menu from a BaseTreeNode and an action map

CHANGED:

- removed deprecated classes MenuFactory and PopupMenuInfo
- rename of module from menu-actions to menu-action

Version 2.4
-------------

ADDED:

- new converter class for transform MenuInfo to BaseTreeNode and back
- new listener class for listen on double and triple clicks

CHANGED:

- update of gradle-plugin dependency 'io.freefair.gradle:lombok-plugin' to new version 6.6
- removed duplicated fields in MenuInfo
- renamed JMenuItemInfo to MenuItemInfo
- tagged PopupMenuInfo as deprecated so only MenuItemInfo will be used


Version 2.3
-------------

ADDED:

- new enum class MenuType for the menu type
- new enum class BaseMenuId for the menu ids
- new class MenuBarFactory created for create a JMenuBar over xml
- new dependency io.github.astrapi69:gen-tree in version 7
- new dependency io.github.astrapi69:visitor in version 6
- new dependency io.github.astrapi69:id-generate in version 1.1
- new dependency io.github.astrapi69:xstream-extensions in version 1.1
- new factory classes for all subtypes of MenuElement created

CHANGED:

- tagged MenuFactory as deprecated and moved all factory methods to appropriate factory classes

Version 2.2
-------------

ADDED:

- new bean class KeyStrokeInfo for store keystroke information for create KeyStroke objects
- new bean class MenuInfo for store information for create menu items

CHANGED:

- update of gradle to new version 7.6
- update of gradle-plugin dependency 'io.freefair.gradle:lombok-plugin' to new version 6.6-rc1
- update of gradle-plugin dependency 'com.diffplug.spotless:spotless-plugin-gradle' to new version 6.12.0
- update of gradle-plugin dependency 'com.github.ben-manes.versions.gradle.plugin' to new version 0.44.0
- update of test dependency file-worker to new version 11.6
- moved enum class BaseMenuId from swing-base-components to this module
- replaced deprecated InputEvent constants with the appropriate values

Version 2.1
-------------

ADDED:

- new factory method in KeyStrokeExtensions for create a Keystroke over a string object


Version 2
-------------

ADDED:

- new factory methods for JMenu and JMenuItem with a JMenuItemInfo as argument
- new factory method for create SystemTray with TrayIcon and a PopupMenu
- new gradle plugin spotless for formatting source code

CHANGED:

- update to jdk version 11
- update of gradle to new version 7.5.1
- update of lombok dependency to new patch version 1.18.24
- update of gradle-plugin dependency 'org.ajoberstar.grgit:grgit-gradle' in version 5.0.0
- update of gradle-plugin dependency 'io.freefair.gradle:lombok-plugin' to new version 6.5.1
- update of gradle-plugin dependency 'com.github.ben-manes.versions.gradle.plugin' to new version 0.42.0
- update of jobj-core dependency to new version 7
- update of throwable dependency to new version 2.3
- update of test dependency file-worker to new version 11.3
- update of test dependency silly-io to new version 2.1
- update of test dependency junit-jupiter to new version 5.9.1

Version 1.4
-------------

ADDED:

- new class ParentMenuResolver that provides methods for resolve parent and root menus
- new factory method for create JMenu objects
- new factory method for create JMenuItem objects
- new factory methods with action listeners for create JMenuItem objects
- new factory methods with KeyStroke object for create JMenuItem objects
- new accelerator method for JMenuItem with KeyStroke object
- new JMenuItemInfo class created for build JMenuItem and JMenu objects from given fields
- new method for get recursive all menu elements from the given parent MenuElement object
- new method in class ParentMenuResolver for get the type of an MenuElement object

CHANGED:

- update gradle to new version 7.3.3
- update of com.github.ben-manes.versions.gradle.plugin to new version 0.41.0
- update of jobj-core dependency to new version 5.3
- update of model-data dependency to new version 1.12
- renamed package actions to action
- update of test dependency silly-io to new version 1.7

Version 1.3
-------------

ADDED:

- new method in enum class LookAndFeels that gets the current look and feel of the application
- new field WINDOWS_CLASSIC for the windows classic look and feel in enum class LookAndFeels
- new gradle-plugin dependency 'org.ajoberstar.grgit:grgit-gradle' in version 4.1.1
- new dependency model-api in version 1.10

CHANGED:

- update gradle to new version 7.3.1
- update of lombok dependency to new version 1.18.22
- update of gradle-lombok-plugin to new version 6.3.0
- update of throw-able dependency to new version 1.7
- update of jobj-core dependency to new version 5
- update of test dependency junit-jupiter to new version 5.8.2
- update of test dependency file-worker to new version 8.1

Version 1.2
-------------

ADDED:

- new method in enum class LookAndFeels that sets only the look and feel to the UIManager
- new method in LookAndFeels class setLookAndFeel with LookAndFeels and Window argument
- new field CROSSPLATFORM in enum class LookAndFeels

CHANGED:

- update gradle to new version 7.2
- update of gradle-lombok-plugin to new version 6.1.0
- update of test dependency silly-io to new version 1.6

Version 1.1
-------------

ADDED:

- new callback methods in action classes
- new factory class for create all components for the help window
- new extension methods in class MenuExtensions
- new dependency to javax.help:javahelp in version 2.0.05
- new dependency to io.github.astrapi69:jobj-core in version 3.9

Version 1
-------------

ADDED:

- new CHANGELOG.md file created


Notable links:
[keep a changelog](http://keepachangelog.com/en/1.0.0/) Don’t let your friends dump git logs into changelogs
