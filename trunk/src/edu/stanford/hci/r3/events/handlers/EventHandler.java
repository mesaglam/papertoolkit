package edu.stanford.hci.r3.events.handlers;

import edu.stanford.hci.r3.events.PenEvent;

/**
 * <p>
 * This is the super interface of all the other event handlers. These are the pen & paper analogues
 * to Java Swing's EventListener architecture.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public interface EventHandler {

	/**
	 * if this event should be consumed (i.e., lower priority event handlers should not see this
	 * event), we should set the event.consumed property to true
	 */
	public void handleEvent(PenEvent event);

	/**
	 * @return the Event Handler's Name
	 */
	public String toString();
}
