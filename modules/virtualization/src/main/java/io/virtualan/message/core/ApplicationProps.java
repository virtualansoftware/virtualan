package io.virtualan.message.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationProps {
	private  ApplicationProps (){
	}

	static Properties noOfResource = new Properties();
	static Properties resourceProperties = new Properties();
	static {
		try {
			InputStream steam = ApplicationProps.class.getClassLoader().getResourceAsStream("application.properties");
			resourceProperties.load(steam);
		} catch (IOException e) {
			log.warn("ApplicationProps static loader {}", e.getMessage());
		}
	}
	public static String getProperty(String key){
		return resourceProperties.getProperty(key);
	}
}
