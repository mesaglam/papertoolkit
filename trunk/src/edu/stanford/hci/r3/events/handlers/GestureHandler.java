package edu.stanford.hci.r3.events.handlers;

import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;

/**
 * <p>
 * Can handle simple marking gestures by default.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class GestureHandler implements EventHandler {

	/**
	 * <p>
	 * Handles drags in one of eight directions.
	 * </p>
	 */
	public static enum GestureDirection {
		E, // 0 radians (cone from -PI/8 to PI/8)
		N, // PI/2
		NE, // PI/4
		NW, // 3PI/4
		S, // -PI/2
		SE, // -PI/4
		SW, // -3PI/4
		W // +/- PI (cone from -7PI/8 ... to -PI and from 7PI/8 to PI)
	}

	private double dx;

	private double dy;

	private Units firstPercentageX;

	private Units firstPercentageY;

	/**
	 * How many samples constitute a gesture?
	 */
	private int gestureThreshold = 1;

	private Units lastPercentageX;

	private Units lastPercentageY;

	private int numSamples;

	/**
	 * @see edu.stanford.hci.r3.events.EventHandler#handleEvent(edu.stanford.hci.r3.events.PenEvent)
	 */
	public void handleEvent(PenEvent event) {
		PercentageCoordinates percentageLocation = event.getPercentageLocation();
		if (event.isPenDown()) {
			firstPercentageX = percentageLocation.getActualValueInXDirection();
			firstPercentageY = percentageLocation.getActualValueInYDirection();
			numSamples = 0;
		} else if (event.isPenUp()) {
			// register a gesture on pen up
			// only if we get more than N samples do we register a gesture
			if (numSamples > gestureThreshold) {
				dx = lastPercentageX.getValue() - firstPercentageX.getValue();
				dy = lastPercentageY.getValue() - firstPercentageY.getValue();

				// calculate the angle, and decide the compass direction
				double theta = Math.atan2(dy, dx);
				double testTheta = theta + Math.PI / 8; // makes it easier to binary search

				final double PI_4 = Math.PI / 4;
				final double PI_2 = Math.PI / 2;
				final double THREE_PI_4 = 3 * Math.PI / 4;

				// Do a ~binary search! =)
				if (testTheta > 0) { // E, NE, N, NW, W
					if (testTheta < PI_2) { // NE or E
						if (testTheta < PI_4) { // E
							handleMark(event, GestureDirection.E);
						} else { // NE
							handleMark(event, GestureDirection.NE);
						}
					} else { // N, NW, W... anything greater than PI/2
						if (testTheta < THREE_PI_4) { // N
							handleMark(event, GestureDirection.N);
						} else if (testTheta < Math.PI) { // NW
							handleMark(event, GestureDirection.NW);
						} else { // W
							handleMark(event, GestureDirection.W);
						}
					}
				} else { // SE, S, SW, W
					if (testTheta > -PI_2) { // S, SE
						if (testTheta > -PI_4) {
							handleMark(event, GestureDirection.SE);
						} else {
							handleMark(event, GestureDirection.S);
						}
					} else { // SW, W
						if (testTheta > -THREE_PI_4) {
							handleMark(event, GestureDirection.SW);
						} else {
							handleMark(event, GestureDirection.W);
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
	public void handleMark(PenEvent e, GestureDirection dir) {
		System.out.println("Mark Direction: " + dir);
	}

}
