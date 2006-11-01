package edu.stanford.hci.r3.pen.ink;

import java.util.Collection;
import java.util.List;

import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.MathUtils;

/**
 * <p>
 * Store ink strokes (multiple samples) in here.
 * </p>
 * <p>
 * We assume that the units in this stroke are consistent (one reference unit), and make sense to the client
 * class that ends up using this stroke object. For example, if the units is in PatternDots, and the values
 * are derived directly from the streaming PenListeners, we need to make sure that the stroke samples do not
 * jump from one page tile to another. In that case, we would assume that the streamed samples come from one
 * Anoto pattern tile, unless otherwise specified.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkStroke {

	/**
	 * Assume data represents pattern dots by default.
	 */
	private static final PatternDots DEFAULT_REFERENCE_UNIT = new PatternDots();

	/**
	 * We can use this to augment rendering by manipulating the transparency & thickness.
	 */
	private int[] force = null;

	/**
	 * The bounds of this ink stroke. This is the rightmost x coordinate of any sample in this stroke.
	 */
	private double maxX = Double.MIN_VALUE;

	/**
	 * 
	 */
	private double maxY = Double.MIN_VALUE;

	/**
	 * 
	 */
	private double minX = Double.MAX_VALUE;

	/**
	 * 
	 */
	private double minY = Double.MAX_VALUE;

	/**
	 * How many samples are stored in this stroke? This should be the same as x.length;
	 */
	private int numSamples;

	/**
	 * How to interpret our units.
	 */
	private Units referenceUnit;

	/**
	 * 
	 */
	private long[] timestamp = null;

	/**
	 * store it in any units you like, specified by referenceUnit.
	 */
	private double[] x = null;

	/**
	 * stored in the referenceUnit.
	 */
	private double[] y = null;

	/**
	 * Copies the samples into our own arrays. The reference unit enables us to interpret the samples
	 * correctly.
	 * 
	 * @param currentStrokeSamples
	 * @param reference
	 */
	public InkStroke(Collection<PenSample> currentStrokeSamples, Units reference) {
		referenceUnit = reference;
		numSamples = currentStrokeSamples.size();
		x = new double[numSamples];
		y = new double[numSamples];
		force = new int[numSamples];
		timestamp = new long[numSamples];

		int i = 0;
		for (PenSample s : currentStrokeSamples) {
			x[i] = s.x;
			y[i] = s.y;
			force[i] = s.force;
			timestamp[i] = s.timestamp;

			// update maxs and mins
			minX = Math.min(s.x, minX);
			minY = Math.min(s.y, minY);
			maxX = Math.max(s.x, maxX);
			maxY = Math.max(s.y, maxY);

			i++;
		}
	}

	public InkStroke(List<PenSample> currentStroke) {
		this(currentStroke, DEFAULT_REFERENCE_UNIT);
	}

	/**
	 * @return the earliest time stamp of this stroke
	 */
	public long getFirstTimestamp() {
		return timestamp[0];
	}

	/**
	 * @return
	 */
	public int[] getForceSamples() {
		return force;
	}

	/**
	 * @return
	 */
	public long getLastTimestamp() {
		return timestamp[numSamples - 1];
	}

	/**
	 * @return the maxX
	 */
	public double getMaxX() {
		return maxX;
	}

	/**
	 * @return the maxY
	 */
	public double getMaxY() {
		return maxY;
	}

	/**
	 * @return the minX
	 */
	public double getMinX() {
		return minX;
	}

	/**
	 * @return the minY
	 */
	public double getMinY() {
		return minY;
	}

	/**
	 * @return
	 */
	public int getNumSamples() {
		return numSamples;
	}

	/**
	 * @return
	 */
	public Units getReferenceUnit() {
		return referenceUnit;
	}

	/**
	 * @return
	 */
	public long[] getTimeSamples() {
		return timestamp;
	}

	/**
	 * @return
	 */
	public double[] getXSamples() {
		return x;
	}

	/**
	 * @return
	 */
	public int[] getXSamplesAsInts() {
		int[] xInts = new int[numSamples];
		for (int i = 0; i < numSamples; i++) {
			xInts[i] = MathUtils.rint(x[i]);
		}
		return xInts;
	}

	/**
	 * @return
	 */
	public double[] getYSamples() {
		return y;
	}

	/**
	 * @return
	 */
	public int[] getYSamplesAsInts() {
		int[] yInts = new int[numSamples];
		for (int i = 0; i < numSamples; i++) {
			yInts[i] = MathUtils.rint(y[i]);
		}
		return yInts;
	}

	/**
	 * @param minStrokeX
	 * @param minStrokeY
	 * @param maxStrokeX
	 * @param maxStrokeY
	 */
	public void setBounds(double minStrokeX, double minStrokeY, double maxStrokeX, double maxStrokeY) {
		minX = minStrokeX;
		minY = minStrokeY;
		maxX = maxStrokeX;
		maxY = maxStrokeY;
	}

	/**
	 * @param mxX
	 *            the maxX to set
	 */
	public void setMaxX(float mxX) {
		maxX = mxX;
	}

	/**
	 * @param mxY
	 *            the maxY to set
	 */
	public void setMaxY(float mxY) {
		maxY = mxY;
	}

	/**
	 * @param mnX
	 *            the minX to set
	 */
	public void setMinX(float mnX) {
		minX = mnX;
	}

	/**
	 * @param mnY
	 *            the minY to set
	 */
	public void setMinY(float mnY) {
		minY = mnY;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "InkStroke: Bounds [" + minX + ", " + minY + "  -->  " + maxX + ", " + maxY + "] NumSamples: "
				+ numSamples;
	}

}
