/**
 * 
 */
package edu.stanford.hci.r3.units;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

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
	 * Used for writing out the XML file.
	 */
	private static final String COMMENT = "Defines the Monitor Resolution to Physical Size Relationship";

	private static final String CONFIG_FILE = "config/PixelsPerInch.xml";

	private static final double DEFAULT_PIXELS_PER_INCH = readPixelsPerInchFromConfigFile();

	/**
	 * The Identity Element representing one Pixel on a "default" screen at a default pixelsPerInch.
	 */
	public static final Units ONE = new Pixels(1) {
		public void setPixelsPerInch(double ppi) {
			System.err.println("Warning: You cannot modify Pixels.ONE");
		}
	};

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
			props.loadFromXML(new FileInputStream(CONFIG_FILE));
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
		setPixelsPerInch(pixPerInch);
	}

	/**
	 * @see edu.stanford.hci.r3.units.Units#getNumberOfUnitsInOneInch()
	 */
	@Override
	protected double getNumberOfUnitsInOneInch() {
		return pixelsPerInch;
	}

	/**
	 * @param ppi
	 */
	public void setPixelsPerInch(double ppi) {
		pixelsPerInch = ppi;
	}

}
