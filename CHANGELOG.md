## Change log
----------------------

Version 1.4-SNAPSHOT
-------------

ADDED:

- new class ParentMenuResolver that provides methods for resolve parent and root menus
- new factory method for create JMenu objects
- new factory methods with action listeners for create JMenuItem objects

CHANGED:

- update gradle to new version 7.3.3
- update of com.github.ben-manes.versions.gradle.plugin to new version 0.41.0
- update of jobj-core dependency to new version 5.2

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

