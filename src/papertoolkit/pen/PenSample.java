package papertoolkit.pen;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.org.apache.xerces.internal.util.XML11Char;

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

	public static final String DOWN = "DOWN";

	/**
	 * To prettify the output string.
	 */
	private static final DecimalFormat FORMATTER = PatternDots.FORMATTER;

	private static final String SAMPLE_XML_FORMAT = "<p x=\"(.*?)\" y=\"(.*?)\" f=\"(.*?)\" t=\"(.*?)\" p=\"(.*?)\".*?/>";

	private static final Pattern SAMPLE_XML_FORMAT_PATTERN = Pattern.compile(SAMPLE_XML_FORMAT);

	public static final String UP = "UP";

	/**
	 * @param xmlString
	 * @return
	 */
	public static PenSample fromXMLString(String xmlString) {
		// of the form
		// <p x="10" y="20" f="123" t="108098098" p="UP"/>
		final Matcher matcherSample = SAMPLE_XML_FORMAT_PATTERN.matcher(xmlString);
		if (matcherSample.find()) {
			final String x = matcherSample.group(1);
			final String y = matcherSample.group(2);
			final String f = matcherSample.group(3);
			final String t = matcherSample.group(4);
			final String penUpOrDown = matcherSample.group(5);
			return new PenSample(Double.parseDouble(x), Double.parseDouble(y), Integer.parseInt(f), Long
					.parseLong(t), penUpOrDown.equals(UP));
		} else {
			return null;
		}
	}

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
	 * to serialize/unserialize, use toXMLString instead...
	 * 
	 * @return a readable, network-friendly, comma-separated string
	 */
	public String toCommaSeparatedString() {
		final DecimalFormat df = new DecimalFormat("#.####");
		final String xString = df.format(x);
		final String yString = df.format(y);
		return "" + timestamp + "," + xString + "," + yString + "," + force + "," + (isPenUp() ? UP : DOWN);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Sample: [" + FORMATTER.format(x) + ", " + FORMATTER.format(y) + "] F=" + force + " T="
				+ timestamp + " P=" + (isPenUp() ? UP : DOWN);
	}

	/**
	 * So far, this is used when communicating with Flash in real time.
	 * 
	 * @return
	 */
	public String toXMLString() {
		return "<p x=\"" + x + "\" y=\"" + y + "\" f=\"" + force + "\" t=\"" + timestamp + "\" p=\""
				+ (isPenUp() ? UP : DOWN) + "\"/>";
	}
}
