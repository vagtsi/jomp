package de.vagtsi.jomp.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central application configuration read our from properties file either from working directory or from classpath.
 */
public class Configuration {
	protected static Logger log = LoggerFactory.getLogger(Configuration.class);
	
	/** Default properties (resource) file name. */
	public static final String CONFIG_FILE_NAME = "config.properties";

	/** System property names for defining the settings path name and file name */
	public static final String SETTINGS_FILE_PROPERTY = "jomp.settings.file";
	public static final String SETTINGS_PATH_PROPERTY = "jomp.settings.path";

	/** Configurable settings file name */
	public static final String SETTINGS_FILE_NAME = System.getProperty(SETTINGS_FILE_PROPERTY, ".jomp-settings");
	
	/** Optional directory of optional config file besides the default file in the app resources. */
	private static final String CONFIG_FILE_DIRECTORY = System.getProperty("CONFIG_DIR", System.getProperty("user.dir"));
	
	/** string array separator char*/ 
	protected static final String ARRAY_SEPARATOR = ",";

	// ~~~~~ configuration values access ~~~~~~~~~~~~~
	
	public static String getString(String key) {
		return config.getProperty(key);
	}
	
	public static String getString(String key, String defaultValue) {
		return config.getProperty(key, defaultValue);
	}

	public static String[] getStringArray(String key) {
		String arrayValue = getString(key);
		if (arrayValue != null) {
			return arrayValue.split(ARRAY_SEPARATOR);
		} else {
			return null;
		}
	}
	
	public static long getLong(String key) {
		return Long.parseLong(config.getProperty(key));
	}

	public static long getLong(String key, long defaultValue) {
		String value = config.getProperty(key);
		if (value != null) {
			return Long.parseLong(value);
		} else {
			return defaultValue;
		}
	}

	public static int getInt(String key) {
		return Integer.parseInt(config.getProperty(key));
	}

	public static int getInt(String key, int defaultValue) {
		String value = config.getProperty(key);
		if (value != null) {
			return Integer.parseInt(value);
		} else {
			return defaultValue;
		}
	}

	public static boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	public static boolean getBoolean(String key, boolean defaultValue) {
		String value = config.getProperty(key);
		if (value != null) {
			return Boolean.parseBoolean(value);
		} else {
			return defaultValue;
		}
	}
	
	// ~~~~~ settings file configuration ~~~~~~ 
	public static File getSettingsFile() {
		String settingsPath = System.getProperty(SETTINGS_PATH_PROPERTY, System.getProperty("user.home"));
		return new File(settingsPath + File.separator + SETTINGS_FILE_NAME);
	}

	// ~~~~~~ private ~~~~~~~~~~~~~~~~~~~
	
	protected Configuration() {
		//utility class
	}
	
	protected static Properties userSettings = readSettingsFile();
	protected static Properties config = readConfig(CONFIG_FILE_NAME);

	private static Properties readConfig(String configFileName) {
		//cascading reading of properties (all optional, recent settings override previous ones!):
		// 1. config.properties from resource
		// 2. config.properties from current working directory
		// 3. .jomp-settings from user home directory
		
		Properties properties = new Properties();
		
		//read config properties from default resource file
		try {
			Properties configResourceProperties = new Properties();
			configResourceProperties.load(Configuration.class.getResourceAsStream("/" + CONFIG_FILE_NAME));
			properties.putAll(configResourceProperties);
			log.info("Sucessful read {} configuration properties from resource [{}]", configResourceProperties.size(), CONFIG_FILE_NAME);
		} catch (IOException e) {
			throw new IllegalStateException("Failed to read configuration from resource", e);
		}
		
		//check for existing file in given directory or current working directory first
		Properties fileProperties = readConfigFile(configFileName);
		if (fileProperties != null) {
			properties.putAll(fileProperties);
		}
		
		//read optional user setting file
		if (userSettings != null && !userSettings.isEmpty()) {
			properties.putAll(userSettings);
		}
		
		return properties;
	}

	private static Properties readSettingsFile() {
		File settingsFile = getSettingsFile();
		if (settingsFile.exists()) {
			return readPropertiesFromFile(settingsFile);
		} else {
			//initialize for enabling setting new values
			return new Properties();
		}
	}
	
	private static Properties readConfigFile(String configFileName) {
		//try to read from given config directory if defined, default is working directory
		File configFile = CONFIG_FILE_DIRECTORY != null
				? new File(CONFIG_FILE_DIRECTORY, configFileName)
				: new File(configFileName);
		if (configFile.exists()) {
			log.debug("Reading app configuration from file [{}]", configFile.getAbsolutePath());
			return readPropertiesFromFile(configFile);
		} else {
			log.debug("No app configuration file found at [{}]", configFile.getAbsolutePath());
		}
		return null;
	}

	protected static Properties readPropertiesFromFile(File file) {
		Properties properties = new Properties();
		try (FileInputStream fis = new FileInputStream(file)) {
			properties.load(fis);
			log.info("Sucessful read {} configuration properties from file [{}]", properties.size(), file.getAbsolutePath());
			return properties;
		} catch (Exception e) {
			log.error("Failed to read configuration from file [{}]", file.getAbsolutePath());
			return null;
		}
	}

}
