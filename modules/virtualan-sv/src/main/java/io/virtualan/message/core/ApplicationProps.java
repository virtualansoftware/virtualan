package io.virtualan.message.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProps {
	static Properties noOfResource = new Properties();
	static Properties resourceProperties = new Properties();
	static {
		try {
			InputStream steam = ApplicationProps.class.getClassLoader().getResourceAsStream("application.properties");
			resourceProperties.load(steam);
		} catch (IOException e) {
		}
	}
	public static String getProperty(String key){
		return resourceProperties.getProperty(key);
	}
}
