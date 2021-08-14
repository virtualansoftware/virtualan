package io.virtualan.core.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.StandardCopyOption;

public class Helper {
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

    public static void addURLToClassLoader(URL url, ClassLoader classLoader) throws IntrospectionException {
        URLClassLoader systemClassLoader = null;
        if(classLoader instanceof  VirtualanClassLoader) {
            systemClassLoader
                    = new URLClassLoader(new URL[]{url}, classLoader);
        } else  {
            systemClassLoader = (URLClassLoader) classLoader;
        }

        Class<URLClassLoader> classLoaderClass = URLClassLoader.class;
        try {
            Method method = classLoaderClass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(systemClassLoader, new Object[]{url});
        } catch (Throwable t) {
            throw new IntrospectionException("Error when adding url to system ClassLoader ");
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
}
