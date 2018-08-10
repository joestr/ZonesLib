# ZonesLib
This plugin allows to control and manage zones. The API contains many methods and features for other plugins.
The Plugins also contains some events to control where zones are and to identify these. There are also events when players entering or leaving zones.

**Warning**: This Plugin has no own commands or features for the user, its grants access to the own API for other Plugins

### Features
* Four different Zone Types: Cuboid, Cylinder, Sphere and Polygon
* Integrated ability to force zones to full height
* Different Events to identify zones and check if players entering or leaving zones
* Ability to display Zones over Particles without client mods (thanks to DarkBlade12)
* Extremly fast detection of Zones at positions

### Dependencies
* [DatabaseHandler](https://github.com/DerTod2/DatabaseHandler) -- `for accessing the databases`

### Installation
Download the latest release [here](https://github.com/DerTod2/ZonesLib/releases/latest) or compile the source with maven

Simply put the **ZonesLib.jar** inside the **plugins** folder. The plugins doesn't need any configuration (for normal use).

### Updates

The plugin includes an updater to fetch the newest version automatically. Every Server start and each 12 hours it checks for new versions. In the ``config.yml`` are two configuration variables to enable/disable auto-checking for updates and automatic downloads of updates. When auto-download is enabled the server owner only needs to restart or reload the server.

### Included Command

The Plugin contains the command ``zl`` or ``zoneslib``. This command allows to reload the plugin and search for updates.

The Permissions are:

**/zl reload** -- ``zoneslib.commands.zoneslib.reload``

**/zl update** -- ``zoneslib.commands.zoneslib.update``

### Use in own Plugins
To use this plugin for own projects simply add the maven repository and dependencies

Repository:
```xml
  <repositories>
    <repository>
      <id>dertod2-repo</id>
      <url>http://nexus.dertod2.net/content/repositories/snapshots/</url>
    </repository>
  </repositories>
```

Dependency:
```xml
  <dependencies>
    <dependency>
    	<groupId>net.dertod2</groupId>
    	<artifactId>ZonesLib</artifactId>
    	<version>0.1.1-SNAPSHOT</version>
    	<scope>provided</scope>
    </dependency>
  </dependencies>
```
### JavaDocs
They can be found [here](http://javadocs.dertod2.net/ZonesLib/)
