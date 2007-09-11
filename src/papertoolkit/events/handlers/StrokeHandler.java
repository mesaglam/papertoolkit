package papertoolkit.events.handlers;

import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * Fires an event on every single pen stroke... If you have gesture recognizers that depend on single strokes,
 * you can subclass this.
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

	private boolean penDownHappened = false;

	/**
	 * Keep a single stroke around... throw away older strokes.
	 */
	private InkStroke mostRecentStroke;

	/**
	 * @return the latest stroke
	 */
	public InkStroke getStroke() {
		return mostRecentStroke;
	}

	/**
	 * @see papertoolkit.events.EventHandler#handleEvent(papertoolkit.events.PenEvent)
	 */
	public void handleEvent(PenEvent event) {
		if (event == null || event.getOriginalSample() == null) {
			DebugUtils.println("Event Object is NULL");
			return;
		}

		if (event.isTypePenDown()) {
			mostRecentStroke = new InkStroke();
			penDownHappened = true;
		} else if (event.isTypePenUp()) {
			// really, this should always be true
			if (penDownHappened) {
				strokeArrived(lastEvent, mostRecentStroke);
				penDownHappened = false;
				return;
			}
		} else {
			mostRecentStroke.addSample(event.getOriginalSample());
			lastEvent = event;
		}
		// do not consume the event (event has a consumed property that we do not set here)
	}

	/**
	 * This handler is called on every pen up.
	 * 
	 * @param lastSample
	 * @param stroke
	 */
	public abstract void strokeArrived(PenEvent lastSample, InkStroke stroke);

	public String toString() {
		return "StrokeHandler";
	}
}
