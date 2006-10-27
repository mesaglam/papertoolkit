package edu.stanford.hci.r3.pen.debug;

import edu.stanford.hci.r3.pen.Pen;

/**
 * <p>
 * Use this to display Pen Coordinates to the console. Useful for debugging. If you get a message
 * like: Port COM5 not found, You may want to make sure that you have JavaCOMM installed.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PenCoordinateDebugger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Pen pen = new Pen();
		pen.startLiveMode();
		pen.addLivePenListener(new DebuggingPenListener());
	}
}
