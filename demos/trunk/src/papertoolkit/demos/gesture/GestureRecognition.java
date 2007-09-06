package papertoolkit.demos.gesture;

import papertoolkit.pen.Pen;
import papertoolkit.pen.gesture.dollar.DollarRecognizer;
import papertoolkit.pen.gesture.dollar.DollarRecognizer.RecognitionResult;
import papertoolkit.pen.ink.InkStroke;
import papertoolkit.pen.streaming.listeners.PenStrokeListener;
import papertoolkit.util.DebugUtils;

public class GestureRecognition {
	public static void main(String[] args) {
		final DollarRecognizer dollarRecognizer = new DollarRecognizer();
		
		// capture ink from a pen
		Pen pen = new Pen();
		pen.addLivePenListener(new PenStrokeListener() {
			public void penStroke(InkStroke stroke) {
				RecognitionResult result = dollarRecognizer.recognize(stroke);
				DebugUtils.println(result);
			}
		});
		pen.startLiveMode();
	}
}
