package edu.stanford.hci.r3.events.handlers;

import edu.stanford.hci.r3.events.PenEvent;

/**
 * <p>
 * Makes code that needs a ClickHandler a little bit cleaner. Extend this instead of ClickHandler.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class ClickAdapter extends ClickHandler {

	@Override
	public void clicked(PenEvent e) {
	}

	@Override
	public void pressed(PenEvent e) {
	}

	@Override
	public void released(PenEvent e) {
	}
}
