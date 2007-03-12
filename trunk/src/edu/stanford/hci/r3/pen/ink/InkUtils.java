package edu.stanford.hci.r3.pen.ink;

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
