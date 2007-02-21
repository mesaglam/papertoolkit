package edu.stanford.hci.r3.paper.regions;

import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.events.handlers.ClickHandler;
import edu.stanford.hci.r3.paper.Region;

/**
 * <p>
 * A Shortcut for creating a region with a click handler.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class ButtonRegion extends Region {

	public ButtonRegion(String name, double x, double y, double w, double h) {
		super(name, x, y, w, h);
		addEventHandler(new ClickHandler() {
			@Override
			public void clicked(PenEvent e) {
				onClick(e);
			}

			@Override
			public void pressed(PenEvent e) {

			}

			@Override
			public void released(PenEvent e) {

			}
		});
	}

	/**
	 * Override this!
	 * 
	 * @param e
	 */
	protected void onClick(PenEvent e) {
		// DebugUtils.println("Clicked");
	}

	/**
	 * Override this!
	 * 
	 * @param e
	 */
	protected void onPress(PenEvent e) {
		// DebugUtils.println("Pressed");
	}

	/**
	 * Override this!
	 * 
	 * @param e
	 */
	protected void onRelease(PenEvent e) {
		// DebugUtils.println("Released");
	}

}
