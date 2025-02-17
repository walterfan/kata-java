# MicroProfile Config
Configuration data can change dynamically and applications need to be able to access the latest configuration information without restarting the server.

MicroProfile Config provides portable externalization of configuration data. This means, you can configure applications and microservices to run in multiple environments without modification or repackaging.


## MicroProfile Config sources 

MicroProfile Config configuration properties can come from different locations and can be in different formats. 

These properties are provided by ConfigSources. ConfigSources are implementations of the org.eclipse.microprofile.config.spi.ConfigSource interface.

The MicroProfile Config specification provides the following default ConfigSource implementations for retrieving configuration values:

* System.getProperties().
* System.getenv().
* All META-INF/microprofile-config.properties files on the class path.
* Files in a directory
* ConfigSource class
* ConfigSourceProvider class

### Additional Resources

org.eclipse.microprofile.config.spi.ConfigSource