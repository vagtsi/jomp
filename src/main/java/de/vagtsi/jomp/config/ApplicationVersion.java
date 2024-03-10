package de.vagtsi.jomp.config;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple bean service providing the complete application version information.
 * 
 * @author Jens Vagts
 */
public class ApplicationVersion {
	private static Logger logger = LoggerFactory.getLogger(ApplicationVersion.class);
	private static final String VERSION_RESOURCE_NAME = "/app.version";

	private static String version;
	private static String buildNumber;
	private static String buildTimestamp;
	private static String buildId;

	public static String getVersion() {
		return version;
	}
	
	public static String getBuildNumber() {
		return buildNumber;
	}
	
	public static String getBuildTimestamp() {
		return buildTimestamp;
	}
	
	public static String getBuildId() {
		return buildId;
	}

	/** Version with time stamp added */  
	public static String getFullVersion() {
		return String.format("%s (%s)", version, buildTimestamp);
	}

	static {
		Properties properties = new Properties();
		try {
			properties.load(ApplicationVersion.class.getResourceAsStream(VERSION_RESOURCE_NAME));
			version = properties.getProperty("version");
			buildNumber = properties.getProperty("build.number");
			buildTimestamp = properties.getProperty("build.timestamp");
			buildId = properties.getProperty("build.id");
		} catch (IOException e) {
			logger.error("Failed to read application version from resource [{}]", VERSION_RESOURCE_NAME, e);
		}
	}
	
	private ApplicationVersion() {
		//utility class
	}

}
