package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyHandler {
	static Properties properties = new Properties();

	public static String getProperty(String name) {
		if (properties.isEmpty()) {
			try {
				properties.load(new FileInputStream("src/main/resources/fetcher.properties"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties.getProperty(name);
	}

}
