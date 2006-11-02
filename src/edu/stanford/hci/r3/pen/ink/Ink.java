package edu.stanford.hci.r3.pen.ink;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.pattern.coordinates.PageAddress;
import edu.stanford.hci.r3.render.ink.InkRenderer;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.files.FileUtils;

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
	 * The name of this Ink cluster.
	 */
	private String name;

	/**
	 * For ink that has sourceType set to BATCHED, this field is meaningful. It tells us which logical page
	 * this ink came from. For STREAMED ink, this field will be left NULL.
	 */
	private PageAddress pageAddress;

	/**
	 * 
	 */
	private InkSource sourceType = InkSource.UNKNOWN;

	/**
	 * 
	 */
	private List<InkStroke> strokes;

	/**
	 * New ink object w/ an empty array of strokes.
	 */
	public Ink() {
		this(new ArrayList<InkStroke>());
	}

	/**
	 * @param theStrokes
	 */
	public Ink(List<InkStroke> theStrokes) {
		strokes = theStrokes;
	}

	public Ink(File xmlFile) {
		// ... load it from xml here...
		// TODO
		DebugUtils.println("Unimplemented");
	}

	/**
	 * @param s
	 */
	public void addStroke(InkStroke s) {
		strokes.add(s);
	}

	/**
	 * @return
	 */
	public String getAsXML() {
		return getAsXML(true);
	}

	/**
	 * Represents this Ink object as an XML string.
	 * 
	 * @param useSeparatorLines
	 * @return
	 */
	public String getAsXML(boolean useSeparatorLines) {
		final String separator = useSeparatorLines ? "\n" : "";

		StringBuilder sb = new StringBuilder();
		sb.append(getOpenTagXML() + separator);
		for (InkStroke s : strokes) {
			sb.append("<stroke begin=\"" + s.getFirstTimestamp() + "\" end=\"" + s.getLastTimestamp() + "\">"
					+ separator);
			double[] x = s.getXSamples();
			double[] y = s.getYSamples();
			int[] f = s.getForceSamples();
			long[] ts = s.getTimeSamples();
			for (int i = 0; i < x.length; i++) {
				sb
						.append("<p x=\"" + x[i] + "\" y=\"" + y[i] + "\" f=\"" + f[i] + "\" t=\"" + ts[i]
								+ "\"/>");
			}
			sb.append("</stroke>" + separator);
		}
		sb.append("</ink>");
		return sb.toString();
	}

	/**
	 * @return
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public int getNumStrokes() {
		return strokes.size();
	}

	private String getOpenTagXML() {
		if (pageAddress == null) {
			return "<ink>";
		} else {
			return "<ink address=\"" + pageAddress.toString() + "\">";
		}
	}

	/**
	 * @return a new Renderer for this Ink object.
	 */
	public InkRenderer getRenderer() {
		return new InkRenderer(this);
	}

	/**
	 * @return
	 */
	public PageAddress getSourcePageAddress() {
		return pageAddress;
	}

	public InkSource getSourceType() {
		return sourceType;
	}

	/**
	 * @return
	 */
	public List<InkStroke> getStrokes() {
		return strokes;
	}

	/**
	 * @param s
	 */
	public void removeStroke(InkStroke s) {
		strokes.remove(s);
	}

	/**
	 * Start over...
	 */
	public void resetColor() {
		setColor(DEFAULT_DARK_INK_COLOR);
	}

	/**
	 * @param xmlFile
	 */
	public void saveAsXMLFile(File xmlFile) {
		FileUtils.writeStringToFile(getAsXML(true), xmlFile);
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
	 * @param src
	 */
	public void setSourceType(InkSource src) {
		sourceType = src;
	}
}
