package papertoolkit.events.handlers;

import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.units.coordinates.PercentageCoordinates;

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
public abstract class LocationHandler extends EventHandler {

	/**
	 * @see papertoolkit.events.EventHandler#handleEvent(papertoolkit.events.PenEvent)
	 */
	public void handleEvent(PenEvent event) {
		PercentageCoordinates percentageLocation = event.getPercentageLocation();

		// TODO: Do something...
	}
}
