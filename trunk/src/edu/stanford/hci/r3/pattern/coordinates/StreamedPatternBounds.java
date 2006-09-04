package edu.stanford.hci.r3.pattern.coordinates;

import edu.stanford.hci.r3.units.PatternDots;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * <p>
 * Stores the bounds in physical (streaming) coordinates.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class StreamedPatternBounds {

	private PatternDots xOrigin;

	private PatternDots yOrigin;

	private PatternDots width;

	private PatternDots height;

	/**
	 * <p>
	 * This object deals with physical coordinates (the type that you get when you stream
	 * coordinates from the Nokia SU-1B). They are all huge numbers, but we store them in
	 * PatternDots objects.
	 * </p>
	 * <p>
	 * Although we can convert the PatternDots objects into other Units, it doesn't really make
	 * sense, as the dots are specified in the world of Anoto's gargantuan pattern space. For
	 * example, if you converted the xOrigin to inches, you would get a beast of a number.
	 * </p>
	 */
	public StreamedPatternBounds(PatternDots x, PatternDots y, PatternDots w, PatternDots h) {
		xOrigin = x;
		yOrigin = y;
		width = w;
		height = h;
	}
	
	public boolean contains(PatternLocation location) {
		
	}
}
