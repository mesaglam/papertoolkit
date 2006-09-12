package edu.stanford.hci.r3.events.handlers;

import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.events.PenEvent;

/**
 * <p>
 * Unlike Java Swing's MouseListener, the Pen & Paper Click Handler cannot sense mouseover.
 * Therefore, there is no analogue to mouseEntered, Exited.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class ClickHandler implements EventHandler {

	/**
	 * 
	 */
	private boolean penDownHappened = false;

	/**
	 * Use this variable to see if there was a double click, while handing a clicked() event.
	 */
	protected int clickCount = 1;

	/**
	 * If the current click time is really close to the last click time, we can signal a double
	 * click.
	 */
	private long lastClickTime = 0;

	/**
	 * 
	 */
	protected int maxMillisBetweenMultipleClicks = 300; // 300 ms for a double-click

	/**
	 * @param e
	 */
	public abstract void clicked(PenEvent e);

	/**
	 * @param e
	 */
	public abstract void pressed(PenEvent e);

	/**
	 * @param e
	 */
	public abstract void released(PenEvent e);

	/**
	 * This method does the hard work of figuring out when a pen pressed, released, and clicked. It
	 * is up to the subclass to do something interesting with once the events are triggered.
	 * 
	 * @see edu.stanford.hci.r3.events.EventHandler#handleEvent(edu.stanford.hci.r3.events.PenEvent)
	 */
	public void handleEvent(PenEvent event) {
		if (event.isPenDown()) {
			pressed(event);
			penDownHappened = true;
		} else if (event.isPenUp()) {
			released(event);

			// really, this should always be true
			if (penDownHappened) {
				if (event.getTimestamp() - lastClickTime <= maxMillisBetweenMultipleClicks) {
					clickCount++;
				} else {
					clickCount = 1; // reset the click count
				}
				clicked(event);
				lastClickTime = event.getTimestamp();
				penDownHappened = false;
			}
		}

		// do not consume the event (event has a consumed property that we do not set here)
	}
}
