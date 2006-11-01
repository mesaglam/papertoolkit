package edu.stanford.hci.r3.pattern.calibrate;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.ink.InkStroke;
import edu.stanford.hci.r3.pen.streaming.listeners.PenListener;
import edu.stanford.hci.r3.pen.streaming.listeners.PenStrokeListener;
import edu.stanford.hci.r3.util.DebugUtils;
import edu.stanford.hci.r3.util.MathUtils;

/**
 * <p>
 * This class helps us create a mapping between the streamed (physical) coordinates and the batched (logical)
 * coordinates for Anoto digital pens. This is important to allow us to mix streaming with batched event
 * handling.
 * </p>
 * <p>
 * Calibrate the Clock First, then Calibrate the X & Y... 
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class CoordinateCalibration {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CoordinateCalibration();
	}

	private Application app;

	private PenListener listener;

	// an app that takes two strokes and records them

	// find the orientation of the two strokes

	// find the cross point of the two strokes, and record this streamed coordinate

	// figure out the same thing with the batched ink...

	private Pen pen;

	/**
	 * 
	 */
	public CoordinateCalibration() {
		app = new Application("Calibration");
		app.addPen(getPen());
		PaperToolkit.setUseHandwritingRecognitionServer(false);
		PaperToolkit.runApplication(app);
	}

	/**
	 * @return
	 */
	private Pen getPen() {
		if (pen == null) {
			pen = new Pen();
			pen.addLivePenListener(getPenListener());
		}
		return pen;
	}

	/**
	 * @return
	 */
	private PenListener getPenListener() {
		if (listener == null) {
			listener = new PenStrokeListener() {
				@Override
				public void penStroke(InkStroke stroke) {
					DebugUtils.println("New " + stroke);
					double[] samplesX = stroke.getXSamples();
					double[] samplesY = stroke.getYSamples();
					DebugUtils.println("STDEV X: " + MathUtils.standardDeviation(samplesX));
					DebugUtils.println("STDEV Y: " + MathUtils.standardDeviation(samplesY));
				}
			};
		}
		return listener;
	}
}
