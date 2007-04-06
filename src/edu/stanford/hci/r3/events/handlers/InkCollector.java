package edu.stanford.hci.r3.events.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.stanford.hci.r3.events.EventHandler;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;
import edu.stanford.hci.r3.util.MathUtils;

/**
 * <p>
 * Captures ink strokes, and allows access to them on demand. Notifies listeners every time a stroke
 * is written. We can set a flag that tells it to notify the listeners every time the pen moves a
 * sufficient distance...
 * </p>
 * <p>
 * TODO: This class contains some filtering code to eliminate false Pen Ups, due to the fault of the
 * streaming digital pen. Should this filtering be done earlier? Should it be an option? Clearly, an
 * implementer of a ContentFilter should not need to manually filter events... =\
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * TODO: Add a scale factor here??? Or maybe a scale factor somewhere in the event pipeline? Or
 * should we do it later on?
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class InkCollector extends EventHandler {

	/**
	 * <p>
	 * Notifies listeners that new ink has arrived.
	 * </p>
	 */
	private class InkNotifier implements Runnable {

		private boolean doNotNotify;

		private InkStroke lastTempStroke;

		private List<PenSample> strokeSamples;

		public InkNotifier(List<PenSample> currentStrokeSamples, InkStroke tempStroke) {
			strokeSamples = currentStrokeSamples;
			lastTempStroke = tempStroke;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				Thread.sleep(MILLIS_TO_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (doNotNotify) {
				// someone told us to cancel
				return;
			}

			if (lastTempStroke != null) {
				strokes.remove(lastTempStroke);
			}

			// System.out.println(currentStrokeSamples.size() + " samples in
			// this stroke.");
			addStrokeAndNotifyListeners(strokeSamples);
		}

		/**
		 * @param b
		 */
		public void setDoNotNotify(boolean b) {
			doNotNotify = b;
		}
	}

	/**
	 * For interpreting the samples.
	 */
	private static final PatternDots DOTS = new PatternDots();

	/**
	 * Number of milliseconds used to filter out bad pen events.
	 */
	private static final int MAX_MILLIS_FOR_PEN_ERROR = 20;

	/**
	 * The notifier will wait for this many milliseconds before it notifies all listeners of the new
	 * ink content. Ideally, this number should be a little longer than MAX_MILLIS_FOR_PEN_ERROR.
	 */
	private static final int MILLIS_TO_DELAY = 21;

	/**
	 * Samples that compose an ink stroke...
	 */
	private List<PenSample> currentStrokeSamples = new ArrayList<PenSample>();

	private long currPenDownTime;

	private double distanceThreshold = 0;

	/**
	 * How far has the pen moved since the pen down?
	 */
	private double distanceTraveled = 0;

	private InkNotifier lastInkNotifier;

	/**
	 * The System time of the most recent pen up event.
	 */
	private long lastPenUpTime;

	private double lastXForDistanceMeasurements;

	private double lastYForDistanceMeasurements;

	private InkStroke mostRecentlyAddedStroke;

	private InkStroke mostRecentlyAddedTemporaryStroke;

	/**
	 * For tracking ink that we have retrieved.
	 */
	private int newInkMarker = 0;

	/**
	 * If true, we will notify our listeners on EVERY SINGLE SAMPLE. An alternate approach would be
	 * to notify after the pen has moved sufficiently far...
	 */
	private boolean notifyAfterEnoughDistance = false;

	/**
	 * This should be synchronized, as multiple threads are working on it.
	 */
	private List<InkStroke> strokes = Collections.synchronizedList(new ArrayList<InkStroke>());

	private long timeDiffBetweenPenUpAndPenDown;

	public InkCollector() {

	};

	/**
	 * @param strokeSamples
	 */
	private synchronized void addStrokeAndNotifyListeners(List<PenSample> strokeSamples) {
		mostRecentlyAddedStroke = new InkStroke(strokeSamples, DOTS);
		strokes.add(mostRecentlyAddedStroke);
		contentArrived();
	}

	/**
	 * @param strokeSamples
	 */
	private synchronized void addStrokeTemporarilyAndNotifyListeners(List<PenSample> strokeSamples) {
		if (mostRecentlyAddedTemporaryStroke != null) {
			strokes.remove(mostRecentlyAddedTemporaryStroke);
		}
		mostRecentlyAddedTemporaryStroke = new InkStroke(strokeSamples, DOTS);
		strokes.add(mostRecentlyAddedTemporaryStroke);
		contentArrived();
	}

	/**
	 * Clear the buffers.
	 */
	public void clear() {
		strokes.clear();
		currentStrokeSamples.clear();
		newInkMarker = 0;
	}

	public abstract void contentArrived();

	/**
	 * @return list of ALL the pen strokes.
	 */
	public Ink getInk() {
		return new Ink(new ArrayList<InkStroke>(strokes));
	}

	/**
	 * @return
	 */
	public Ink getNewInkOnly() {
		Ink newInk = new Ink(
				new ArrayList<InkStroke>(strokes.subList(newInkMarker, strokes.size())));
		newInkMarker = strokes.size();
		return newInk;
	}

	/**
	 * @return
	 */
	public int getNumStrokesCollected() {
		return strokes.size();
	}

	/**
	 * @return timestamp that last stroke was completed, in milliseconds, or -1 if there are no
	 *         strokes.
	 */
	public long getTimestampOfMostRecentInkStroke() {
		if (strokes != null && strokes.size() >= 1) {
			return strokes.get(strokes.size() - 1).getLastTimestamp();
		} else {
			return -1;
		}
	}

	/**
	 * @return
	 */
	public Date getTimestampOfMostRecentPenUp() {
		return new Date(lastPenUpTime);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.events.EventHandler#handleEvent(edu.stanford.hci.r3.events.PenEvent)
	 */
	@Override
	public void handleEvent(PenEvent event) {

		final PercentageCoordinates percentageLocation = event.getPercentageLocation();
		final Units xPct = percentageLocation.getX();
		final Units yPct = percentageLocation.getY();
		final long timestamp = event.getTimestamp();

		// collect the ink strokes in default units? (i.e., PatternDots?)
		// the thing that renders the ink should decide how to scale it
		final double xDots = xPct.getValueInPatternDots();
		final double yDots = yPct.getValueInPatternDots();

		if (event.isPenDown()) {
			currPenDownTime = System.currentTimeMillis();
			timeDiffBetweenPenUpAndPenDown = currPenDownTime - lastPenUpTime;
			// DebugUtils.println("The pen was up for " +
			// timeDiffBetweenPenUpAndPenDown + "
			// milliseconds");

			// 20 milliseconds (1/50 of a second) is probably faster than a
			// human can go up and down
			if (timeDiffBetweenPenUpAndPenDown > MAX_MILLIS_FOR_PEN_ERROR /* millis */) {
				// not a pen error!

				// let the last ink notifier run
				lastInkNotifier = null;

				// reset the distance traveled
				distanceTraveled = 0;
				lastXForDistanceMeasurements = xDots;
				lastYForDistanceMeasurements = yDots;

				// We should start a new stroke!
				currentStrokeSamples = new ArrayList<PenSample>();
				currentStrokeSamples.add(new PenSample(xDots, yDots, 128, timestamp));
			} else {
				// we'll assume this is a pen manufacturing error (jitter)!

				// "kill" the last notifier if possible (best effort)
				lastInkNotifier.setDoNotNotify(true);
				lastInkNotifier = null;

				// add this sample back to the current stroke
				currentStrokeSamples.add(new PenSample(xDots, yDots, 128, timestamp));
			}
		} else if (event.isPenUp()) {
			// the pen is lifted from the page

			// record the time of the pen up
			lastPenUpTime = System.currentTimeMillis();

			// we need to notify our listeners
			// notify after a short delay, because we may actually update the
			// current stroke
			// if there is a pen error
			lastInkNotifier = new InkNotifier(currentStrokeSamples,
					mostRecentlyAddedTemporaryStroke);
			new Thread(lastInkNotifier).start();

			// System.out.println("Collected " + strokes.size() + " strokes so
			// far.");
		} else { // regular sample
			currentStrokeSamples.add(new PenSample(xDots, yDots, 128, timestamp));

			// are we supposed to notify after enough distance?
			if (notifyAfterEnoughDistance) {
				// assume zero distance for now...
				distanceTraveled += MathUtils.distance(xDots, yDots, //
						lastXForDistanceMeasurements, lastYForDistanceMeasurements);
				lastXForDistanceMeasurements = xDots;
				lastYForDistanceMeasurements = yDots;

				if (distanceTraveled > distanceThreshold) {
					addStrokeTemporarilyAndNotifyListeners(currentStrokeSamples);
				}
			}
		}
	}

	/**
	 * @param xmlFile
	 */
	public void saveInkToXMLFile(File xmlFile) {
		new Ink(strokes).saveToXMLFile(xmlFile);
	}

	/**
	 * @param notifyAfterThisMuchPenMovement
	 * @deprecated do not use this yet... it's a bit slow
	 */
	public void setNotifyDistance(Units notifyAfterThisMuchPenMovement) {
		if (notifyAfterThisMuchPenMovement == null) {
			distanceThreshold = 0;
			notifyAfterEnoughDistance = false;
		} else {
			distanceThreshold = notifyAfterThisMuchPenMovement.getValueInPatternDots();
			notifyAfterEnoughDistance = true;
		}
	}

	/**
	 * @see edu.stanford.hci.r3.events.ContentFilter#toString()
	 */
	@Override
	public String toString() {
		return "Ink Collector [" + strokes.size() + " strokes]";
	}
}
