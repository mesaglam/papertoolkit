package edu.stanford.hci.r3.pen.ink;

import java.util.Collection;

import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.util.MathUtils;

/**
 * <p>
 * Store ink strokes (multiple samples) in here.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkStroke {

	private int[] force = null;

	private double maxX = Double.MIN_VALUE;

	private double maxY = Double.MIN_VALUE;

	private double minX = Double.MAX_VALUE;

	private double minY = Double.MAX_VALUE;

	private int numSamples;

	/**
	 * How to interpret our units.
	 */
	private Units referenceUnit;

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
	 * Copies the samples into our own arrays. The reference unit enables us to interpret the
	 * samples correctly.
	 * 
	 * @param currentStrokeSamples
	 * @param reference
	 */
	public InkStroke(Collection<InkSample> currentStrokeSamples, Units reference) {
		referenceUnit = reference;
		numSamples = currentStrokeSamples.size();
		x = new double[numSamples];
		y = new double[numSamples];
		force = new int[numSamples];
		timestamp = new long[numSamples];

		int i = 0;
		for (InkSample s : currentStrokeSamples) {
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
	 * @param maxY
	 *            the maxY to set
	 */
	public void setMaxY(float maxY) {
		this.maxY = maxY;
	}

	/**
	 * @param minX
	 *            the minX to set
	 */
	public void setMinX(float minX) {
		this.minX = minX;
	}

	/**
	 * @param minY
	 *            the minY to set
	 */
	public void setMinY(float minY) {
		this.minY = minY;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "InkStroke: Bounds [" + minX + ", " + minY + "  -->  " + maxX + ", " + maxY + "]";
	}

}
