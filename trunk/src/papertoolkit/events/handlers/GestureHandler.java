package papertoolkit.events.handlers;

import papertoolkit.events.PenEvent;
import papertoolkit.pen.gesture.dollar.DollarRecognizer;
import papertoolkit.pen.gesture.dollar.DollarRecognizer.RecognitionResult;
import papertoolkit.pen.ink.InkStroke;

/**
 * <p>
 * Uses the $1 Gesture Recognizer. (Wobbrock, et al.)
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public abstract class GestureHandler extends StrokeHandler {

	private DollarRecognizer dollarRecognizer;

	public GestureHandler() {
		dollarRecognizer = new DollarRecognizer();
	}

	/* (non-Javadoc)
	 * @see papertoolkit.events.handlers.StrokeHandler#toString()
	 */
	public String toString() {
		return "DollarGestureHandler";
	}

	// add template
	// add example to template
	// remove template

	/*
	 * (non-Javadoc)
	 * 
	 * @see papertoolkit.events.handlers.StrokeHandler#strokeArrived(papertoolkit.events.PenEvent,
	 *      papertoolkit.pen.ink.InkStroke)
	 */
	public void strokeArrived(PenEvent lastSample, InkStroke stroke) {
		RecognitionResult result = dollarRecognizer.recognize(stroke);
		gestureArrived(lastSample, result, stroke);
	}
	
	public abstract void gestureArrived(PenEvent lastSample, RecognitionResult result, InkStroke stroke);
}
