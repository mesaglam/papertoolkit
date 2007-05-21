package edu.stanford.hci.r3.events.handlers;

import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;

/**
 * <p>
 * This event handler can detect the starting and ending locations of a drag operation (pen down, pen move,
 * pen up)... Drags are single-stroke operations. You can specify what happens due to this drag, and can
 * access the source and destination regions... Ideally, you can drag across non-patterned paper, as long as
 * the source and dest are both patterened regions.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class DragHandler extends EventHandler {

	private double maxXVal;
	private double maxYVal;
	private double minXVal;
	private double minYVal;

	protected abstract void handleDragMinMax(double minX, double minY, double maxX, double maxY);

	@Override
	public void handleEvent(PenEvent event) {
		// collect the min and max

		PercentageCoordinates pctLocation = event.getPercentageLocation();
		double pctX = pctLocation.getPercentageInXDirection();
		double pctY = pctLocation.getPercentageInYDirection();

		if (event.isTypePenDown()) {
			minXVal = Double.MAX_VALUE;
			minYVal = Double.MAX_VALUE;
			maxXVal = Double.MIN_VALUE;
			maxYVal = Double.MIN_VALUE;
		} else if (event.isTypePenUp()) {
			// call the min and max handler
			handleDragMinMax(minXVal, minYVal, maxXVal, maxYVal);
		} else {
			if (pctX < minXVal) {
				minXVal = pctX;
			}
			if (pctY < minYVal) {
				minYVal = pctY;
			}
			if (pctX > maxXVal) {
				maxXVal = pctX;
			}
			if (pctY > maxYVal) {
				maxYVal = pctY;
			}
		}
	}

	@Override
	public String toString() {
		return "Drag Handler";
	}
}
