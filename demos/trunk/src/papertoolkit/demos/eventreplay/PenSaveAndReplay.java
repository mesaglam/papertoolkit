package papertoolkit.demos.eventreplay;

import papertoolkit.pen.Pen;
import papertoolkit.pen.debug.DebuggingPenListener;

/**
 * <p>
 * This handles saving and replaying at the lowest level, the pen input device. Basically, when you create a
 * new Pen, it will start logging all pen events to files in the eventData/ directory. You can optionally ask
 * the pen to load and replay these events, as if it were physically happening in real time.
 * 
 * This will also work for higher level events, if we can replay the low level pen input.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * @lastWorkedOn 10 September 2007
 */
public class PenSaveAndReplay {

	/**
	 * 
	 */
	private Pen pen;

	/**
	 * 
	 */
	public PenSaveAndReplay() {
		pen = new Pen(); // local pen
		// pen = new Pen("Remote Pen", "solaria.stanford.edu", 11104); // remote pen
		pen.addLivePenListener(new DebuggingPenListener(pen));
		pen.startLiveMode();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PenSaveAndReplay();
	}
}
