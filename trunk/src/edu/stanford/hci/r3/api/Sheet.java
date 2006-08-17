package edu.stanford.hci.r3.api;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.units.Units;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Represents one sheet of interactive/augmented paper. This sheet can be large (like a GIGAprint)
 * or it can be smaller, like an 8.5x11" print.
 * 
 * The Sheet can be rendered in many different contexts. It can be rendered to a PDF/PS file, or
 * printed to a Java2D printer. It can also be rendered to the screen for a quick preview. Each of
 * these options has different advantages/disadvantages. For example, when rendering to the screen
 * or Java2D, no dot pattern is drawn by default. This is because doing so would be inefficient.
 */
public class Sheet {

	/**
	 * The Height of this Augmented Sheet.
	 */
	private double heightInInches;

	/**
	 * The Width of this Augmented Sheet.
	 */
	private double widthInInches;

	/**
	 * A list of all the regions contained on this sheet.
	 */
	private List<Region> regions = new ArrayList<Region>();

	/**
	 * Defaults to US Letter.
	 */
	public Sheet() {
		this(8.5, 11.0);
	}

	/**
	 * The main constructor.
	 * 
	 * @param widthInches
	 * @param heightInches
	 */
	public Sheet(double widthInches, double heightInches) {
		widthInInches = widthInches;
		heightInInches = heightInches;
	}

	/**
	 * Choose your own units.
	 * 
	 * @param width
	 * @param height
	 */
	public Sheet(Units width, Units height) {
		this(width.getValueInInches(), height.getValueInInches());
	}
}
