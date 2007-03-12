package edu.stanford.hci.r3.pen.ink;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Integrate all the nice features that we can use to analyze ink into this class.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class InkUtils {

	public static List<InkStroke> findAllStrokesContainedWithin(List<Ink> inkWell,
			InkStroke container) {

		// calculate the bounds of the container stroke
		Rectangle2D bounds = container.getBounds();

		// return any stroke that falls completely within these bounds
		// do not include the original container!
		List<InkStroke> matchingStrokes = new ArrayList<InkStroke>();
		for (Ink ink : inkWell) {
			List<InkStroke> strokes = ink.getStrokes();
			for (InkStroke stroke : strokes) {
				if (bounds.contains(stroke.getBounds()) && !container.equals(stroke)) {
					// the stroke is completely inside the containing stroke
					matchingStrokes.add(stroke);
				}
			}
		}
		return matchingStrokes;
	}

	/**
	 * @param inkWell
	 * @return
	 */
	public static InkStroke findStrokeWithLargestArea(List<Ink> inkWell) {
		InkStroke biggestStroke = null;
		double maxArea = Double.MIN_VALUE;

		for (Ink ink : inkWell) {
			List<InkStroke> strokes = ink.getStrokes();
			for (InkStroke stroke : strokes) {

				double area = stroke.getArea();
				if (area > maxArea) {
					biggestStroke = stroke;
					maxArea = area;
				}
			}
		}

		DebugUtils.println(biggestStroke);
		return biggestStroke;
	}

}
