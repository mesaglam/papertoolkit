package edu.stanford.hci.r3.paper;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.units.Inches;
import edu.stanford.hci.r3.units.Size;
import edu.stanford.hci.r3.units.Units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * <p>
 * Represents one sheet of interactive/augmented paper. This sheet can be large (like a GIGAprint)
 * or it can be smaller, like an 8.5x11" print.
 * </p>
 * <p>
 * The Sheet can be rendered in many different contexts. It can be rendered to a PDF/PS file, or
 * printed to a Java2D printer. It can also be rendered to the screen for a quick preview. Each of
 * these options has different advantages/disadvantages. For example, when rendering to the screen
 * or Java2D, no dot pattern is drawn by default. This is because doing so would be inefficient.
 * </p>
 * <p>
 * The dimensions of the sheet is kept in Units objects, and is only normalized (usually to Points)
 * when necessary (i.e., when rendering a PDF).
 * </p>
 * <p>
 * On Coordinate Systems: To maintain the analogue with GUI development, we will choose the origin
 * (0,0) to be the top-left corner of the document (I can hear the screams of PDF/Postscript
 * enthusiasts already--I'm sorry). We will make it easy to flip the coordinate systems to a more
 * Postscript-friendly way later. Possibly, we'll have a call like setCoordinateSystem(GUI |
 * POSTSCRIPT). Happy?
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class Sheet {

	private static final String INDENT = "   ";

	/**
	 * A list of all the regions contained on this sheet. This is the master list. We may keep other
	 * lists for convenience or efficiency.
	 */
	private List<Region> regions = new ArrayList<Region>();

	/**
	 * Represents the rectangular size of this Sheet.
	 */
	private Size size = new Size();

	/**
	 * Defaults to US Letter.
	 */
	public Sheet() {
		this(new Inches(8.5), new Inches(11.0));
	}

	/**
	 * A convenience method for our American friends. =)
	 * 
	 * @param widthInches
	 * @param heightInches
	 */
	public Sheet(double widthInches, double heightInches) {
		this(new Inches(widthInches), new Inches(heightInches));
	}

	/**
	 * Choose your own units.
	 * 
	 * @param width
	 * @param height
	 */
	public Sheet(Units w, Units h) {
		setSize(w, h);
	}

	/**
	 * @param r
	 */
	public void addRegion(Region r) {
		regions.add(r);
	}

	/**
	 * @return
	 */
	public Units getHeight() {
		return size.getHeight();
	}

	/**
	 * @return the internal list of regions
	 */
	public List<Region> getRegions() {
		return regions;
	}

	/**
	 * @return a copy of the Size object (so you can't modify the size of this sheet)
	 */
	public Size getSize() {
		return size.clone();
	}

	/**
	 * @return
	 */
	public Units getWidth() {
		return size.getWidth();
	}

	/**
	 * @param width
	 * @param height
	 */
	protected void setSize(Units width, Units height) {
		size.setSize(width, height);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// the indent
		String i = INDENT;

		sb.append("Sheet {\n");
		sb.append(i + "Size: " + getWidth() + " x " + getHeight() + "\n");

		sb.append(i + "Regions: " + regions.size() + " {\n");

		// indent twice
		i = INDENT + INDENT;

		for (Region r : regions) {
			sb.append(i + r.toString() + "\n");
		}

		i = INDENT;

		sb.append(i + "}\n");
		sb.append("}\n");
		return sb.toString();
	}

	/**
	 * @return
	 */
	public String toXML() {
		return PaperToolkit.toXML(this);
	}
}
