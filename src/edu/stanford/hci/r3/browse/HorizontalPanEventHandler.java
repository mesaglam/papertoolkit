package edu.stanford.hci.r3.browse;

import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * <p>
 * A custom pan event handler...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 */
public class HorizontalPanEventHandler extends PDragSequenceEventHandler {

	private boolean autopan;
	private double minAutopanSpeed = 250;
	private double maxAutopanSpeed = 750;

	public HorizontalPanEventHandler() {
		super();
		setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
		setAutopan(true);
	}

	protected void drag(PInputEvent e) {
		super.drag(e);
		pan(e);
	}

	protected void pan(PInputEvent e) {
		PCamera c = e.getCamera();
		Point2D l = e.getPosition();

		if (c.getViewBounds().contains(l)) {
			PDimension d = e.getDelta();

			// c.translateView(d.getWidth(), d.getHeight());
			c.translateView(d.getWidth(), 0);
		}
	}

	// ****************************************************************
	// Auto Pan
	// ****************************************************************

	public void setAutopan(boolean autopan) {
		this.autopan = autopan;
	}

	public boolean getAutopan() {
		return autopan;
	}

	/**
	 * Set the minAutoPan speed in pixels per second.
	 * 
	 * @param minAutopanSpeed
	 */
	public void setMinAutopanSpeed(double minAutopanSpeed) {
		this.minAutopanSpeed = minAutopanSpeed;
	}

	/**
	 * Set the maxAutoPan speed in pixes per second.
	 * 
	 * @param maxAutopanSpeed
	 */
	public void setMaxAutopanSpeed(double maxAutopanSpeed) {
		this.maxAutopanSpeed = maxAutopanSpeed;
	}

	/**
	 * Do auto panning even when the mouse is not moving.
	 */
	protected void dragActivityStep(PInputEvent aEvent) {
		if (!autopan)
			return;

		PCamera c = aEvent.getCamera();
		PBounds b = c.getBoundsReference();
		Point2D l = aEvent.getPositionRelativeTo(c);
		int outcode = b.outcode(l);
		PDimension delta = new PDimension();

		if ((outcode & Rectangle.OUT_TOP) != 0) {
			delta.height = validatePanningSpeed(-1.0 - (0.5 * Math.abs(l.getY() - b.getY())));
		} else if ((outcode & Rectangle.OUT_BOTTOM) != 0) {
			delta.height = validatePanningSpeed(1.0 + (0.5 * Math.abs(l.getY()
					- (b.getY() + b.getHeight()))));
		}

		if ((outcode & Rectangle.OUT_RIGHT) != 0) {
			delta.width = validatePanningSpeed(1.0 + (0.5 * Math.abs(l.getX()
					- (b.getX() + b.getWidth()))));
		} else if ((outcode & Rectangle.OUT_LEFT) != 0) {
			delta.width = validatePanningSpeed(-1.0 - (0.5 * Math.abs(l.getX() - b.getX())));
		}

		c.localToView(delta);

		// only go for deltas in width...
		if (delta.width != 0) {
			c.translateView(delta.width, 0);
		}
		// if (delta.width != 0 || delta.height != 0) {
		// c.translateView(delta.width, delta.height);
		// }
	}

	protected double validatePanningSpeed(double delta) {
		double minDelta = minAutopanSpeed / (1000 / getDragActivity().getStepRate());
		double maxDelta = maxAutopanSpeed / (1000 / getDragActivity().getStepRate());

		boolean deltaNegative = delta < 0;
		delta = Math.abs(delta);
		if (delta < minDelta)
			delta = minDelta;
		if (delta > maxDelta)
			delta = maxDelta;
		if (deltaNegative)
			delta = -delta;
		return delta;
	}

	// ****************************************************************
	// Debugging - methods for debugging
	// ****************************************************************

	/**
	 * Returns a string representing the state of this node. This method is intended to be used only
	 * for debugging purposes, and the content and format of the returned string may vary between
	 * implementations. The returned string may be empty but may not be <code>null</code>.
	 * 
	 * @return a string representation of this node's state
	 */
	protected String paramString() {
		StringBuffer result = new StringBuffer();

		result.append("minAutopanSpeed=" + minAutopanSpeed);
		result.append(",maxAutopanSpeed=" + maxAutopanSpeed);
		if (autopan)
			result.append(",autopan");
		result.append(',');
		result.append(super.paramString());

		return result.toString();
	}
}
