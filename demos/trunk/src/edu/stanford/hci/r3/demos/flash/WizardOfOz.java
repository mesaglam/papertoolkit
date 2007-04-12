package edu.stanford.hci.r3.demos.flash;

import edu.stanford.hci.r3.PaperToolkit;

/**
 * <p>
 * Examples of how you might use R3 to create a Wizard of Oz interface to capture pen input, and provide
 * feedback to an external device that looks like an advanced AI has "recognized" the text and drawings.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class WizardOfOz {

	private PaperToolkit paperToolkit;

	public WizardOfOz() {
		paperToolkit = new PaperToolkit();
		// DebugUtils.println(paperToolkit.getProperty("unusedProperty"));
	}

	public static void main(String[] args) {
		new WizardOfOz();
	}
}
