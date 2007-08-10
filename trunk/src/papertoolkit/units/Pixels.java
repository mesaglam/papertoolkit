/**
 * 
 */
package papertoolkit.units;

import papertoolkit.config.Configuration;
import papertoolkit.units.conversion.PixelsPerInch;

/**
 * <p>
 * Represents a screen based unit. This depends on the Pixels Per Inch of the screen, of course.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Pixels extends Units {

	/**
	 * The Key to access the configuration file for Pixels.
	 */
	public static final String CONFIG_FILE_KEY = "pixels.pixelsperinch.file";

	/**
	 * The location of the config file is PaperToolkit/data/config/PixelsPerInch.xml. The default is
	 * approximately 95 (my current monitor's physical ppi). That is, an inch on my monitor is about 95
	 * pixels.
	 */
	public static final String CONFIG_FILE_VALUE = "/config/PixelsPerInch.xml";

	/**
	 * The config file specifies our default pixels per inch on this system. You can customize that file if
	 * you want to map it to your screen dpi, for example.
	 */
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
	 * The interpretation of distance varies depending on the specifications of your monitor. We store the
	 * value in an XML file that you can customize.
	 * 
	 * @return
	 */
	private static double readPixelsPerInchFromConfigFile() {
		final String property = Configuration.getPropertyFromConfigFile(PROPERTY_NAME, CONFIG_FILE_KEY);
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
	public Pixels(double numPixels, PixelsPerInch pixPerInch) {
		super(numPixels);
		pixelsPerInch = pixPerInch.getValue();
	}

	/**
	 * @see papertoolkit.units.Units#getNumberOfUnitsInOneInch()
	 */
	protected double getNumberOfUnitsInOneInch() {
		return pixelsPerInch;
	}

	/**
	 * @return how many pixels per inch shall we use to interpret this value?
	 * 
	 */
	public double getPixelsPerInch() {
		return pixelsPerInch;
	}
}
