/**
 * 
 */
package edu.stanford.hci.r3.units;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import edu.stanford.hci.r3.config.Configuration;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Represents a screen based unit. This depends on the Pixels Per Inch of the screen, of course.
 */
public class Pixels extends Units {

	/**
	 * The Key to access the configuration file for Pixels.
	 */
	public static final String CONFIG_FILE_KEY = "pixels.pixelsperinch.file";

	private static final double DEFAULT_PIXELS_PER_INCH = readPixelsPerInchFromConfigFile();

	/**
	 * The Identity Element representing one Pixel on a "default" screen at a default pixelsPerInch.
	 */
	public static final Pixels ONE = new Pixels(1);

	/**
	 * The key as stored in the xml file.
	 */
	private static final String PROPERTY_NAME = "pixelsPerInch";

	/**
	 * The interpretation of distance varies depending on the specifications of your monitor. We
	 * store the value in an XML file that you can customize.
	 * 
	 * @return
	 */
	private static double readPixelsPerInchFromConfigFile() {
		final Properties props = new Properties();
		try {
			props.loadFromXML(Configuration.getConfigFileStream(CONFIG_FILE_KEY));
		} catch (InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		final String property = props.getProperty(PROPERTY_NAME);
		final double ppi = Double.parseDouble(property);
		return ppi;
	}

	/**
	 * How many pixels (in this environment) equals one physical inch?
	 */
	private double pixelsPerInch = DEFAULT_PIXELS_PER_INCH;

	/**
	 * One little square on your monitor, containing a color. =)
	 */
	public Pixels() {
		super(1);
	}

	/**
	 * @param numPixels
	 */
	public Pixels(double numPixels) {
		super(numPixels);
	}

	/**
	 * @param numPixels
	 */
	public Pixels(double numPixels, double pixPerInch) {
		super(numPixels);
		pixelsPerInch = pixPerInch;
	}

	/**
	 * @see edu.stanford.hci.r3.units.Units#getNumberOfUnitsInOneInch()
	 */
	@Override
	protected double getNumberOfUnitsInOneInch() {
		return pixelsPerInch;
	}

	/**
	 * @return
	 * 
	 */
	public double getPixelsPerInch() {
		return pixelsPerInch;
	}
}
