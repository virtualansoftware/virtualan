package io.virtualan.core.util;

import io.virtualan.core.model.VirtualServiceRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

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

  private static  Map<String, Map<String, VirtualServiceRequest>> virtualServiceRequestMap = new HashMap<>();

  public static Map<String, Map<String, VirtualServiceRequest>> getVirtualServiceRequestMap() {
    return virtualServiceRequestMap;
  }

  public static  void setVirtualServiceRequestMap(
      Map<String, Map<String, VirtualServiceRequest>> virtualServiceRequestMap1) {
    virtualServiceRequestMap = virtualServiceRequestMap1;
  }

  public static boolean isValidJson(String jsonStr) {
    try {
      Object json = new JSONTokener(jsonStr).nextValue();
      if (json instanceof JSONObject || json instanceof JSONArray) {
        return true;
      } else {
        return false;
      }
    }catch (Exception e){
      return false;
    }
  }

  public static void writeYaml(String filename, InputStream in) throws IOException {
    File targetFile = new File(filename);
    InputStream initialStream = in;
    java.nio.file.Files.copy(
        initialStream,
        targetFile.toPath(),
        StandardCopyOption.REPLACE_EXISTING);

    initialStream.close();
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
