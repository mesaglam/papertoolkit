package edu.stanford.hci.r3.pen.ink;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.util.files.FileUtils;

/**
 * <p>
 * On its surface, this is just a <code>List&lt;InkStroke&gt;</code>... However, this class will
 * provide nice functions for clustering strokes, selecting strokes, etc.
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
	 * Black but with some transparency.
	 */
	private Color color = new Color(0, 0, 0, 220);

	/**
	 * The name of this Ink cluster.
	 */
	private String name;

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

	/**
	 * @param s
	 */
	public void addStroke(InkStroke s) {
		strokes.add(s);
	}

	/**
	 * @param useSeparatorLines
	 * @return
	 */
	public String getAsXML(boolean useSeparatorLines) {
		final String separator = useSeparatorLines ? "\n" : "";

		StringBuilder sb = new StringBuilder();
		sb.append("<ink>" + separator);
		for (InkStroke s : strokes) {
			sb.append("<stroke begin=\"" + s.getFirstTimestamp() + "\" end=\""
					+ s.getLastTimestamp() + "\">" + separator);
			double[] x = s.getXSamples();
			double[] y = s.getYSamples();
			int[] f = s.getForceSamples();
			long[] ts = s.getTimeSamples();
			for (int i = 0; i < x.length; i++) {
				sb.append("<p x=\"" + x[i] + "\" y=\"" + y[i] + "\" f=\"" + f[i] + "\" t=\""
						+ ts[i] + "\"/>");
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
	 * Use this for anything you like. It may help in debugging, or uniquely identifying ink
	 * clusters.
	 * 
	 * @param theName
	 */
	public void setName(String theName) {
		name = theName;
	}
}
