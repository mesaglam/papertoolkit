package edu.stanford.hci.r3.demos.eventreplay;

import edu.stanford.hci.r3.Application;
import edu.stanford.hci.r3.PaperToolkit;

/**
 * <p>
 * Simple demo of event saving and replay...
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public class BasicSaveAndReplay extends Application {

	public BasicSaveAndReplay() {
		super("Basic Save and Replay");
		
	}

	public static void main(String[] args) {
		PaperToolkit toolkit = new PaperToolkit();
		toolkit.startApplication(new BasicSaveAndReplay());
	}
}
