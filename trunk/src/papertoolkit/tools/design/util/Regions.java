package papertoolkit.tools.design.util;

/**
 * 
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author Marcello
 *
 */
public class Regions {

	/**
	 * Returns the scale necessary to make width/height fit inside 
	 * maxWidth/maxHeight.
	 * @param width
	 * @param height
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static double makeItFit(double width, double height, 
			double maxWidth, double maxHeight) {
		double xprop, yprop;
		if ((xprop = width / maxWidth) < (yprop = height / maxHeight))
			return 1/yprop;
		return 1/xprop;
	}

}
