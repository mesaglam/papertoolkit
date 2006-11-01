package edu.stanford.hci.r3.pen.streaming;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

import edu.stanford.hci.r3.units.PatternDots;

/**
 * <p>
 * Stores a pen sample. Soon, this class will be folded into InkSample.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @author Joel Brandt
 */
public class PenSample implements Serializable {

	/**
	 * at the start of each stroke, there is a time that represents the stroke's beginning
	 */
	private static long anchorTime = 0L;

	/**
	 * 
	 * 
	 */
	private static final DecimalFormat FORMATTER = PatternDots.FORMATTER;

	/**
	 * measure of force from pen tip
	 */
	public int force;

	/**
	 * whether pen is up
	 */
	public boolean penUp;

	/**
	 * timestamp of server when received
	 */
	public long timestamp;

	/**
	 * x location of the point, in physical Anoto coordinates
	 */
	public double x;

	/**
	 * y location of the point, in physical Anoto coordinates
	 */
	public double y;

	/**
	 * @param timestamp
	 * @param x
	 * @param y
	 * @param force
	 */
	public PenSample(long timestamp, double x, double y, int force) {
		this(timestamp, x, y, force, false /* pen is down */);
	}

	/**
	 * @param theTimestamp
	 * @param theX
	 * @param theY
	 * @param theForce
	 *            hehehe.
	 * @param isPenUp
	 */
	public PenSample(long theTimestamp, double theX, double theY, int theForce, boolean isPenUp) {
		timestamp = theTimestamp;
		x = theX;
		y = theY;
		force = theForce;
		penUp = isPenUp;
	}

	/**
	 * @return
	 */
	public int getForce() {
		return force;
	}

	/**
	 * @return
	 */
	public Date getTime() {
		return new Date(timestamp);
	}

	/**
	 * @return
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @return
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return
	 */
	public boolean isPenUp() {
		return penUp;
	}

	/**
	 * @param ts
	 * 
	 * Convert from a string in the Anoto XML format to the correct time stamp
	 * @deprecated because this was used for parsing the Nokia pen request files. Also, the "anchor time"
	 *             should probably be stored in the stroke, and not in the sample.
	 */
	public void setAnotoAnchorTime(String ts) {
		if (ts.startsWith("+")) {
			anchorTime += Long.parseLong(ts.substring(1, ts.length()));
		} else if (ts.contains(" ")) {
			// of the format: 1118214908803 (20050608 071508)
			String time = ts.substring(0, ts.indexOf(" "));
			anchorTime = Long.parseLong(time);
		} else {
			// of the format: 1118214908803
			anchorTime = Long.parseLong(ts);
		}
		timestamp = anchorTime;
	}

	/**
	 * @param f
	 */
	public void setForce(int f) {
		force = f;
	}

	/**
	 * Does this sample represent a Pen Up? Perhaps we should subclass PenSample, instead of storing another
	 * field in here? It seems a little wasteful if most samples are not pen up samples. =\
	 * 
	 * @param b
	 */
	public void setPenUp(boolean b) {
		penUp = b;
	}

	/**
	 * @param t
	 */
	public void setTimestamp(long t) {
		timestamp = t;
	}

	/**
	 * @param xVal
	 */
	public void setX(double xVal) {
		x = xVal;
	}

	/**
	 * @param yVal
	 */
	public void setY(double yVal) {
		y = yVal;
	}

	/**
	 * @return
	 */
	public String toCommaSeparatedString() {
		final DecimalFormat df = new DecimalFormat("#.####");
		final String xString = df.format(x);
		final String yString = df.format(y);
		return "" + timestamp + "," + xString + "," + yString + "," + force + ","
				+ (isPenUp() ? "UP" : "DOWN");
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Sample: [" + FORMATTER.format(x) + ", " + FORMATTER.format(y) + "] F=" + force + " T="
				+ timestamp + " P=" + (isPenUp() ? "UP" : "DOWN");
	}
}
