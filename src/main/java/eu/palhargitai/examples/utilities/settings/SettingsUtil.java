package eu.palhargitai.examples.utilities.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Describes a utility that can read settings. It loads a 'default' file from
 * the classpath and provides some utility methods to access the settings.
 * 
 * 
 * @author Pal Hargitai
 */
public enum SettingsUtil {

	/** Default instance. */
	INSTANCE;

	/** A logger. */
	private static final Logger LOGGER = Logger.getLogger(SettingsUtil.class.getCanonicalName());
	/** Properties file location, contains actual settings. */
	private static String PROPERTIES_LOCATION = "/settings.properties";

	/** Properties containing the settings. */
	private volatile Properties settings = null;

	/**
	 * Updates the properties location.
	 * 
	 * @param propertiesLocation
	 *            The new properties location.
	 */
	protected void updatePropertiesLocation(String propertiesLocation) {
		SettingsUtil.PROPERTIES_LOCATION = propertiesLocation;
	}

	/**
	 * Gets a value from the settings, if the value is not set, return the
	 * defaultValue.
	 * 
	 * @param key
	 *            The key to return the settings for.
	 * @param defaultValue
	 *            The default value.
	 * @return The value associated with the key from the properties, or, if not
	 *         set, the defaultValue.
	 */
	private String getValueDelegate(String key, String defaultValue) {
		SettingsUtil.LOGGER.fine(String.format("Getting property '%s' with default value '%s'.", key, defaultValue));
		final Properties properties = this.getSettings();
		final String result;
		if (properties.containsKey(key)) {
			result = properties.getProperty(key);
		} else {
			result = defaultValue;
		}
		SettingsUtil.LOGGER.fine(String.format("Resolved property value '%s' for property '%s'.", result, key));
		return result;
	}

	/**
	 * Gets the settings, if the settings have not been retrieved before,
	 * retrieves them.
	 * 
	 * @return The settings.
	 */
	private Properties getSettings() {
		Properties result = this.settings;
		if (result == null) {
			synchronized (this) {
				result = this.settings;
				if (result == null) {
					this.settings = result = new Properties();
					SettingsUtil.initializeProperties(this.settings, SettingsUtil.PROPERTIES_LOCATION);
				}
			}
		}
		return result;
	}

	/** Resets the properties. Only for testing! */
	protected void resetProperties() {
		synchronized (this) {
			this.settings = null;
		}
	}

	/**
	 * Initializes the properties with the values from the given location.
	 * 
	 * @param properties
	 *            The properties.
	 */
	private static void initializeProperties(Properties properties, String location) {
		InputStream resourceStream = null;
		// First attempt to load the resource from the context classloader.
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if (contextClassLoader != null) {
			resourceStream = contextClassLoader.getResourceAsStream(location);
		}
		// If the first attempt failed, use the settings class to resolve the
		// resource.
		if (resourceStream == null) {
			resourceStream = SettingsUtil.class.getResourceAsStream(location);
		}
		// Load the resource
		try {
			if (resourceStream != null) {
				properties.load(resourceStream);
			}
		} catch (IOException e) {
			// We just log the exception.
			SettingsUtil.LOGGER.warning(String.format("Could not read from '%s' to properties.", location));
			SettingsUtil.LOGGER.log(Level.FINE, String.format("Exception occured while reading '%s'.", location), e);
		} finally {
			if (resourceStream != null) {
				try {
					resourceStream.close();
				} catch (IOException e) {
					SettingsUtil.LOGGER.warning(String.format("An exception occured while closing the stream for '%s'.", location));
					SettingsUtil.LOGGER.log(Level.FINE, String.format("Exception occured closing stream for '%s'.", location), e);
					// Don't rethrow the exception, as it may overwrite the
					// originating exception!
				}
			}
		}
	}

	/**
	 * Gets a value from the settings, if the value is not set, return the
	 * defaultValue.
	 * 
	 * @param key
	 *            The key to return the settings for.
	 * @param defaultValue
	 *            The default value.
	 * @return The value associated with the key from the properties, or, if not
	 *         set, the defaultValue.
	 */
	public static String getValue(String key, String defaultValue) {
		// Delegate to instance method.
		return SettingsUtil.INSTANCE.getValueDelegate(key, defaultValue);
	}
}
