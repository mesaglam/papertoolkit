package edu.stanford.hci.r3.tools.design.util;

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
