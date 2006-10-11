package edu.stanford.hci.r3.events.filters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.events.ContentFilter;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.pen.ink.*;
import edu.stanford.hci.r3.units.*;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * Captures ink strokes, and allows access to them on demand. Notifies listeners every time a stroke
 * is written.
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
	 * 
	 */
	private static final PatternDots DOTS = new PatternDots();

	private static final Pixels PIXELS = new Pixels();

	/**
	 * 
	 */
	private List<InkSample> currentStrokeSamples = new ArrayList<InkSample>();

	/**
	 * For tracking ink that we have retrieved.
	 */
	private int newInkMarker = 0;

	/**
	 * 
	 */
	private List<InkStroke> strokes = new ArrayList<InkStroke>();

	private long lastPenUpTime;

	private long currPenDownTime;

	private long diff;

	private InkNotifier lastInkNotifier;

	/**
	 * Clear the buffers.
	 */
	public void clear() {
		strokes.clear();
		currentStrokeSamples.clear();
		newInkMarker = 0;
	}

	private static final int MILLIS_TO_DELAY = 20;

	private class InkNotifier implements Runnable {

		private boolean doNotNotify;

		public InkNotifier() {

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
	 * @see edu.stanford.hci.r3.events.ContentFilter#filterEvent(edu.stanford.hci.r3.events.PenEvent)
	 */
	@Override
	public void filterEvent(PenEvent event) {
		PercentageCoordinates percentageLocation = event.getPercentageLocation();
		Units x = percentageLocation.getX();
		Units y = percentageLocation.getY();
		long timestamp = event.getTimestamp();

		// collect the ink strokes
		if (event.isPenDown()) {
			currPenDownTime = System.currentTimeMillis();
			diff = currPenDownTime - lastPenUpTime;
			DebugUtils.println(diff + " in milliseconds");

			// say 20 milliseconds (1/50 of a second) is probably faster than a human can go up and
			// down =)
			if (diff > 20 /* millis */) {
				currentStrokeSamples.clear();
				currentStrokeSamples.add(new InkSample(x.getValueIn(PIXELS), y.getValueIn(PIXELS), 128,
						timestamp));
				lastInkNotifier = null; // let it run
			} else {
				currentStrokeSamples.add(new InkSample(x.getValueIn(PIXELS), y.getValueIn(PIXELS), 128,
						timestamp));
				lastInkNotifier.setDoNotNotify(true); // let it run
			}
		} else if (event.isPenUp()) {
			lastPenUpTime = System.currentTimeMillis();

			lastInkNotifier = new InkNotifier();
			// notify after a short delay, because we may actually update the content
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
		Ink newInk = new Ink(new ArrayList<InkStroke>(strokes.subList(newInkMarker, strokes.size())));
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
	public long getLastTimestamp() {
		if (strokes != null && strokes.size() >= 1) {
			return strokes.get(strokes.size() - 1).getLastTimestamp();
		} else {
			return -1;
		}
	}

	/**
	 * @param xmlFile
	 */
	public void saveToXMLFile(File xmlFile) {
		PaperToolkit.toXML(this, xmlFile);
	}

	/**
	 * @see edu.stanford.hci.r3.events.ContentFilter#toString()
	 */
	@Override
	public String toString() {
		return "Ink Collector [" + strokes.size() + " strokes]";
	}
}
