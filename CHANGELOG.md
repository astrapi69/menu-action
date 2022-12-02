## Change log
----------------------

Version 2.4-SNAPSHOT
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
