package edu.stanford.hci.r3.examples.pdf;

import org.jpedal.examples.simpleviewer.SimpleViewer;

import edu.stanford.hci.r3.util.WindowUtils;

/**
 * <p>
 * This software is distributed under the <a href="http://hci.stanford.edu/research/copyright.txt">
 * BSD License</a>.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * Demonstrates using the JPedal SimpleViewer. By default, the images will look blurry. Upgrading
 * JAI with the higher performance native libraries will probably help.
 */
public class JPedalSimpleViewer {
	public static void main(String[] args) {
		WindowUtils.setNativeLookAndFeel();
		SimpleViewer viewer = new SimpleViewer();
		viewer.setupViewer("testData/ButterflyNetCHI2006.pdf");
	}
}
