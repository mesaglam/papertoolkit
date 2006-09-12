package edu.stanford.hci.r3.events.filters;

import edu.stanford.hci.r3.events.EventFilter;
import edu.stanford.hci.r3.events.PenEvent;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkContainer extends EventFilter {

	int numStrokes = 0;

	@Override
	public void filterEvent(PenEvent event) {
		// collect the ink strokes
		if (event.isPenDown()) {
		} else if (event.isPenUp()) {
			numStrokes++;
			System.out.println(numStrokes + " strokes in this Ink Container.");
		} else { // regular stroke
		}
	}

	@Override
	public String toString() {
		return "Ink Container";
	}
}
