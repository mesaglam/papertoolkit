package edu.stanford.hci.r3.events.filters;

import java.io.File;
import java.util.ArrayList;
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

	/**
	 * Clear the buffers.
	 */
	public void clear() {
		strokes.clear();
		currentStrokeSamples.clear();
		newInkMarker = 0;
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
			currentStrokeSamples.clear();
			currentStrokeSamples.add(new InkSample(x.getValueIn(PIXELS), y.getValueIn(PIXELS), 128,
					timestamp));
		} else if (event.isPenUp()) {
			// System.out.println(currentStrokeSamples.size() + " samples in this stroke.");
			strokes.add(new InkStroke(currentStrokeSamples, DOTS));
			notifyAllListenersOfNewContent();
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
	 * @return timestamp that last stroke was completed, in milliseconds, or -1 if there are no strokes.
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
