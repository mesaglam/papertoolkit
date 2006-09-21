package edu.stanford.hci.r3.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import edu.stanford.hci.r3.actions.remote.ActionReceiver;
import edu.stanford.hci.r3.pattern.TiledPatternGenerator;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.units.Pixels;

/**
 * <p>
 * The design of this configuration scheme was informed by Jeff Heer's prefuse code. It is
 * incomplete, but when properly implemented, it will allow a user of this toolkit to retrieve
 * resources from the JAR, and customize the operation of the toolkit with one or more config files.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Configuration extends Properties {

	private static final Configuration config = new Configuration();

	/**
	 * Resolves the configuration name to a String value. The value can correspond to a file name,
	 * path, numeric value, etc.
	 * 
	 * @param propertyName
	 *            a key to index the toolkit's configuration
	 * @return the value corresponding to the configName key
	 */
	public static String get(String propertyName) {
		final String property = config.getProperty(propertyName);
		// System.out.println("Configuration.java: Retrieved Property " + propertyName + " --> "
		// + property);
		return property;
	}

	/**
	 * TODO: Works for now, but won't work once we package it in a JAR.
	 * 
	 * @param propertyName
	 * @return
	 */
	public static File getConfigFile(String propertyName) {
		try {
			// System.out.println("Configuration.java: Property Name: " + propertyName);
			final URL resource = Configuration.class.getResource(get(propertyName));
			// System.out.println("Configuration.java: URL: " + resource);
			final URI uri = resource.toURI();
			final File file = new File(uri);
			return file;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param propertyName
	 * @return
	 * @throws IOException
	 */
	private static InputStream getConfigFileStream(String propertyName) throws IOException {
		final String resourceName = get(propertyName);
		// System.out.println("Configuration.java: Resource is " + resourceName);
		final URL resource = Configuration.class.getResource(resourceName);
		return resource.openStream();
	}

	/**
	 * @param propertyName
	 * @param configFileKey
	 * @return
	 */
	public static String getPropertyFromConfigFile(String propertyName, String configFileKey) {
		final Properties props = new Properties();
		try {
			// System.out.println("Configuration.java: Config File Key is " + propertyName + " --> "
			// + configFileKey);
			final InputStream configFileStream = Configuration.getConfigFileStream(configFileKey);

			// Just for debugging...
			// BufferedReader br = new BufferedReader(new InputStreamReader(configFileStream));
			// String line;
			// while ((line = br.readLine()) != null) {
			// System.out.println(line);
			// }

			props.loadFromXML(configFileStream);
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props.getProperty(propertyName);
	}

	/**
	 * 
	 */
	private Configuration() {
		setDefaultConfig();
	}

	/**
	 * Provide default locations for our xml config files and other resources.
	 */
	private void setDefaultConfig() {
		// part of the resources in the JAR File (or export directory)
		setProperty(Pixels.CONFIG_FILE_KEY, Pixels.CONFIG_FILE_VALUE);
		setProperty(RegionRenderer.CONFIG_FILE_KEY, RegionRenderer.CONFIG_FILE_VALUE);
		setProperty(TiledPatternGenerator.CONFIG_PATH_KEY, TiledPatternGenerator.CONFIG_PATH_VALUE);
		setProperty(ActionReceiver.CONFIG_FILE_KEY, ActionReceiver.CONFIG_FILE_VALUE);
	}

}
