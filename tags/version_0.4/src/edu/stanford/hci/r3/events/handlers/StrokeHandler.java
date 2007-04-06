package edu.stanford.hci.r3.events.handlers;

import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Handles single pen strokes...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class StrokeHandler extends EventHandler {

	/**
	 * Keeps the most recent event, so that when pen up happens, we can give it an event that does
	 * not have ZERO coordinates.
	 */
	private PenEvent lastEvent;

	/**
	 * 
	 */
	private boolean penDownHappened = false;

	private InkStroke stroke;

	public InkStroke getStroke() {
		return stroke;
	}
	
	/**
	 * @see edu.stanford.hci.r3.events.EventHandler#handleEvent(edu.stanford.hci.r3.events.PenEvent)
	 */
	public void handleEvent(PenEvent event) {
		if (event.isPenDown()) {
			stroke = new InkStroke();
			penDownHappened = true;
		} else if (event.isPenUp()) {
			// really, this should always be true
			if (penDownHappened) {
				strokeArrived(lastEvent);
				penDownHappened = false;
				return;
			}
		}
		
		if (event == null || event.getOriginalSample() == null) {
			DebugUtils.println("Event Object is NULL");
		} else {
			stroke.addSample(event.getOriginalSample());
			lastEvent = event;
		}

		// do not consume the event (event has a consumed property that we do not set here)
	}

	/**
	 * @param e
	 */
	public abstract void strokeArrived(PenEvent e);
	
	public String toString() {
		return "StrokeHandler";
	}
}
