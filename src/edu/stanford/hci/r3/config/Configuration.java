package edu.stanford.hci.r3.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

	private Configuration() {
		setDefaultConfig();
	}

	/**
	 * 
	 */
	private void setDefaultConfig() {
		// part of the resources in the JAR File (or export directory)
		setProperty(Pixels.CONFIG_FILE_KEY, "/config/PixelsPerInch.xml");
	}

	/**
	 * @param configNameOfResource
	 * @return
	 * @throws IOException
	 */
	public static InputStream getConfigFileStream(String configNameOfResource) throws IOException {
		return Configuration.class.getResource(get(configNameOfResource)).openStream();
	}

	/**
	 * Resolves the configuration name to a String value. The value can correspond to a file name,
	 * path, numeric value, etc.
	 * 
	 * @param configName
	 *           a key to indext the toolkit's configuration
	 * @return the value corresponding to the configName key
	 */
	public static String get(String configName) {
		return config.getProperty(configName);
	}

}
