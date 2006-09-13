package edu.stanford.hci.r3.events.handlers;

import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;

/**
 * <p>
 * Triggers different code depending on which area of the region we are writing on. May be useful
 * for things like writing in the margin of a notebook.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class LocationHandler implements EventHandler {

	/**
	 * @see edu.stanford.hci.r3.events.EventHandler#handleEvent(edu.stanford.hci.r3.events.PenEvent)
	 */
	public void handleEvent(PenEvent event) {
		PercentageCoordinates percentageLocation = event.getPercentageLocation();

		// TODO: Do something...
	}
}
