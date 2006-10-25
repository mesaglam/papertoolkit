package edu.stanford.hci.r3.pen.ink;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

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
	 * 
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
