package papertoolkit.demos.externallibs.pdf;

import org.jpedal.examples.simpleviewer.SimpleViewer;

import papertoolkit.util.WindowUtils;


/**
 * <p>
 * Demonstrates using the JPedal SimpleViewer. By default, the images will look blurry. Upgrading JAI with the
 * higher performance native libraries will probably help.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>.</span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class JPedalSimpleViewer {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WindowUtils.setNativeLookAndFeel();
		SimpleViewer viewer = new SimpleViewer();
		viewer.setupViewer("data/TestFiles/ButterflyNetCHI2006.pdf");
	}
}
