package io.virtualan.core.util;

import io.virtualan.core.model.VirtualServiceRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * The type Application configuration.
 *
 * @author Elan Thangamani
 */
@Slf4j
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
    String profile = System.getenv("spring.profiles.active");
    profile = profile == null ? System.getenv("SPRING_PROFILES_ACTIVE") : profile ;
    String fileName = profile != null ? "virtualan-"+profile+".properties" : "virtualan.properties";
    try {
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
          fileName);
      if(stream == null) {
        stream = VirtualanConfiguration.class.getClassLoader().getResourceAsStream(
            fileName);
      }
      properties.load(stream);
    } catch (Exception e) {
       log.warn(fileName + " is missing");
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

  public  static  File getPath() {
    return getPath("default-path", "/conf");
  }
  public  static  File getSrcPath() {
    return getDefaultPath( "source");
  }
  public  static File getDestPath() {
    return getDefaultPath( "classes");
  }

  public static File getDefaultPath(String defaultPath) {
    File file = new File(getPath() + File.separator + defaultPath);
    if(!file.exists()){
      file.mkdirs();
    }
    return new File(getPath() + File.separator + defaultPath) ;
  }

  public  static  File getDependencyPath() {
    return getPath("dependencyFolder", "/openapi/virtualan/lib");
  }

  public  static File getYamlPath() {
    return getDefaultPath("yaml");
  }

  public static File getPath(String keyName, String defaultPath) {
    return properties != null && properties.getProperty(keyName) != null ? new File(properties.getProperty(keyName)) : new File(defaultPath);
  }
}
