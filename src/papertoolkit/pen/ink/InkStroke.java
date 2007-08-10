package papertoolkit.pen.ink;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import papertoolkit.pen.PenSample;
import papertoolkit.units.PatternDots;
import papertoolkit.units.Units;
import papertoolkit.util.MathUtils;


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
	 * How to interpret our units.
	 */
	private Units referenceUnit;

	/**
	 * store it in any units you like, specified by referenceUnit.
	 */
	private List<PenSample> samples = new ArrayList<PenSample>();

	/**
	 * The stroke width. It determines how wide the ink stroke will look when rendered.
	 */
	private double strokeWidth = 1.2;

	public InkStroke() {
		referenceUnit = DEFAULT_REFERENCE_UNIT;
	}

	/**
	 * Copies the samples into our own arrays. The reference unit enables us to interpret the samples
	 * correctly.
	 * 
	 * @param currentStrokeSamples
	 * @param reference
	 */
	public InkStroke(Collection<PenSample> currentStrokeSamples, Units reference) {
		referenceUnit = reference;
		for (PenSample s : currentStrokeSamples) {
			addSample(s.x, s.y, s.force, s.timestamp);
		}
	}

	/**
	 * @param stroke
	 */
	public InkStroke(List<PenSample> stroke) {
		this(stroke, DEFAULT_REFERENCE_UNIT);
	}

	/**
	 * @param x
	 * @param y
	 * @param force
	 * @param ts
	 */
	public void addSample(double x, double y, int force, long ts) {
		samples.add(new PenSample(x, y, force, ts));

		// update maxs and mins
		minX = Math.min(x, minX);
		minY = Math.min(y, minY);
		maxX = Math.max(x, maxX);
		maxY = Math.max(y, maxY);
	}

	/**
	 * @param penSample
	 */
	public void addSample(PenSample penSample) {
		samples.add(penSample);

		// update maxs and mins
		minX = Math.min(penSample.x, minX);
		minY = Math.min(penSample.y, minY);
		maxX = Math.max(penSample.x, maxX);
		maxY = Math.max(penSample.y, maxY);
	}

	/**
	 * @return the area in pixels^2, or whatever units the ink is in (possible PatternDots^2)
	 */
	public double getArea() {
		return (maxX - minX) * (maxY - minY);
	}

	/**
	 * @return
	 */
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * @return
	 */
	public long getDuration() {
		return getLastTimestamp() - getFirstTimestamp();
	}

	/**
	 * Returns the last sample in this stroke (end-point).
	 * 
	 * @return
	 */
	public PenSample getEnd() {
		return samples.get(samples.size() - 1);
	}

	/**
	 * @return the earliest time stamp of this stroke
	 */
	public long getFirstTimestamp() {
		return samples.get(0).timestamp;
	}

	public Date getFirstTimestampAsDate() {
		return new Date(samples.get(0).timestamp);
	}

	/**
	 * @return
	 */
	public int[] getForceSamples() {
		int numSamples = getNumSamples();
		int[] f = new int[numSamples];
		for (int i = 0; i < numSamples; i++) {
			f[i] = samples.get(i).force;
		}
		return f;
	}

	/**
	 * @return
	 */
	public long getLastTimestamp() {
		return samples.get(samples.size() - 1).timestamp;
	}

	/**
	 * @return
	 */
	public Date getLastTimestampAsDate() {
		return new Date(samples.get(samples.size() - 1).timestamp);
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
		return samples.size();
	}

	/**
	 * @return
	 */
	public Units getReferenceUnit() {
		return referenceUnit;
	}

	public List<PenSample> getSamples() {
		return samples;
	}

	/**
	 * Returns the first sample in this stroke (start-point).
	 * 
	 * @return
	 */
	public PenSample getStart() {
		return samples.get(0);
	}

	/**
	 * @return
	 */
	public long[] getTimeSamples() {
		int numSamples = getNumSamples();
		long[] t = new long[numSamples];
		for (int i = 0; i < numSamples; i++) {
			t[i] = samples.get(i).timestamp;
		}
		return t;
	}

	public double getWidth() {
		return strokeWidth;
	}

	/**
	 * @return
	 */
	public double[] getXSamples() {
		int numSamples = getNumSamples();
		double[] x = new double[numSamples];
		for (int i = 0; i < numSamples; i++) {
			x[i] = samples.get(i).x;
		}
		return x;
	}

	/**
	 * @return
	 */
	public int[] getXSamplesAsInts() {
		int numSamples = getNumSamples();
		int[] xInts = new int[numSamples];
		for (int i = 0; i < numSamples; i++) {
			xInts[i] = MathUtils.rint(samples.get(i).x);
		}
		return xInts;
	}

	/**
	 * @return
	 */
	public double[] getYSamples() {
		int numSamples = getNumSamples();
		double[] y = new double[numSamples];
		for (int i = 0; i < numSamples; i++) {
			y[i] = samples.get(i).y;
		}
		return y;
	}

	/**
	 * @return
	 */
	public int[] getYSamplesAsInts() {
		int numSamples = getNumSamples();
		int[] yInts = new int[numSamples];
		for (int i = 0; i < numSamples; i++) {
			yInts[i] = MathUtils.rint(samples.get(i).y);
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

	public void setSamples(List<PenSample> newSamples) {
		samples = newSamples;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "InkStroke: Bounds [" + minX + ", " + minY + "  -->  " + maxX + ", " + maxY + "] "
				+ getNumSamples() + " Samples with timestamps from [" + getFirstTimestampAsDate() + " to "
				+ getLastTimestampAsDate() + "]";
	}

}
