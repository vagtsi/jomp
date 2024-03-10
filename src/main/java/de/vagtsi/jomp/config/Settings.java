package de.vagtsi.jomp.config;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * User settings file overriding default config properties.
 * 
 * @author Jens Vagts
 */
public class Settings extends Configuration {
	
	public static void setString(String key, String value) {
		userSettings.setProperty(key, value);
		config.setProperty(key, value);
	}

	public static void setBoolean(String key, boolean value) {
		userSettings.setProperty(key, Boolean.toString(value));
		config.setProperty(key, Boolean.toString(value));
	}

	public static void setInt(String key, int value) {
		userSettings.setProperty(key, Integer.toString(value));
		config.setProperty(key, Integer.toString(value));
	}

	public static void setLong(String key, long value) {
		userSettings.setProperty(key, Long.toString(value));
		config.setProperty(key, Long.toString(value));
	}

	public static void setStringArray(String key, String... values) {
		String array = Arrays.stream(values)
				.collect(Collectors.joining(ARRAY_SEPARATOR));
		userSettings.setProperty(key, array);
		config.setProperty(key, array);
	}
	
	public static void saveSettings() {
		if (userSettings.isEmpty()) {
			log.debug("Skipping saving of empty user settings file");
			return;
		}
		File settingsFile = getSettingsFile();
		log.debug("Saving user settings with {} values to file [{}]", userSettings.size(), settingsFile.getAbsolutePath());
	
		try (FileOutputStream out = new FileOutputStream(settingsFile)) {
			userSettings.store(out, "jOMP user settings");
			log.info("Sucessful saved {} values to user settings file [{}]", userSettings.size(), settingsFile.getAbsolutePath());
		} catch (Exception e) {
			log.error("Failed to save user settings to file [{}]", settingsFile.getAbsolutePath());
		}		
		
	}
	
	// ~~~~~~ private ~~~~~~~~~~~~~~~~~~~
	
	protected Settings() {
		//utility class
	}
}
