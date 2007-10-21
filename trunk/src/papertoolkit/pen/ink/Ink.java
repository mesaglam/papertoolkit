package papertoolkit.pen.ink;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import papertoolkit.pattern.coordinates.PageAddress;
import papertoolkit.pen.PenSample;
import papertoolkit.render.ink.InkRenderer;
import papertoolkit.util.DebugUtils;
import papertoolkit.util.files.FileUtils;

/**
 * <p>
 * On its surface, this is just a <code>List&lt;InkStroke&gt;</code>... However, this class will provide
 * nice functions for clustering strokes, selecting strokes, etc.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Ink {

	/**
	 * <p>
	 * Helps us determine where we got this ink from. It is not a required field, but is set when it is
	 * convenient.
	 * </p>
	 */
	public enum InkSource {
		BATCHED, STREAMED, UNKNOWN
	}

	/**
	 * Mostly Black, but with some transparency.
	 */
	private static final Color DEFAULT_DARK_INK_COLOR = new Color(0, 0, 0, 230);

	/**
	 * Default to the dark ink.
	 */
	private Color color = DEFAULT_DARK_INK_COLOR;

	/**
	 * Most recent timestamp in this Ink cluster.
	 */
	private long maxTS = Long.MIN_VALUE;

	/**
	 * The bounds of this ink collection. This is the rightmost x coordinate of any sample in this collection
	 * of strokes.
	 */
	private double maxX = Double.MIN_VALUE;

	/**
	 * 
	 */
	private double maxY = Double.MIN_VALUE;

	/**
	 * Earliest Timestamp in this Ink cluster.
	 */
	private long minTS = Long.MAX_VALUE;

	/**
	 * 
	 */
	private double minX = Double.MAX_VALUE;

	/**
	 * 
	 */
	private double minY = Double.MAX_VALUE;

	/**
	 * The name of this Ink cluster.
	 */
	private String name = "Ink";

	/**
	 * For ink that has sourceType set to BATCHED, this field is meaningful. It tells us which logical page
	 * this ink came from. For STREAMED ink, this field will be left NULL.
	 */
	private PageAddress pageAddress;

	/**
	 * The source of this Ink object.
	 */
	private InkSource sourceType = InkSource.UNKNOWN;

	/**
	 * An Ink object is essentially a list of InkStroke objects.
	 */
	private List<InkStroke> strokes;

	/**
	 * New ink object w/ an empty array of strokes.
	 */
	public Ink() {
		this(new ArrayList<InkStroke>());
	}

	/**
	 * Create an ink object from a serialized XML file.
	 * 
	 * @param xmlFile
	 */
	public Ink(File xmlFile) {
		this(); // empty list of strokes
		loadFromXMLFile(xmlFile);
	}

	/**
	 * @param theStrokes
	 */
	public Ink(List<InkStroke> theStrokes) {
		strokes = theStrokes;

		for (InkStroke s : theStrokes) {
			updateMinAndMax(s);
		}
	}

	/**
	 * @param s
	 *            the stroke to be added to the internal list.
	 */
	public void addStroke(InkStroke s) {
		strokes.add(s);
		updateMinAndMax(s);
	}

	/**
	 * @param pageInk
	 */
	public void append(Ink pageInk) {
		for (InkStroke stroke : pageInk.getStrokes()) {
			addStroke(stroke);
		}
	}

	/**
	 * @return
	 */
	public Color getColor() {
		return color;
	}

	public long getFirstTimestamp() {
		return minTS;
	}

	/**
	 * @return
	 */
	public String getInnerXML() {
		return getInnerXML(false);
	}

	/**
	 * @return the xml without the outer <ink></ink> tags.
	 */
	public String getInnerXML(boolean useSeparatorLines) {
		final String separator = useSeparatorLines ? "\n" : "";
		StringBuilder sb = new StringBuilder();
		for (InkStroke s : strokes) {
			sb.append("<stroke begin=\"" + s.getFirstTimestamp() + "\" end=\"" + s.getLastTimestamp() + "\">"
					+ separator);
			double[] x = s.getXSamples();
			double[] y = s.getYSamples();
			int[] f = s.getForceSamples();
			long[] ts = s.getTimeSamples();
			for (int i = 0; i < x.length; i++) {
				sb.append("<p x=\"" + x[i] + "\" y=\"" + y[i] + //
						"\" f=\"" + f[i] + "\" t=\"" + ts[i] + "\"/>");
			}
			sb.append("</stroke>" + separator);
		}
		return sb.toString();
	}

	public long getLastTimestamp() {
		return maxTS;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	/**
	 * @return
	 */
	public double getMinX() {
		return minX;
	}

	/**
	 * @return
	 */
	public double getMinY() {
		return minY;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the number of strokes in this ink object.
	 */
	public int getNumStrokes() {
		return strokes.size();
	}

	/**
	 * @return
	 */
	private String getOpenTagXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<ink");
		if (pageAddress != null) {
			sb.append(" address=\"" + pageAddress.toString() + "\"");
		}
		if (minTS != Long.MAX_VALUE) {
			sb.append(" begin=\"" + minTS + "\" end=\"" + maxTS + "\"");
		}
		sb.append(">");
		return sb.toString();
	}

	/**
	 * @return a new Renderer for this Ink object.
	 */
	public InkRenderer getRenderer() {
		return new InkRenderer(this);
	}

	/**
	 * @return the page address that generated this ink.
	 */
	public PageAddress getSourcePageAddress() {
		return pageAddress;
	}

	/**
	 * @return
	 */
	public InkSource getSourceType() {
		return sourceType;
	}

	/**
	 * @return the strokes that this Ink object comprises.
	 */
	public List<InkStroke> getStrokes() {
		return strokes;
	}

	/**
	 * Load strokes and other information from an xml file. It will clear this object before the load occurs,
	 * effectively replacing this Ink object with the one represented by the XML file.
	 * 
	 * @param xmlFileSource
	 */
	public void loadFromXMLFile(File xmlFileSource) {
		new InkXMLParser(this).parse(xmlFileSource);
	}

	/**
	 * @param s
	 */
	public void removeStroke(InkStroke s) {
		strokes.remove(s);
	}

	/**
	 * Drops it in the desktop directory...
	 * 
	 * @param w
	 * @param h
	 */
	public File renderToJPEGFile() {
		File homeDir = FileSystemView.getFileSystemView().getHomeDirectory();
		File destFile = new File(homeDir, getName() + ".jpg");
		renderToJPEGFile(destFile);
		return destFile;
	}

	/**
	 * @param dest
	 */
	public void renderToJPEGFile(File dest) {
		new InkRenderer(this).renderToJPEGRecentered(dest);
	}

	/**
	 * Start over...
	 */
	public void resetColor() {
		setColor(DEFAULT_DARK_INK_COLOR);
	}

	/**
	 * Save this object out as an XML file.
	 * 
	 * @param xmlFileDest
	 */
	public void saveToXMLFile(File xmlFileDest) {
		FileUtils.writeStringToFile(toXMLString(true), xmlFileDest);
	}

	/**
	 * @param c
	 */
	public void setColor(Color c) {
		color = c;
	}

	/**
	 * Use this for anything you like. It may help in debugging, or uniquely identifying ink clusters.
	 * 
	 * @param theName
	 */
	public void setName(String theName) {
		name = theName;
	}

	/**
	 * Set the Anoto page address that we got this Ink object from. When we do this, it is implied that our
	 * sourceType is BATCHED.
	 * 
	 * @param address
	 */
	public void setSourcePageAddress(PageAddress address) {
		setSourceType(InkSource.BATCHED);
		pageAddress = address;
	}

	/**
	 * @param pageAddrString
	 */
	public void setSourcePageAddress(String pageAddrString) {
		setSourcePageAddress(new PageAddress(pageAddrString));
	}

	/**
	 * @param src
	 */
	public void setSourceType(InkSource src) {
		sourceType = src;
	}

	/**
	 * @return return an XML representation of this Ink object.
	 */
	public String toXMLString() {
		return toXMLString(true);
	}

	/**
	 * Represents this Ink object as an XML string.
	 * 
	 * @param useSeparatorLines
	 * @return
	 */
	public String toXMLString(boolean useSeparatorLines) {
		final String separator = useSeparatorLines ? "\n" : "";

		StringBuilder sb = new StringBuilder();
		sb.append(getOpenTagXML() + separator);
		sb.append(getInnerXML(useSeparatorLines) + separator);
		sb.append("</ink>");
		return sb.toString();
	}

	/**
	 * @param s
	 */
	private void updateMinAndMax(InkStroke s) {
		if (s == null) {
			DebugUtils.println("Warning: Detected a NULL Ink stroke.");
			return;
		}

		// update maxs and mins
		minX = Math.min(s.getMinX(), minX);
		minY = Math.min(s.getMinY(), minY);
		maxX = Math.max(s.getMaxX(), maxX);
		maxY = Math.max(s.getMaxY(), maxY);
		minTS = Math.min(s.getFirstTimestamp(), minTS);
		maxTS = Math.max(s.getLastTimestamp(), maxTS);
	}

	/**
	 * Not an efficient way to do it....
	 * 
	 * @return
	 */
	public Point2D getMeanXandY() {
		double meanX = 0;
		double meanY = 0;
		double n = 0;

		for (InkStroke s : strokes) {
			List<PenSample> samples = s.getSamples();
			for (PenSample ps : samples) {
				n++;
				meanX = (meanX * (n - 1) / n) + (ps.x / n);
				meanY = (meanY * (n - 1) / n) + (ps.y / n);
			}
		}
		return new Point2D.Double(meanX, meanY);
	}

	public Ink getRecentered() {
		// a copy of the ink, recentered so that the top left is 0,0
		Ink recentered = new Ink();
		for (InkStroke s : strokes) {
			recentered.addStroke(s.getRecentered(minX, minY));
		}
		return recentered;
	}
}
