package edu.stanford.hci.r3;

import edu.stanford.hci.r3.util.WindowUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class PaperToolkit {

	/**
	 * 
	 */
	public PaperToolkit() {
		initializeSwing();
	}

	/**
	 * Sets up parameters for any Java Swing UI we need.
	 */
	private void initializeSwing() {
		WindowUtils.setNativeLookAndFeel();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
