package edu.stanford.hci.r3.pen;

import edu.stanford.hci.r3.pen.debug.DebuggingPenListener;

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
public class Test_Pen {

	public static void main(String[] args) {
		Pen p = new Pen("Remote Pen", "171.66.32.119");
		p.addLivePenListener(new DebuggingPenListener("Remote Pen"));
		p.startLiveMode();
	}
}
