package edu.stanford.hci.r3.events.handlers;

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
	 * @see edu.stanford.hci.r3.events.handlers.EventHandler#handleEvent(edu.stanford.hci.r3.events.PenEvent)
	 */
	public boolean handleEvent(PenEvent event) {
		
		
		return false; // do not consume the event
	}
}
