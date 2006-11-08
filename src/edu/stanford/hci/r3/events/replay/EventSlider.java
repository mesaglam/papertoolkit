package edu.stanford.hci.r3.events.replay;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.BoundedRangeModel;

import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.MathUtils;
import edu.stanford.hci.r3.util.components.JRangeSlider;

public class EventSlider extends JRangeSlider {

	final BasicStroke centerStroke = new BasicStroke(2);

	/**
	 * @param model
	 * @param orientation
	 * @param direction
	 */
	public EventSlider(BoundedRangeModel model, int orientation, int direction) {
		super(model, orientation, direction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.util.components.JRangeSlider#customPaint(java.awt.Graphics2D, int,
	 *      int)
	 */
	protected void customPaint(Graphics2D g, int width, int height) {
		final int lo = toScreen(getLowValue());
		final int hi = toScreen(getHighValue());
		final int min = toScreen(getMinimum());
		final int max = toScreen(getMaximum());
		final int range = max - min;
		final int xCenter = MathUtils.rint((hi - lo) / 2.0 + lo - 1);

		// DebugUtils.println(fractionLow + " " + fractionCenter + " " + fractionHigh);
		DebugUtils.println("lo: " + lo + " hi: " + hi);
		DebugUtils.println("x: " + xCenter + " " + height);
		g.setColor(Color.DARK_GRAY);
		g.setStroke(centerStroke);
		g.drawLine(xCenter, 0, xCenter, height);
	}
}
