package papertoolkit.events.handlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Timer;


import papertoolkit.events.EventHandler;
import papertoolkit.events.PenEvent;
import papertoolkit.pen.PenSample;
import papertoolkit.pen.handwriting.HandwritingRecognitionService;
import papertoolkit.pen.ink.Ink;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.units.PatternDots;
import papertoolkit.units.Units;
import papertoolkit.units.coordinates.PercentageCoordinates;

/**
 * <p>
 * Produces ASCII text from input samples. Listeners can be notified whenever we have enough text.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class HandwritingHandler extends EventHandler {

	/**
	 * For interpreting the samples.
	 */
	private static final PatternDots DOTS = new PatternDots();

	/**
	 * Samples that compose an ink stroke...
	 */
	private List<PenSample> currentStrokeSamples = new ArrayList<PenSample>();

	private Timer notificationTimer;

	/**
	 * This is the client that will connect to the handwriting recognition server...
	 */
	private HandwritingRecognitionService recognizerService;

	/**
	 * This should be synchronized, as multiple threads are working on it.
	 */
	private List<InkStroke> strokes = Collections.synchronizedList(new ArrayList<InkStroke>());

	/**
	 * Gets access to the HandwritingRecognitionService, which wraps the communication with the Handwriting
	 * Recognition Server.
	 */
	public HandwritingHandler() {
		recognizerService = HandwritingRecognitionService.getInstance();
		notificationTimer = new Timer(450, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notificationTimer.stop();
				contentArrived();
			}
		});
	}

	/**
	 * Clear the internal strokes storage.
	 */
	public void clear() {
		strokes.clear();
	}

	/**
	 * 
	 */
	public abstract void contentArrived();
	
	/**
	 * Capture Ink Strokes and Notify listeners when the user has paused writing. This is to make it more
	 * efficient to call recognizeHandwriting on every contentArrived() notification.
	 */
	public void handleEvent(PenEvent event) {
		final PercentageCoordinates percentageLocation = event.getPercentageLocation();
		final Units x = percentageLocation.getX();
		final Units y = percentageLocation.getY();
		final long timestamp = event.getTimestamp();

		// collect the ink strokes
		if (event.isTypePenDown()) {
			// not a pen error!
			currentStrokeSamples.clear();
			currentStrokeSamples
					.add(new PenSample(x.getValueInPixels(), y.getValueInPixels(), 128, timestamp));
			notificationTimer.stop(); // if the pen down happened within our ~350 ms, then we don't notify anyone
		} else if (event.isTypePenUp()) {
			strokes.add(new InkStroke(currentStrokeSamples, DOTS));
			notificationTimer.restart();
		} else { // regular sample
			currentStrokeSamples
					.add(new PenSample(x.getValueInPixels(), y.getValueInPixels(), 128, timestamp));
		}
	}

	/**
	 * @return the top-ranked ASCII translation for the ink strokes.
	 */
	public String recognizeHandwriting() {
		if (strokes.size() == 0) {
			// DebugUtils.println("Num Strokes to Recognize: " + strokes.size());
			return "";
		} else {
			final Ink ink = new Ink(strokes);
			final String xml = ink.toXMLString(false /* no separator lines */);
			final String result = recognizerService.recognizeHandwriting(xml);
			return result;
		}
	}

	/**
	 * @return a list of the top ten recognized results (including the top one, at position 0)
	 */
	public List<String> recognizeHandwritingWithAlternatives() {
		recognizeHandwriting();
		return recognizerService.getAlternatives();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.hci.r3.events.EventHandler#toString()
	 */
	public String toString() {
		return "Handwriting Recognizer: " + strokes.size() + " strokes.";
	}

}
