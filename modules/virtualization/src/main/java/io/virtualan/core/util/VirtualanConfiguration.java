package io.virtualan.core.util;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * The type Application configuration.
 *
 * @author Elan Thangamani
 */
public class VirtualanConfiguration {
  private static Properties properties = new Properties();
  static {
    reload();
  }

  public static  void reload(){
    try {
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
          "virtualan.properties");
      if(stream == null) {
        stream = VirtualanConfiguration.class.getClassLoader().getResourceAsStream(
            "virtualan.properties");
      }
      properties.load(stream);
    } catch (Exception e) {

    }
  }
  /**
   * Gets properties.
   *
   * @return the properties
   */
  public static Map<String, String> getProperties() {
    return (Map)properties;
  }

  /**
   * Gets property.
   *
   * @param keyName the key name
   * @return the property
   */
  public static String getProperty(String keyName) {
    return properties.getProperty(keyName);
  }

  public  static  File getSrcPath() {
    return getPath("srcFolder", "/conf/source");
  }
  public  static File getDestPath() {
    return getPath("destFolder", "/conf/classes");
  }

  public  static  File getDependencyPath() {
    return getPath("dependencyFolder", "/openapi/virtualan/lib");
  }

  public  static File getYamlPath() {
    return getPath("yamlFolder", "/conf/yaml");
  }

  public static File getPath(String keyName, String defaultPath) {
    return properties != null && properties.getProperty(keyName) != null ? new File(properties.getProperty(keyName)) : new File(defaultPath);
  }
}
