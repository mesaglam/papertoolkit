package edu.stanford.hci.r3.pen.ink;

import java.awt.Color;
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

	private Color color = Color.BLACK;

	/**
	 * 
	 */
	private List<InkStroke> strokes;

	/**
	 * @param theStrokes
	 */
	public Ink(List<InkStroke> theStrokes) {
		strokes = theStrokes;
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
	public List<InkStroke> getStrokes() {
		return strokes;
	}

	/**
	 * @return
	 */
	public int getNumStrokes() {
		return strokes.size();
	}

	/**
	 * @param c
	 */
	public void setColor(Color c) {
		color = c;
	}
}
