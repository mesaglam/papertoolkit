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

import edu.stanford.hci.r3.pattern.TiledPatternGenerator;
import edu.stanford.hci.r3.render.RegionRenderer;
import edu.stanford.hci.r3.units.Pixels;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * The design of this configuration scheme was informed by Jeff Heer's prefuse source code.
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
		return config.getProperty(propertyName);
	}

	/**
	 * @param propertyName
	 * @return
	 * @throws IOException
	 */
	private static InputStream getConfigFileStream(String propertyName) throws IOException {
		return Configuration.class.getResource(get(propertyName)).openStream();
	}

	/**
	 * TODO: Works for now, but won't work once we package it in a JAR.
	 * 
	 * @param propertyName
	 * @return
	 */
	public static File getConfigFile(String propertyName) {
		try {
			System.out.println("Property Name: " + propertyName);
			URL resource = Configuration.class.getResource(get(propertyName));
			System.out.println("URL: " + resource);
			URI uri = resource.toURI();
			System.out.println(uri);
			File file = new File(uri);
			System.out.println(file.getAbsolutePath());
			return file;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param propertyName
	 * @param configFileKey
	 * @return
	 */
	public static String getPropertyFromConfigFile(String propertyName, String configFileKey) {
		final Properties props = new Properties();
		try {
			props.loadFromXML(Configuration.getConfigFileStream(configFileKey));
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props.getProperty(propertyName);
	}

	private Configuration() {
		setDefaultConfig();
	}

	/**
	 * 
	 */
	private void setDefaultConfig() {
		// part of the resources in the JAR File (or export directory)
		setProperty(Pixels.CONFIG_FILE, "/config/PixelsPerInch.xml");
		setProperty(RegionRenderer.CONFIG_FILE, "/config/RegionRenderer.xml");
		setProperty(TiledPatternGenerator.CONFIG_PATH, "/pattern/");
	}

}
