package edu.stanford.hci.r3.pen.handwriting;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.pen.Pen;
import edu.stanford.hci.r3.pen.debug.DebuggingPenListener;
import edu.stanford.hci.r3.pen.streaming.PenListener;
import edu.stanford.hci.r3.pen.streaming.PenSample;
import edu.stanford.hci.r3.util.DebugUtils;

/**
 * <p>
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class HandwritingCaptureApp extends Application {

	private Pen pen;

	public HandwritingCaptureApp() {
		super("Handwriting Capture");
		addPen(getPen());

	}

	/**
	 * @return
	 */
	public Pen getPen() {
		if (pen == null) {
			pen = new Pen("Main Pen");
			pen.addLivePenListener(new PenListener() {

				public void penDown(PenSample sample) {

				}

				public void penUp(PenSample sample) {

				}

				public void sample(PenSample sample) {
					if ((topLeft != null) && (bottomRight != null)) {
						double x = sample.getX();
						double y = sample.getY();
						double tlX = topLeft.getX();
						double tlY = topLeft.getY();
						double brx = bottomRight.getX();
						double brY = bottomRight.getY();

						DebugUtils.println("X: " + x / (brx - tlX) + "  Y: " + (y / brY - tlY));
					}
				}
			});
		}
		return pen;
	}

	private PenSample topLeft;

	private PenSample bottomRight;

	public void addCalibrationHandlers() {
		final Pen pen = getPen();
		pen.addLivePenListener(new PenListener() {
			public void penDown(PenSample sample) {
				if (topLeft == null) {
					topLeft = sample;
					DebugUtils.println("Top Left Point is now set to " + topLeft);
				} else if (bottomRight == null) {
					bottomRight = sample;
					DebugUtils.println("Bottom Right Point is now set to " + bottomRight);
				} else {
					final PenListener listener = this;
					// We must modify the listeners from an external thread, as we are
					// currently iterating through it
					// This will happen after we have released the lock
					new Thread(new Runnable() {
						public void run() {
							pen.removeLivePenListener(listener);
						}
					}).start();
				}
			}

			public void penUp(PenSample sample) {

			}

			public void sample(PenSample sample) {

			}
		});
	}
}
