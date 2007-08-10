package papertoolkit.pen;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

import papertoolkit.units.PatternDots;
import papertoolkit.util.MathUtils;


/**
 * <p>
 * Stores a pen sample or ink samples.
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
	 * To prettify the output string.
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
	 * x location of the point, in physical Anoto coordinates TODO: Make these private later...
	 */
	public double x;

	/**
	 * y location of the point, in physical Anoto coordinates
	 */
	public double y;

	/**
	 * @param xVal
	 * @param yVal
	 * @param f
	 * @param ts
	 */
	public PenSample(double xVal, double yVal, int f, long ts) {
		this(xVal, yVal, f, ts, false /* pen is down */);
	}

	/**
	 * @param theX
	 * @param theY
	 * @param theForce
	 *            hehehe.
	 * @param theTimestamp
	 * @param isPenUp
	 */
	public PenSample(double theX, double theY, int theForce, long theTimestamp, boolean isPenUp) {
		timestamp = theTimestamp;
		x = theX;
		y = theY;
		force = theForce;
		penUp = isPenUp;
	}

	public double getDistanceFrom(PenSample otherSample) {
		return MathUtils.distance(x, y, otherSample.x, otherSample.y);
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

	/**
	 * So far, this is used when communicating with Flash in real time.
	 * 
	 * @return
	 */
	public String toXMLString() {
		return "<p x=\"" + x + "\" y=\"" + y + "\" f=\"" + force + "\" t=\"" + timestamp + "\" p=\""
				+ (isPenUp() ? "U" : "D") + "\"/>";
	}
}
