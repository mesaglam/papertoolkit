package edu.stanford.hci.r3.events.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.hci.r3.events.ContentFilter;
import edu.stanford.hci.r3.events.PenEvent;
import edu.stanford.hci.r3.pen.handwriting.HandwritingRecognitionService;
import edu.stanford.hci.r3.pen.ink.InkSample;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.units.PatternDots;
import edu.stanford.hci.r3.units.Units;
import edu.stanford.hci.r3.units.coordinates.PercentageCoordinates;
import edu.stanford.hci.r3.util.DebugUtils;

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

	private static int instanceCount;

	/**
	 * 
	 */
	public static int getInstanceCount() {
		return instanceCount;
	}

	/**
	 * Samples that compose an ink stroke...
	 */
	private List<InkSample> currentStrokeSamples = new ArrayList<InkSample>();

	/**
	 * This is the client that will connect to the handwriting recognition server...
	 */
	private HandwritingRecognitionService recognizerBridge;

	/**
	 * This should be synchronized, as multiple threads are working on it.
	 */
	private List<InkStroke> strokes = Collections.synchronizedList(new ArrayList<InkStroke>());

	/**
	 * 
	 */
	public HandwritingRecognizer() {
		// register that at least one instance exists...
		// we will check this at application start, and if true, we'll have to run the handwriting recognition
		// server
		instanceCount++;

		// create a RecognizerBridge for this local content filter...
		recognizerBridge = HandwritingRecognitionService.getInstance();
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
					.add(new InkSample(x.getValueInPixels(), y.getValueInPixels(), 128, timestamp));
		} else if (event.isPenUp()) {
			strokes.add(new InkStroke(currentStrokeSamples, DOTS));
			notifyAllListenersOfNewContent();
			System.out.println("Collected " + strokes.size() + " strokes so far.");
		} else { // regular sample
			currentStrokeSamples
					.add(new InkSample(x.getValueInPixels(), y.getValueInPixels(), 128, timestamp));
		}
	}

	/**
	 * @return
	 */
	public String recognizeHandwriting() {
		String result = recognizerBridge.recognizeHandwriting("[[helllooo world]]");
		DebugUtils.println(result);
		return result;
	}

	@Override
	public String toString() {
		return "Handwriting Recognizer";
	}

}
