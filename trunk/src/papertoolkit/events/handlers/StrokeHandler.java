package papertoolkit.events.handlers;

import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.util.DebugUtils;

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
	 * Keeps the most recent event, so that when pen up happens, we can give it an event that does not have
	 * ZERO coordinates.
	 */
	private PenEvent lastEvent;

	/**
	 * 
	 */
	private boolean penDownHappened = false;

	/**
	 * 
	 */
	private InkStroke stroke;

	/**
	 * 
	 * @return
	 */
	public InkStroke getStroke() {
		return stroke;
	}

	/**
	 * @see papertoolkit.events.EventHandler#handleEvent(papertoolkit.events.PenEvent)
	 */
	public void handleEvent(PenEvent event) {
		if (event.isTypePenDown()) {
			stroke = new InkStroke();
			penDownHappened = true;
		} else if (event.isTypePenUp()) {
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

	/**
	 * 
	 */
	public String toString() {
		return "StrokeHandler";
	}
}
