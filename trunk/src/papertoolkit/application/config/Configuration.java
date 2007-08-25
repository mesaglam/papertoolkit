package papertoolkit.application.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import papertoolkit.PaperToolkit;
import papertoolkit.actions.remote.ActionReceiver;
import papertoolkit.render.RegionRenderer;
import papertoolkit.units.Pixels;

/**
 * <p>
 * This configuration scheme allows developers to customize the operation of the toolkit through config files.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Configuration extends Properties {

	/**
	 * Stores our configuration properties.
	 */
	private static final Configuration config = new Configuration();

	/**
	 * Returns a resource file on the file system...
	 * 
	 * There are no plans to make PaperToolkit available as a single JAR.
	 * 
	 * @param configFileKey
	 * @return
	 */
	public static File getConfigFile(String configFileKey) {
		return getResourceFile(configFileKey);
	}

	/**
	 * @param configFileKey
	 * @return
	 */
	public static Properties getPropertiesFromConfigFile(String configFileKey) {
		final Properties props = new Properties();
		try {
			final InputStream configFileStream = getResourceFile(configFileKey).toURI().toURL().openStream();
			props.loadFromXML(configFileStream);
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}

	/**
	 * @param propertyName
	 * @param configFileKey
	 * @return
	 */
	public static String getPropertyFromConfigFile(String propertyName, String configFileKey) {
		return getPropertiesFromConfigFile(configFileKey).getProperty(propertyName);
	}

	/**
	 * Resolves the configuration name to a String value. The value can correspond to a file name, path,
	 * numeric value, etc.
	 * 
	 * @param propertyName
	 *            a key to index the toolkit's configuration
	 * @return the value corresponding to the configName key
	 */
	private static String getPropertyValue(String propertyName) {
		final String property = config.getProperty(propertyName);
		return property;
	}

	/**
	 * @param configFileKey
	 * @return
	 */
	private static File getResourceFile(String configFileKey) {
		// this is a relative path to PaperToolkit
		return new File(PaperToolkit.getToolkitRootPath(), getPropertyValue(configFileKey));
	}

	/**
	 * Provide default locations for our xml config files and other resources.
	 */
	private Configuration() {
		// part of the resources in the JAR File (or export directory)
		// this maps abstract names to actual files on the file system.
		setProperty(Pixels.CONFIG_FILE_KEY, Pixels.CONFIG_FILE_VALUE);
		setProperty(RegionRenderer.CONFIG_FILE_KEY, RegionRenderer.CONFIG_FILE_VALUE);
		setProperty(ActionReceiver.CONFIG_FILE_KEY, ActionReceiver.CONFIG_FILE_VALUE);
		setProperty(PaperToolkit.CONFIG_PATTERN_PATH_KEY, PaperToolkit.CONFIG_PATTERN_PATH_VALUE);
		setProperty(PaperToolkit.CONFIG_FILE_KEY, PaperToolkit.CONFIG_FILE_VALUE);
	}
}
