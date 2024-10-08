## Change log
----------------------

Version 4.2-SNAPSHOT
-------------


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
