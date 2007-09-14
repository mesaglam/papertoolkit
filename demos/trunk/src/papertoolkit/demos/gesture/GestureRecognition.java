package papertoolkit.demos.gesture;

import papertoolkit.pen.Pen;
import papertoolkit.pen.gesture.dollar.DollarRecognizer;
import papertoolkit.pen.gesture.dollar.DollarRecognizer.RecognitionResult;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.pen.streaming.listeners.PenStrokeListener;
import papertoolkit.util.DebugUtils;

/**
 * <p>
 * Uses the Dollar Recognizer directly, with the low level PenListeners...
 * This class cannot use the monitoring service, because it doesn't instantiate the toolkit...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class GestureRecognition {
	public static void main(String[] args) {
		final DollarRecognizer dollarRecognizer = new DollarRecognizer();

		// capture ink from a pen
		Pen pen = new Pen();
		pen.addLivePenListener(new PenStrokeListener() {
			public void strokeArrived(InkStroke stroke) {
				RecognitionResult result = dollarRecognizer.recognize(stroke);
				DebugUtils.println(result);
			}
		});
		pen.startLiveMode();
	}
}
