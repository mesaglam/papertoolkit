package edu.stanford.hci.r3.events.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.hci.r3.events.ContentFilter;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.pen.PenSample;
import edu.stanford.hci.r3.pen.handwriting.HandwritingRecognitionService;
import edu.stanford.hci.r3.pen.ink.Ink;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;

/**
 * <p>
 * Produces ASCII text from input samples. Listeners can be notified whenever we have enough text.
 * Unimplemented.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class HandwritingRecognizer extends ContentFilter {

	/**
	 * For interpreting the samples.
	 */
	private static final PatternDots DOTS = new PatternDots();

	/**
	 * Samples that compose an ink stroke...
	 */
	private List<PenSample> currentStrokeSamples = new ArrayList<PenSample>();

	/**
	 * This is the client that will connect to the handwriting recognition server...
	 */
	private HandwritingRecognitionService recognizerService;

	/**
	 * This should be synchronized, as multiple threads are working on it.
	 */
	private List<InkStroke> strokes = Collections.synchronizedList(new ArrayList<InkStroke>());

	/**
	 * 
	 */
	public HandwritingRecognizer() {
		recognizerService = HandwritingRecognitionService.getInstance();
	}

	/**
	 * 
	 */
	public void clear() {
		strokes.clear();
	}

	/**
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
			// not a pen error!
			currentStrokeSamples.clear();
			currentStrokeSamples
					.add(new PenSample(x.getValueInPixels(), y.getValueInPixels(), 128, timestamp));
		} else if (event.isPenUp()) {
			strokes.add(new InkStroke(currentStrokeSamples, DOTS));
			notifyAllListenersOfNewContent();
			// System.out.println("Collected " + strokes.size() + " strokes so far.");
		} else { // regular sample
			currentStrokeSamples
					.add(new PenSample(x.getValueInPixels(), y.getValueInPixels(), 128, timestamp));
		}
	}

	/**
	 * @return
	 */
	public String recognizeHandwriting() {
		final Ink ink = new Ink(strokes);
		final String xml = ink.getAsXML(false /* no separator lines */);
		final String result = recognizerService.recognizeHandwriting(xml);
		return result;
	}

	/**
	 * @return a list of the top ten recognized results (including the top one, at position 0)
	 */
	public List<String> recognizeHandwritingWithAlternatives() {
		recognizeHandwriting();
		return recognizerService.getAlternatives();
	}

	@Override
	public String toString() {
		return "Handwriting Recognizer";
	}

}
