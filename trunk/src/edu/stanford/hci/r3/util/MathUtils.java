package edu.stanford.hci.r3.util;

/**
 * <p>
 * Math
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class MathUtils {

	
	/**
	 * @param vals
	 * @return
	 */
	public static double standardDeviation(double... vals) {
		if (vals == null || vals.length == 0) {
			return 0;
		}

		final double mean = average(vals);
		double variance = 0;
		for (double val : vals) {
			variance += Math.pow((val - mean), 2);
		}
		return Math.sqrt(variance / vals.length);
	}
	
	/**
	 * If you think your values will overflow this operation, then roll your own
	 * 
	 * @param vals
	 * @return
	 */
	public static double average(double... vals) {
		if (vals == null || vals.length == 0) {
			return 0;
		}
		return sum(vals) / vals.length;
	}

	/**
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	/**
	 * @param val
	 * @return
	 */
	public static int rint(double val) {
		return (int) Math.round(val);
	}

	/**
	 * @param vals
	 * @return
	 */
	private static double sum(double... vals) {
		double sum = 0;
		for (double val : vals) {
			sum += val;
		}
		return sum;
	}
}
