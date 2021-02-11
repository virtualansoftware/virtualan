package io.virtualan.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class VirtualanClassLoader extends ClassLoader {

  private File isGeneratedFile(File file, String fileName) {
    if (file.isDirectory()) {
      for (File child : file.listFiles()) {
        File file1 = isGeneratedFile(child, fileName);
        if(file1 != null){
          return file1;
        }
      }
    } else {
      if (file.getAbsolutePath().replace(File.separator,".").endsWith(fileName+".class")) {
        return file;
      }
    }
    return  null;
  }

  public VirtualanClassLoader(ClassLoader parent) {
    super(parent);
  }

  public Class loadClass(String name) throws ClassNotFoundException {
    File file = isGeneratedFile(VirtualanConfiguration.getDestPath(), name);
    if (file != null) {
      try {
        URL myUrl = file.toURI().toURL();
        URLConnection connection = myUrl.openConnection();
        InputStream input = connection.getInputStream();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int data = input.read();

        while (data != -1) {
          buffer.write(data);
          data = input.read();
        }

        input.close();

        byte[] classData = buffer.toByteArray();

        return defineClass(name,
            classData, 0, classData.length);
      } catch (MalformedURLException e) {
        log.warn("MYClassloader : " + e.getMessage());
      } catch (IOException e) {
        log.warn("MYClassloader : " + e.getMessage());
      }
    } else {
      return super.loadClass(name);
    }
    return null;
  }
}