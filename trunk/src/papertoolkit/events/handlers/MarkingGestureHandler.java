package papertoolkit.events.handlers;

import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.units.Units;
import papertoolkit.units.coordinates.PercentageCoordinates;

/**
 * <p>
 * Can handle simple marking gestures (e.g., eight compass directions).
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class MarkingGestureHandler extends EventHandler {

	/**
	 * <p>
	 * Handles drags in one of eight directions.
	 * </p>
	 */
	public static enum MarkDirection {
		E, // 0 radians (cone from -PI/8 to PI/8)
		N, // PI/2
		NE, // PI/4
		NW, // 3PI/4
		S, // -PI/2
		SE, // -PI/4
		SW, // -3PI/4
		W
		// +/- PI (cone from -7PI/8 ... to -PI & from 7PI/8 to PI)
	}

	private double dx;

	private double dy;

	private Units firstPercentageX;

	private Units firstPercentageY;

	/**
	 * How many samples constitute a gesture? The larger, the more strict we will be (i.e., longer gestures
	 * only).
	 */
	private int gestureThreshold = 4;

	private Units lastPercentageX;

	private Units lastPercentageY;

	private int numSamples;

	/**
	 * @see papertoolkit.events.EventHandler#handleEvent(papertoolkit.events.PenEvent)
	 */
	public void handleEvent(PenEvent event) {
		PercentageCoordinates percentageLocation = event.getPercentageLocation();
		if (event.isTypePenDown()) {
			firstPercentageX = percentageLocation.getActualValueInXDirection();
			firstPercentageY = percentageLocation.getActualValueInYDirection();
			numSamples = 0;
		} else if (event.isTypePenUp()) {
			// register a gesture on pen up
			// only if we get more than N samples do we register a gesture
			if (numSamples > gestureThreshold) {
				dx = lastPercentageX.getValue() - firstPercentageX.getValue();
				dy = lastPercentageY.getValue() - firstPercentageY.getValue();

				// calculate the angle, and decide the compass direction
				double theta = Math.atan2(dy, dx);

				// makes it easier to binary search
				// we negate it because our y actually grows going DOWN the page.
				// This is opposite from the standard cartesian coordinate system.
				double testTheta = -theta + Math.PI / 8;

				final double PI_4 = Math.PI / 4;
				final double PI_2 = Math.PI / 2;
				final double THREE_PI_4 = 3 * Math.PI / 4;

				// Do a ~binary search! =)
				if (testTheta > 0) { // E, NE, N, NW, W
					if (testTheta < PI_2) { // NE or E
						if (testTheta < PI_4) { // E
							handleMark(event, MarkDirection.E);
						} else { // NE
							handleMark(event, MarkDirection.NE);
						}
					} else { // N, NW, W... anything greater than PI/2
						if (testTheta < THREE_PI_4) { // N
							handleMark(event, MarkDirection.N);
						} else if (testTheta < Math.PI) { // NW
							handleMark(event, MarkDirection.NW);
						} else { // W
							handleMark(event, MarkDirection.W);
						}
					}
				} else { // SE, S, SW, W
					if (testTheta > -PI_2) { // S, SE
						if (testTheta > -PI_4) {
							handleMark(event, MarkDirection.SE);
						} else {
							handleMark(event, MarkDirection.S);
						}
					} else { // SW, W
						if (testTheta > -THREE_PI_4) {
							handleMark(event, MarkDirection.SW);
						} else {
							handleMark(event, MarkDirection.W);
						}
					}

				}

			}
		} else {
			numSamples++;
			lastPercentageX = percentageLocation.getActualValueInXDirection();
			lastPercentageY = percentageLocation.getActualValueInYDirection();
		}
	}

	/**
	 * @param e
	 * @param dir
	 */
	public abstract void handleMark(PenEvent e, MarkDirection dir);

	/* (non-Javadoc)
	 * @see papertoolkit.events.EventHandler#toString()
	 */
	public String toString() {
		return "MarkingGestureHandler";
	}
}
