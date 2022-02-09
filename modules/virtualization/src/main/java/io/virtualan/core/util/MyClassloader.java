package io.virtualan.core.util;

public class MyClassloader extends java.net.URLClassLoader {

    public MyClassloader(java.net.URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addURL(java.net.URL url) {
        super.addURL(url);
    }
}