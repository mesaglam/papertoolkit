package edu.stanford.hci.r3.events.filters;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.ContentFilter;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkSample;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.units.Pixels;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;

/**
 * <p>
 * Captures ink strokes, and allows access to them on demand. Notifies listeners every time a stroke
 * is written.
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
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class InkCollector extends ContentFilter {

	/**
	 * <p>
	 * Notifies listeners that new ink has arrived.
	 * </p>
	 */
	private class InkNotifier implements Runnable {

		private boolean doNotNotify;

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
			// System.out.println(currentStrokeSamples.size() + " samples in this stroke.");
			strokes.add(new InkStroke(currentStrokeSamples, DOTS));
			notifyAllListenersOfNewContent();
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
	 * For unit conversions...
	 */
	private static final Pixels PIXELS = new Pixels();

	/**
	 * Samples that compose an ink stroke...
	 */
	private List<InkSample> currentStrokeSamples = new ArrayList<InkSample>();

	private long currPenDownTime;

	private InkNotifier lastInkNotifier;

	private long lastPenUpTime;

	/**
	 * For tracking ink that we have retrieved.
	 */
	private int newInkMarker = 0;

	/**
	 * This should be synchronized, as multiple threads are working on it.
	 */
	private List<InkStroke> strokes = Collections.synchronizedList(new ArrayList<InkStroke>());

	private long timeDiffBetweenPenUpAndPenDown;

	/**
	 * Clear the buffers.
	 */
	public void clear() {
		strokes.clear();
		currentStrokeSamples.clear();
		newInkMarker = 0;
	}

	/**
	 * 
	 * @see edu.stanford.hci.r3.events.ContentFilter#filterEvent(edu.stanford.hci.r3.events.PenEvent)
	 */
	@Override
	public void filterEvent(PenEvent event) {
		final PercentageCoordinates percentageLocation = event.getPercentageLocation();
		final Units x = percentageLocation.getX();
		final Units y = percentageLocation.getY();
		final long timestamp = event.getTimestamp();

		// collect the ink strokes
		if (event.isPenDown()) {
			currPenDownTime = System.currentTimeMillis();
			timeDiffBetweenPenUpAndPenDown = currPenDownTime - lastPenUpTime;
			// DebugUtils.println("The pen was up for " + timeDiffBetweenPenUpAndPenDown + "
			// milliseconds");

			// say 20 milliseconds (1/50 of a second) is probably faster than a human can go up and
			// down =)
			if (timeDiffBetweenPenUpAndPenDown > MAX_MILLIS_FOR_PEN_ERROR /* millis */) {
				// not a pen error!
				currentStrokeSamples.clear();
				currentStrokeSamples.add(new InkSample(x.getValueIn(PIXELS), y.getValueIn(PIXELS),
						128, timestamp));
				lastInkNotifier = null; // let it run
			} else {
				// "kill" the last notifier if possible
				lastInkNotifier.setDoNotNotify(true);

				// we'll assume this is a pen manufacturing error (jitter)!
				currentStrokeSamples.add(new InkSample(x.getValueIn(PIXELS), y.getValueIn(PIXELS),
						128, timestamp));
			}
		} else if (event.isPenUp()) {
			lastPenUpTime = System.currentTimeMillis();
			lastInkNotifier = new InkNotifier();

			// notify after a short delay, because we may actually update the content if there was a
			// pen error
			new Thread(lastInkNotifier).start();

			// System.out.println("Collected " + strokes.size() + " strokes so far.");
		} else { // regular sample
			currentStrokeSamples.add(new InkSample(x.getValueIn(PIXELS), y.getValueIn(PIXELS), 128,
					timestamp));
		}
	}

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
	 * @param xmlFile
	 */
	public void saveInkToXMLFile(File xmlFile) {
		new Ink(strokes).saveAsXMLFile(xmlFile);
	}

	/**
	 * @see edu.stanford.hci.r3.events.ContentFilter#toString()
	 */
	@Override
	public String toString() {
		return "Ink Collector [" + strokes.size() + " strokes]";
	}
}
